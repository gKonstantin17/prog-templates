// Типизация для Jitsi Meet External API
// Источник: https://jitsi.github.io/handbook/docs/dev-guide/dev-guide-iframe/

export interface JitsiMeetExternalAPI {
  new(domain: string, options: JitsiMeetOptions): JitsiMeetExternalAPI;

  /**
   * Выполняет команду над конференцией
   */
  executeCommand(command: string, ...args: any[]): void;

  /**
   * Добавляет слушатель событий
   */
  addEventListener(event: string, handler: (data: any) => void): void;

  /**
   * Удаляет слушатель событий
   */
  removeEventListener(event: string, handler: (data: any) => void): void;

  /**
   * Очищает ресурсы и удаляет IFrame
   */
  dispose(): void;

  /**
   * Возвращает IFrame элемент
   */
  getIFrame(): HTMLIFrameElement;
}

export interface JitsiMeetOptions {
  /**
   * Имя комнаты для подключения
   */
  roomName: string;

  /**
   * Ширина IFrame (например, '100%' или 800)
   */
  width?: string | number;

  /**
   * Высота IFrame (например, '100%' или 600)
   */
  height?: string | number;

  /**
   * Родительский элемент для вставки IFrame
   */
  parentNode?: HTMLElement;

  /**
   * Переопределение конфигурации Jitsi
   */
  configOverwrite?: Record<string, any>;

  /**
   * Переопределение конфигурации интерфейса
   */
  interfaceConfigOverwrite?: Record<string, any>;

  /**
   * JWT токен для авторизации
   */
  jwt?: string;

  /**
   * Информация о пользователе
   */
  userInfo?: {
    email?: string;
    displayName?: string;
  };

  /**
   * Устройства для использования
   */
  devices?: {
    audioInput?: string;
    audioOutput?: string;
    videoInput?: string;
  };

  /**
   * Язык интерфейса
   */
  lang?: string;
}

/**
 * События Jitsi Meet
 */
export interface JitsiEvents {
  /**
   * Вызывается когда конференция готова к закрытию
   */
  readyToClose: () => void;

  /**
   * Вызывается когда участник присоединился
   */
  participantJoined: (data: { id: string; displayName: string; formattedId: string }) => void;

  /**
   * Вызывается когда участник покинул конференцию
   */
  participantLeft: (data: { id: string; displayName: string }) => void;

  /**
   * Вызывается когда изменился статус демонстрации экрана
   */
  screenSharingStatusChanged: (data: { on: boolean }) => void;

  /**
   * Вызывается когда изменилось состояние видео
   */
  videoConferenceJoined: (data: { roomName: string; id: string; displayName: string; timestamp: number }) => void;

  /**
   * Вызывается когда изменилось состояние аудио
   */
  audioMuteChanged: (data: { muted: boolean }) => void;

  /**
   * Вызывается когда изменилось состояние видео
   */
  videoMuteChanged: (data: { muted: boolean }) => void;

  /**
   * Вызывается когда получено сообщение через data channel
   */
  endpointMessageReceived: (data: { sender: string; data: any }) => void;
}

/**
 * Команды для управления конференцией
 */
export type JitsiCommands =
  | 'hangup'
  | 'toggleAudio'
  | 'toggleVideo'
  | 'toggleScreenShare'
  | 'toggleFilmstrip'
  | 'toggleChat'
  | 'toggleShareRoomLink'
  | 'setTileView'
  | 'setVideoQuality'
  | 'setAudioMute'
  | 'setVideoMute'
  | 'openChat'
  | 'closeChat'
  | 'sendEndpointMessage';

declare global {
  interface Window {
    JitsiMeetExternalAPI?: JitsiMeetExternalAPI;
  }
}
