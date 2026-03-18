import { Injectable, NgZone } from '@angular/core';
import { JitsiMeetExternalAPI, JitsiMeetOptions } from '../../types/jitsi-meet';

export interface JitsiJoinOptions {
  displayName: string;
  email?: string;
  isLogoped: boolean;
  jwt?: string;
}

export type JitsiEventListener = (data: any) => void;

/**
 * Сервис для работы с Jitsi Meet IFrame API
 *
 * Использование:
 * 1. Вызвать joinRoom() для подключения к комнате
 * 2. Использовать методы управления (toggleAudio, toggleVideo, etc.)
 * 3. Вызвать dispose() при уничтожении компонента
 */
@Injectable({ providedIn: 'root' })
export class JitsiService {
  private api: JitsiMeetExternalAPI | null = null;
  private jitsiDomain: string;

  constructor(private ngZone: NgZone) {
    // По умолчанию используем публичный сервер Jitsi
    // Для локального сервера заменить на 'localhost:8000'
    this.jitsiDomain = 'meet.jit.si';
  }

  /**
   * Устанавливает домен Jitsi сервера
   */
  setJitsiDomain(domain: string): void {
    this.jitsiDomain = domain;
  }

  /**
   * Подключается к комнате Jitsi Meet
   *
   * @param containerId ID HTML элемента для вставки IFrame
   * @param roomName Имя комнаты
   * @param options Опции подключения
   */
  async joinRoom(
    containerId: string,
    roomName: string,
    options: JitsiJoinOptions
  ): Promise<void> {
    // Ждём загрузки скрипта Jitsi
    await this.loadJitsiScript();

    // Ждём появления контейнера (с таймаутом)
    const container = await this.waitForContainer(containerId);
    if (!container) {
      throw new Error(`Container #${containerId} not found after waiting`);
    }

    // Конфигурация для логопеда и пациента
    const configOverwrite = this.getConfigForRole(options.isLogoped);
    const interfaceConfigOverwrite = this.getInterfaceForRole(options.isLogoped);

    const jitsiOptions: JitsiMeetOptions = {
      roomName,
      width: '100%',
      height: '100%',
      parentNode: container,
      configOverwrite,
      interfaceConfigOverwrite,
      jwt: options.jwt,
      userInfo: {
        displayName: options.displayName,
        email: options.email
      },
      lang: 'ru'
    };

    this.api = new window.JitsiMeetExternalAPI!(this.jitsiDomain, jitsiOptions);

    // Подписываемся на события
    this.setupEventListeners();
  }

  /**
   * Ждёт появления контейнера в DOM
   */
  private async waitForContainer(containerId: string, timeoutMs: number = 5000): Promise<HTMLElement | null> {
    const startTime = Date.now();

    while (Date.now() - startTime < timeoutMs) {
      const container = document.getElementById(containerId);
      if (container) {
        console.log(`Container #${containerId} found`);
        return container;
      }
      // Ждём 100мс перед следующей проверкой
      await new Promise(resolve => setTimeout(resolve, 100));
    }

    console.error(`Container #${containerId} not found after ${timeoutMs}ms`);
    return null;
  }

  /**
   * Возвращает конфигурацию для роли пользователя
   */
  private getConfigForRole(isLogoped: boolean): Record<string, any> {
    return {
      // Общие настройки
      startWithAudioMuted: false,
      startWithVideoMuted: false,
      disableModeratorIndicator: !isLogoped,

      // Для логопеда: включаем все инструменты
      enableClosePage: isLogoped,
      disableLocalVideoFlip: !isLogoped,

      // Шумоподавление (важно для логопедии!)
      audioQuality: {
        stereo: false,
        opusMaxAverageBitrate: 28000
      },

      // Адаптивная сетка видео
      enableTileView: true,
      tileViewMaxColumns: isLogoped ? 4 : 2,

      // Для логопеда: показываем индикатор доминирующего говорящего
      DISABLE_DOMINANT_SPEAKER_INDICATOR: !isLogoped,

      // === ОТКЛЮЧАЕМ МОБИЛЬНОЕ ПРИЛОЖЕНИЕ ===
      // Правильный синтаксис для отключения баннера приложения
      deeplinking: {
        disabled: true
      },

      // === ПОКАЗЫВАТЬ ВИДЕО УЧАСТНИКОВ ВО ВРЕМЯ ДЕМОСТРАЦИИ ===
      // Включаем filmstrip (полоска с видео участников)
      enableFilmstrip: true,
      // Показываем filmstrip даже во время демонстрации экрана
      hideFilmstripOnScreenShare: false,
      // Позиция filmstrip: 'bottom', 'right', 'left'
      filmstripStripPosition: 'bottom',
      // Показывать видео локального участника в filmstrip
      localVideoFilmstrip: true,

      // === P2P НАСТРОЙКИ ===
      // P2P включён для лучшей производительности в локальной сети
      p2p: {
        enabled: true,
        // Используем STUN сервер Google
        stunServers: [
          { urls: 'stun:stun.l.google.com:19302' },
          { urls: 'stun:stun1.l.google.com:19302' }
        ],
        // Таймаут для P2P соединения
        iceTimeoutSeconds: 30
      },

      // === JVB НАСТРОЙКИ ===
      // Настройки для стабильности соединения через JVB
      iceTimeoutSeconds: 30,
    };
  }

  /**
   * Возвращает конфигурацию интерфейса для роли пользователя
   */
  private getInterfaceForRole(isLogoped: boolean): Record<string, any> {
    const baseButtons = [
      'microphone', 'camera', 'hangup', 'chat', 'raisehand'
    ];

    return {
      // Кастомизация тулбара
      TOOLBAR_BUTTONS: isLogoped
        ? [...baseButtons, 'desktop', 'settings', 'participants', 'tileview']
        : ['microphone', 'camera', 'hangup', 'raisehand'],

      // === СКРЫВАЕМ ЛОГОТИПЫ И ВОДЯНЫЕ ЗНАКИ ===
      SHOW_JITSI_WATERMARK: false,
      SHOW_BRAND_WATERMARK: false,
      SHOW_WATERMARK_FOR_GUESTS: false,
      SHOW_POWERED_BY: false,
      SHOW_DEEP_LINKING_IMAGE: false,

      // Убираем левый и правый вотермарки
      WATERMARK_LINK: '',
      CUSTOM_WATERMARK: '',

      // Отключаем предложение использовать мобильное приложение
      DISABLE_DEEP_LINKING: true,

      // Настройки сетки
      TILE_VIEW_MAX_COLUMNS: isLogoped ? 4 : 2,

      // Скрываем настройки для пациента
      SETTINGS_SECTIONS: isLogoped
        ? ['devices', 'language', 'moderator']
        : [],

      // Дополнительные настройки
      DISABLE_JOIN_LEAVE_NOTIFICATIONS: false,
      DISPLAY_WELCOME_PAGE: false,

      // Убираем нижний правый логотип
      FILM_STRIP_ONLY: false,
      RIGHT_WATERMARK: '',
      LEFT_WATERMARK: '',
    };
  }

  /**
   * Настраивает обработчики событий
   */
  private setupEventListeners() {
    if (!this.api) return;

    // Готово к закрытию
    this.api.addEventListener('readyToClose', (data: any) => {
      this.ngZone.run(() => {
        console.log('Jitsi: readyToClose', data);
      });
    });

    // Участник присоединился
    this.api.addEventListener('participantJoined', (data: any) => {
      this.ngZone.run(() => {
        console.log('Jitsi: participantJoined', data);
      });
    });

    // Участник покинул
    this.api.addEventListener('participantLeft', (data: any) => {
      this.ngZone.run(() => {
        console.log('Jitsi: participantLeft', data);
      });
    });

    // Демонстрация экрана
    this.api.addEventListener('screenSharingStatusChanged', (data: any) => {
      this.ngZone.run(() => {
        console.log('Jitsi: screenSharingStatusChanged', data);
      });
    });

    // Конференция присоединилась
    this.api.addEventListener('videoConferenceJoined', (data: any) => {
      this.ngZone.run(() => {
        console.log('✅ Jitsi: videoConferenceJoined', data);
      });
    });

    // ОШИБКА конференции
    this.api.addEventListener('videoConferenceLeft', (data: any) => {
      this.ngZone.run(() => {
        console.error('❌ Jitsi: videoConferenceLeft', data);
      });
    });

    // Изменение состояния аудио
    this.api.addEventListener('audioMuteChanged', (data: any) => {
      this.ngZone.run(() => {
        console.log('Jitsi: audioMuteChanged', data);
      });
    });

    // Изменение состояния видео
    this.api.addEventListener('videoMuteChanged', (data: any) => {
      this.ngZone.run(() => {
        console.log('Jitsi: videoMuteChanged', data);
      });
    });

    // Ошибки
    this.api.addEventListener('error', (data: any) => {
      this.ngZone.run(() => {
        console.error('❌ Jitsi: error', data);
      });
    });
  }

  // ==================== Методы управления ====================

  /**
   * Переключить состояние микрофона
   */
  toggleAudio(): void {
    this.api?.executeCommand('toggleAudio');
  }

  /**
   * Переключить состояние камеры
   */
  toggleVideo(): void {
    this.api?.executeCommand('toggleVideo');
  }

  /**
   * Переключить демонстрацию экрана
   */
  toggleScreenShare(): void {
    this.api?.executeCommand('toggleScreenShare');
  }

  /**
   * Завершить конференцию
   */
  hangup(): void {
    this.api?.executeCommand('hangup');
  }

  /**
   * Включить/выключить плиточный вид
   */
  setTileView(enabled: boolean): void {
    this.api?.executeCommand('setTileView', enabled);
  }

  /**
   * Установить качество видео
   */
  setVideoQuality(quality: 'low' | 'standard' | 'high'): void {
    this.api?.executeCommand('setVideoQuality', quality);
  }

  /**
   * Включить/выключить микрофон
   */
  setAudioMute(muted: boolean): void {
    this.api?.executeCommand('setAudioMute', muted);
  }

  /**
   * Включить/выключить камеру
   */
  setVideoMute(muted: boolean): void {
    this.api?.executeCommand('setVideoMute', muted);
  }

  /**
   * Открыть чат
   */
  openChat(): void {
    this.api?.executeCommand('openChat');
  }

  /**
   * Закрыть чат
   */
  closeChat(): void {
    this.api?.executeCommand('closeChat');
  }

  /**
   * Отправить сообщение другому участнику через data channel
   */
  sendEndpointMessage(to: string, data: any): void {
    this.api?.executeCommand('sendEndpointMessage', to, data);
  }

  /**
   * Отправить сообщение всем участникам
   */
  broadcastMessage(data: any): void {
    this.api?.executeCommand('sendEndpointMessage', '', data);
  }

  /**
   * Добавить слушатель событий
   */
  addEventListener(event: string, handler: JitsiEventListener): void {
    this.api?.addEventListener(event, handler);
  }

  /**
   * Удалить слушатель событий
   */
  removeEventListener(event: string, handler: JitsiEventListener): void {
    this.api?.removeEventListener(event, handler);
  }

  /**
   * Очищает ресурсы и удаляет IFrame
   */
  dispose(): void {
    if (this.api) {
      this.api.dispose();
      this.api = null;
    }
  }

  /**
   * Загружает скрипт Jitsi External API
   */
  private loadJitsiScript(): Promise<void> {
    return new Promise((resolve, reject) => {
      // Проверяем, загружен ли уже скрипт
      if (window.JitsiMeetExternalAPI) {
        resolve();
        return;
      }

      const script = document.createElement('script');
      script.src = `https://${this.jitsiDomain}/external_api.js`;
      script.async = true;
      script.onload = () => {
        console.log('Jitsi API script loaded');
        resolve();
      };
      script.onerror = () => {
        console.error('Failed to load Jitsi API script');
        reject(new Error('Failed to load Jitsi API'));
      };
      document.head.appendChild(script);
    });
  }
}
