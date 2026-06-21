import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Product } from '../models/product';

export interface ProductPage {
  content: Product[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}

@Injectable({ providedIn: 'root' })
export class ProductService {
  private readonly http = inject(HttpClient);

  getProducts(params?: { name?: string; page?: number; size?: number }): Observable<ProductPage> {
    return this.http.get<ProductPage>('/api/v1/products', { params: { ...params, page: params?.page ?? 0, size: params?.size ?? 20 } });
  }
}
