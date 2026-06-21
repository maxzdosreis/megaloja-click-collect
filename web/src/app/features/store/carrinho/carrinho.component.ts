import { Component, inject, computed } from '@angular/core';
import { CurrencyPipe, Location } from '@angular/common';
import { RouterLink } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDividerModule } from '@angular/material/divider';
import { CartService } from '../../../core/services/cart.service';

@Component({
  selector: 'app-carrinho',
  standalone: true,
  imports: [
    CurrencyPipe,
    RouterLink,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatDividerModule,
  ],
  templateUrl: './carrinho.component.html',
  styleUrl: './carrinho.component.scss',
})
export class CarrinhoComponent {
  readonly cartService = inject(CartService);
  private readonly location = inject(Location);

  readonly subtotal = computed(() =>
    this.cartService.cartItems().reduce(
      (sum, item) => sum + item.product.price * item.quantity,
      0
    )
  );

  readonly total = computed(() => this.subtotal());

  goBack(): void {
    this.location.back();
  }

  increaseQty(productId: number, current: number): void {
    this.cartService.updateQuantity(productId, current + 1);
  }

  decreaseQty(productId: number, current: number): void {
    if (current <= 1) {
      this.cartService.removeFromCart(productId);
    } else {
      this.cartService.updateQuantity(productId, current - 1);
    }
  }

  removeItem(productId: number): void {
    this.cartService.removeFromCart(productId);
  }
}
