import { Component, Input, Output, EventEmitter } from '@angular/core';
import { PdfViewerModule } from 'ng2-pdf-viewer';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatToolbarModule } from '@angular/material/toolbar';

@Component({
  selector: 'app-presentation-viewer',
  standalone: true,
  imports: [PdfViewerModule, MatIconModule, MatButtonModule, MatToolbarModule],
  templateUrl: './presentation-viewer.component.html',
  styleUrls: ['./presentation-viewer.component.scss']
})
export class PresentationViewerComponent {
  @Input() pdfUrl: string | Uint8Array = '';
  @Input() fileName: string = '';
  @Input() page: number = 1;
  @Input() total: number = 1;
  @Output() pageChange = new EventEmitter<number>();
  @Output() close = new EventEmitter<void>();

  nextPage() {
    if (this.page < this.total) {
      this.page++;
      this.pageChange.emit(this.page);
    }
  }

  previousPage() {
    if (this.page > 1) {
      this.page--;
      this.pageChange.emit(this.page);
    }
  }
}
