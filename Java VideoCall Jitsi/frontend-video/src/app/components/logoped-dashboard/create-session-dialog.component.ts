import { Component } from '@angular/core';
import { MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSelectModule } from '@angular/material/select';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatIconModule } from '@angular/material/icon';
import { FormsModule } from '@angular/forms';
import { CreateSessionRequest } from '../../services/session.service';
import { User } from '../../models/user';

@Component({
  selector: 'app-create-session-dialog',
  standalone: true,
  imports: [
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatSelectModule,
    MatCheckboxModule,
    MatIconModule,
    FormsModule
  ],
  templateUrl: './create-session-dialog.component.html',
  styleUrls: ['./create-session-dialog.component.scss']
})
export class CreateSessionDialogComponent {
  title = '';
  startTime = '';
  endTime = '';
  selectedPatients: number[] = [];
  patients: User[] = [];

  constructor(public dialogRef: MatDialogRef<CreateSessionDialogComponent>) {
    // Устанавливаем даты по умолчанию при создании
    this.setDefaultDates();
  }

  /**
   * Устанавливает дату начала = текущее время, дату окончания = +1 час
   */
  private setDefaultDates(): void {
    const now = new Date();
    const oneHourLater = new Date(now.getTime() + 60 * 60 * 1000);

    // Форматируем для datetime-local input (YYYY-MM-DDTHH:MM)
    this.startTime = this.formatDateTimeLocal(now);
    this.endTime = this.formatDateTimeLocal(oneHourLater);
  }

  /**
   * Форматирует дату в формат для datetime-local input
   */
  private formatDateTimeLocal(date: Date): string {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    
    return `${year}-${month}-${day}T${hours}:${minutes}`;
  }

  ngOnInit() {
    // Тестовые данные - пациенты создаются при старте backend
    this.patients = [
      { id: 3, username: 'patient1', fullName: 'Пациент Один', role: 'PATIENT' },
      { id: 4, username: 'patient2', fullName: 'Пациент Два', role: 'PATIENT' }
    ];
  }

  togglePatient(patientId: number, event: any) {
    if (event.checked) {
      this.selectedPatients.push(patientId);
    } else {
      this.selectedPatients = this.selectedPatients.filter(id => id !== patientId);
    }
  }

  create(): void {
    if (!this.title || !this.startTime || !this.endTime) {
      return;
    }

    const request: CreateSessionRequest = {
      title: this.title,
      startTime: new Date(this.startTime).toISOString(),
      endTime: new Date(this.endTime).toISOString(),
      patientIds: this.selectedPatients
    };

    this.dialogRef.close(request);
  }
}
