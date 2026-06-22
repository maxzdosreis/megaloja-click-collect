import { Component, inject, OnInit, signal } from '@angular/core';
import { CurrencyPipe } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDividerModule } from '@angular/material/divider';
import { OrderService } from '../../../core/services/order.service';
import { OrderResponse } from '../../../core/models/order-response';

@Component({
  selector: 'app-confirmacao',
  standalone: true,
  imports: [
    CurrencyPipe,
    RouterLink,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatDividerModule,
  ],
  templateUrl: './confirmacao.component.html',
  styleUrl: './confirmacao.component.scss',
})
export class ConfirmacaoComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly orderService = inject(OrderService);

  readonly order = signal<OrderResponse | null>(null);
  readonly loading = signal(true);
  readonly error = signal(false);

  ngOnInit(): void {
    const id = Number(this.route.snapshot.params['id']);
    if (!id) {
      this.error.set(true);
      this.loading.set(false);
      return;
    }

    this.orderService.getOrderById(id).subscribe({
      next: res => {
        this.order.set(res);
        this.loading.set(false);
      },
      error: () => {
        this.error.set(true);
        this.loading.set(false);
      },
    });
  }

  getEstimatedTime(): string {
    return 'Em até 2 horas úteis';
  }

  getStatusLabel(status: string): string {
    const map: Record<string, string> = {
      PENDING: 'Pendente',
      APPROVED: 'Aprovado',
      READY: 'Pronto para retirada',
      COMPLETED: 'Finalizado',
      CANCELED: 'Cancelado',
    };
    return map[status] || status;
  }
}
