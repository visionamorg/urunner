import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface ExportTemplateDto {
  id: number;
  creatorId: number;
  creatorUsername: string;
  name: string;
  description: string;
  cssLayout: string;
  previewUrl: string;
  votes: number;
  downloads: number;
  hasVoted: boolean;
  createdAt: string;
}

export interface CreateExportTemplateRequest {
  name: string;
  description: string;
  cssLayout: string;
  previewUrl?: string;
}

@Injectable({ providedIn: 'root' })
export class ExportTemplateService {
  constructor(private http: HttpClient) {}

  getAll(): Observable<ExportTemplateDto[]> {
    return this.http.get<ExportTemplateDto[]>('/api/export-templates');
  }

  create(req: CreateExportTemplateRequest): Observable<ExportTemplateDto> {
    return this.http.post<ExportTemplateDto>('/api/export-templates', req);
  }

  toggleVote(id: number): Observable<ExportTemplateDto> {
    return this.http.post<ExportTemplateDto>(`/api/export-templates/${id}/vote`, {});
  }

  download(id: number): Observable<ExportTemplateDto> {
    return this.http.post<ExportTemplateDto>(`/api/export-templates/${id}/download`, {});
  }
}
