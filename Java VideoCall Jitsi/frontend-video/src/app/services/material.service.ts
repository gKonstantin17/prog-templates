import { Injectable } from '@angular/core';
import { HttpClient, HttpEventType } from '@angular/common/http';
import { Material } from '../models/material';
import { firstValueFrom } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class MaterialService {
  constructor(private http: HttpClient) {}

  async getMaterials(sessionId: number): Promise<Material[]> {
    return firstValueFrom(
      this.http.get<Material[]>(`/api/materials/sessions/${sessionId}`, { withCredentials: true })
    );
  }

  async uploadMaterial(sessionId: number, file: File): Promise<Material> {
    const formData = new FormData();
    formData.append('file', file);

    return firstValueFrom(
      this.http.post<Material>(`/api/materials/sessions/${sessionId}`, formData, { withCredentials: true })
    );
  }

  async deleteMaterial(id: number): Promise<void> {
    await firstValueFrom(
      this.http.delete(`/api/materials/${id}`, { withCredentials: true })
    );
  }

  getMaterialUrl(id: number): string {
    return `/api/materials/${id}`;
  }
}
