import { Component, inject, OnInit, signal } from '@angular/core';
import { CurrencyPipe, DatePipe } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { OrderService, OrderPage } from '../../../core/services/order.service';
import { OrderResponse } from '../../../core/models/order-response';
import { User } from '../../../core/models/user';

@Component({
  selector: 'app-historico',
  standalone: true,
  imports: [
    CurrencyPipe,
    DatePipe,
    RouterLink,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
  ],
  templateUrl: './historico.component.html',
  styleUrl: './historico.component.scss',
})
export class HistoricoComponent implements OnInit {
  private readonly orderService = inject(OrderService);
  private readonly router = inject(Router);

  readonly orders = signal<OrderResponse[]>([]);
  readonly loading = signal(true);
  readonly error = signal(false);

  ngOnInit(): void {
    const raw = localStorage.getItem('megaloja_user');
    if (!raw) {
      this.error.set(true);
      this.loading.set(false);
      return;
    }

    const user: User = JSON.parse(raw);

    this.orderService.getOrders(user.id).subscribe({
      next: (page: OrderPage) => {
        this.orders.set(page.content);
        this.loading.set(false);
      },
      error: () => {
        this.error.set(true);
        this.loading.set(false);
      },
    });
  }

  getStatusLabel(status: string): string {
    const map: Record<string, string> = {
      PENDING: 'Pendente',
      APPROVED: 'Em Separação',
      READY_FOR_PICKUP: 'Pronto para Retirada',
      DELIVERED: 'Finalizado',
      CANCELED: 'Cancelado',
    };
    return map[status] || status;
  }

  getStatusClass(status: string): string {
    const classes: Record<string, string> = {
      PENDING: 'status-pending',
      APPROVED: 'status-separating',
      READY_FOR_PICKUP: 'status-ready',
      DELIVERED: 'status-ready',
      CANCELED: 'status-canceled',
    };
    return classes[status] || '';
  }

  trackOrder(id: number): void {
    this.router.navigate(['/acompanhar', id]);
  }
}
