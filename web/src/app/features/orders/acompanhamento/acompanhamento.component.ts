import { Component, inject, OnInit, signal, computed } from '@angular/core';
import { DatePipe } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { Location } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { OrderService } from '../../../core/services/order.service';
import { OrderResponse } from '../../../core/models/order-response';

interface Step {
  status: string;
  label: string;
  icon: string;
  description: string;
}

@Component({
  selector: 'app-acompanhamento',
  standalone: true,
  imports: [
    DatePipe,
    RouterLink,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
  ],
  templateUrl: './acompanhamento.component.html',
  styleUrl: './acompanhamento.component.scss',
})
export class AcompanhamentoComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly orderService = inject(OrderService);
  private readonly location = inject(Location);

  readonly order = signal<OrderResponse | null>(null);
  readonly loading = signal(true);
  readonly error = signal(false);

  readonly steps: Step[] = [
    {
      status: 'PENDING',
      label: 'Pedido Confirmado',
      icon: 'check_circle',
      description: 'Seu pedido foi registrado com sucesso',
    },
    {
      status: 'APPROVED',
      label: 'Em Separação',
      icon: 'inventory',
      description: 'Seus itens estão sendo separados',
    },
    {
      status: 'READY_FOR_PICKUP',
      label: 'Pronto para Retirada',
      icon: 'storefront',
      description: 'Aguardando retirada na loja',
    },
  ];

  readonly currentStepIndex = computed(() => {
    const status = this.order()?.status;
    if (!status || status === 'CANCELED') return -1;
    return this.steps.findIndex(s => s.status === status);
  });

  readonly isCanceled = computed(() => this.order()?.status === 'CANCELED');
  readonly isDelivered = computed(() => this.order()?.status === 'DELIVERED');

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

  isCompleted(index: number): boolean {
    return this.currentStepIndex() > index || this.isDelivered();
  }

  isActive(index: number): boolean {
    return this.currentStepIndex() === index;
  }

  goBack(): void {
    this.location.back();
  }
}
