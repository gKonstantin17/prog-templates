import { Component, OnInit, signal, OnDestroy, ViewChild, ElementRef } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { JitsiService } from '../../services/jitsi.service';
import { MaterialService } from '../../services/material.service';
import { Material } from '../../models/material';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { SessionService } from '../../services/session.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-video-room',
  standalone: true,
  imports: [
    MatIconModule,
    MatButtonModule,
    MatToolbarModule,
    MatProgressSpinnerModule,
    MatSnackBarModule,
    CommonModule,
    FormsModule,
    MatInputModule,
    MatFormFieldModule
  ],
  templateUrl: './video-room.component.html',
  styleUrls: ['./video-room.component.scss']
})
export class VideoRoomComponent implements OnInit, OnDestroy {
  @ViewChild('jitsiContainer', { static: true }) jitsiContainer!: ElementRef;

  roomName = signal('');
  connecting = signal(true);
  isMuted = signal(false);
  isCameraOff = signal(false);
  isScreenSharing = signal(false);
  tileViewEnabled = signal(false);
  materials = signal<Material[]>([]);
  isLogoped = signal(false);

  private sessionId?: number;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private snackBar: MatSnackBar,
    public materialService: MaterialService,
    private jitsiService: JitsiService,
    private sessionService: SessionService,
    private authService: AuthService
  ) {}

  async ngOnInit() {
    this.sessionId = this.route.snapshot.params['id'];
    const token = this.route.snapshot.queryParamMap.get('token');
    this.roomName.set(this.route.snapshot.queryParamMap.get('room') || 'Комната');

    if (!token) {
      this.snackBar.open('Ошибка: нет токена для подключения', 'Закрыть', { duration: 3000 });
      this.router.navigate(['/logoped']);
      return;
    }

    try {
      // Получаем данные сессии
      if (this.sessionId) {
        const session = await this.sessionService.getSession(this.sessionId);
        this.roomName.set(session.title);
      }

      // Определяем роль пользователя
      const user = await this.authService.getCurrentUser();
      this.isLogoped.set(user.role === 'LOGOPED');

      // Получаем токен Jitsi с сервера
      if (this.sessionId) {
        const { url, token: jitsiToken, roomName } = await this.sessionService.getJoinToken(this.sessionId);

        // Устанавливаем домен Jitsi из ответа сервера
        let jitsiDomain = url;
        if (jitsiDomain.startsWith('https://')) {
          jitsiDomain = jitsiDomain.substring(8);
        } else if (jitsiDomain.startsWith('http://')) {
          jitsiDomain = jitsiDomain.substring(7);
        }
        if (jitsiDomain.endsWith('/')) {
          jitsiDomain = jitsiDomain.slice(0, -1);
        }
        this.jitsiService.setJitsiDomain(jitsiDomain);

        // Подключаемся к комнате Jitsi
        await this.jitsiService.joinRoom('jitsi-meet-frame', roomName, {
          displayName: user.fullName || user.username,
          email: '',
          isLogoped: user.role === 'LOGOPED',
          jwt: jitsiToken
        });

        // Подписываемся на события
        this.jitsiService.addEventListener('videoConferenceJoined', () => {
          console.log('✅ Jitsi conference joined!');
          this.connecting.set(false);
          this.snackBar.open('Подключено к комнате', 'Закрыть', { duration: 2000 });
        });

        this.jitsiService.addEventListener('participantJoined', () => {
          console.log('✅ Participant joined - hiding overlay');
          this.connecting.set(false);
        });

        // Таймаут на случай если события не сработают
        setTimeout(() => {
          if (this.connecting()) {
            console.warn('⚠️ Forcing connecting=false after timeout');
            this.connecting.set(false);
          }
        }, 3000);

        this.jitsiService.addEventListener('screenSharingStatusChanged', (data: any) => {
          this.isScreenSharing.set(data.on);
        });

        this.jitsiService.addEventListener('tileViewChanged', (data: any) => {
          this.tileViewEnabled.set(data.enabled);
        });

        this.jitsiService.addEventListener('readyToClose', () => {
          this.router.navigate(['/logoped']);
        });
      }

      // Загружаем материалы сессии
      if (this.sessionId) {
        try {
          const materials = await this.materialService.getMaterials(this.sessionId);
          this.materials.set(materials);
        } catch {
          // Игнорируем ошибку загрузки материалов
        }
      }

    } catch (e: any) {
      console.error('Connection error:', e);
      this.snackBar.open('Ошибка подключения: ' + e.message, 'Закрыть', { duration: 5000 });
      this.router.navigate(['/logoped']);
    }
  }

  toggleMute() {
    this.jitsiService.toggleAudio();
    this.isMuted.update(v => !v);
  }

  toggleCamera() {
    this.jitsiService.toggleVideo();
    this.isCameraOff.update(v => !v);
  }

  toggleScreenShare() {
    this.jitsiService.toggleScreenShare();
  }

  toggleTileView() {
    // Переключаем режим сетки
    const newState = !this.tileViewEnabled();
    this.jitsiService.setTileView(newState);
    this.tileViewEnabled.set(newState);
    
    this.snackBar.open(
      newState ? 'Режим сетки включён' : 'Режим сетки выключен', 
      'Закрыть', 
      { duration: 2000 }
    );
  }

  // Материалы и презентации
  async uploadMaterial(event: Event) {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    if (!file) return;

    try {
      const sessionId = this.route.snapshot.params['id'];
      await this.materialService.uploadMaterial(sessionId, file);
      this.snackBar.open('Файл загружен', 'Закрыть', { duration: 3000 });
      const materials = await this.materialService.getMaterials(sessionId);
      this.materials.set(materials);
    } catch (e: any) {
      this.snackBar.open('Ошибка загрузки: ' + e.message, 'Закрыть', { duration: 5000 });
    }
    input.value = '';
  }

  async deleteMaterial(id: number) {
    if (!confirm('Удалить этот материал?')) return;
    try {
      await this.materialService.deleteMaterial(id);
      const sessionId = this.route.snapshot.params['id'];
      const materials = await this.materialService.getMaterials(sessionId);
      this.materials.set(materials);
      this.snackBar.open('Материал удален', 'Закрыть', { duration: 3000 });
    } catch (e: any) {
      this.snackBar.open('Ошибка удаления: ' + e.message, 'Закрыть', { duration: 5000 });
    }
  }

  showMaterials() {
    const sessionId = this.route.snapshot.params['id'];
    this.materialService.getMaterials(sessionId).then(materials => {
      this.materials.set(materials);
    });
  }

  async leaveRoom() {
    console.log('Leaving room...');
    await this.cleanup();
    this.router.navigate(['/logoped']);
  }

  private async cleanup() {
    console.log('Cleaning up Jitsi connection...');
    this.jitsiService.dispose();
  }

  ngOnDestroy() {
    this.cleanup();
  }
}
