import { Component, inject, computed, signal } from '@angular/core';
import { CurrencyPipe } from '@angular/common';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { Location } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDividerModule } from '@angular/material/divider';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { CartService } from '../../../core/services/cart.service';
import { OrderService } from '../../../core/services/order.service';
import { CreateOrderRequest } from '../../../core/models/create-order-request';
import { User } from '../../../core/models/user';

interface PaymentOption {
  id: string;
  label: string;
  icon: string;
}

@Component({
  selector: 'app-pagamento',
  standalone: true,
  imports: [
    CurrencyPipe,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatDividerModule,
    MatProgressSpinnerModule,
  ],
  templateUrl: './pagamento.component.html',
  styleUrl: './pagamento.component.scss',
})
export class PagamentoComponent {
  readonly cartService = inject(CartService);
  private readonly orderService = inject(OrderService);
  private readonly router = inject(Router);
  private readonly location = inject(Location);

  readonly paymentMethods: PaymentOption[] = [
    { id: 'credit_card', label: 'Cartão de Crédito', icon: 'credit_card' },
    { id: 'pix', label: 'Pix', icon: 'pix' },
    { id: 'debit_card', label: 'Cartão de Débito', icon: 'credit_card' },
  ];

  readonly selectedPayment = signal('pix');
  readonly loading = signal(false);
  readonly errorMessage = signal('');

  readonly total = computed(() =>
    this.cartService.cartItems().reduce(
      (sum, item) => sum + item.product.price * item.quantity, 0
    )
  );

  selectPayment(id: string): void {
    this.selectedPayment.set(id);
    this.cartService.setSelectedPayment(id);
  }

  goBack(): void {
    this.location.back();
  }

  finalizarPedido(): void {
    if (this.cartService.cartItems().length === 0 || !this.cartService.selectedStore()) return;

    this.loading.set(true);
    this.errorMessage.set('');

    const user: User = JSON.parse(localStorage.getItem('megaloja_user')!);

    const request: CreateOrderRequest = {
      customerId: user.id,
      storeId: this.cartService.selectedStore()!.id,
      items: this.cartService.cartItems().map(item => ({
        productId: item.product.id,
        orderId: 0,
        quantity: item.quantity,
        unitPrice: item.product.price,
      })),
    };

    this.orderService.createOrder(request).subscribe({
      next: response => {
        this.cartService.clearCart();
        this.loading.set(false);
        this.router.navigate(['/pedido-confirmado', response.id]);
      },
      error: (err: HttpErrorResponse) => {
        this.errorMessage.set(
          err.error?.message || 'Erro ao criar pedido. Tente novamente.'
        );
        this.loading.set(false);
      },
    });
  }
}
