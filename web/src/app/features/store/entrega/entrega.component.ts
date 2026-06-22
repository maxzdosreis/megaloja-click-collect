import { Component, inject, OnInit, signal } from '@angular/core';
import { Location } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDividerModule } from '@angular/material/divider';
import { CartService, DeliveryMethod } from '../../../core/services/cart.service';
import { StoreService } from '../../../core/services/store.service';
import { Store } from '../../../core/models/store';

@Component({
  selector: 'app-entrega',
  standalone: true,
  imports: [
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatDividerModule,
  ],
  templateUrl: './entrega.component.html',
  styleUrl: './entrega.component.scss',
})
export class EntregaComponent implements OnInit {
  readonly cartService = inject(CartService);
  private readonly storeService = inject(StoreService);
  private readonly location = inject(Location);

  readonly stores = signal<Store[]>([]);
  readonly loading = signal(true);

  private readonly storeHours: Record<number, string> = {
    1: 'Seg-Sex 08h-18h, Sáb 08h-12h',
    2: 'Seg-Sáb 10h-22h, Dom 12h-20h',
    3: 'Seg-Sex 09h-19h, Sáb 09h-15h',
  };

  ngOnInit(): void {
    this.storeService.getStores().subscribe({
      next: page => {
        this.stores.set(page.content.filter(s => s.active));
        this.loading.set(false);
      },
      error: () => this.loading.set(false),
    });
  }

  getStoreHours(storeId: number): string {
    return this.storeHours[storeId] || 'Consulte a loja';
  }

  selectMethod(method: DeliveryMethod): void {
    this.cartService.setDeliveryMethod(method);
  }

  selectStore(store: Store): void {
    this.cartService.setSelectedStore(store);
  }

  goBack(): void {
    this.location.back();
  }
}
