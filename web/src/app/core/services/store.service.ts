import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Store } from '../models/store';

export interface StorePage {
  content: Store[];
}

@Injectable({ providedIn: 'root' })
export class StoreService {
  private readonly http = inject(HttpClient);

  getStores(): Observable<StorePage> {
    return this.http.get<StorePage>('/api/v1/stores');
  }
}
