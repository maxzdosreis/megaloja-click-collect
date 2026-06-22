import { Injectable, signal, computed } from '@angular/core';
import { Product } from '../models/product';
import { CartItem } from '../models/cart-item';
import { Store } from '../models/store';

export type DeliveryMethod = 'delivery' | 'pickup';

@Injectable({ providedIn: 'root' })
export class CartService {
  private readonly items = signal<CartItem[]>([]);

  readonly itemsCount = computed(() =>
    this.items().reduce((sum, item) => sum + item.quantity, 0)
  );

  readonly cartItems = this.items.asReadonly();

  addToCart(product: Product): void {
    this.items.update(list => {
      const existing = list.find(i => i.product.id === product.id);
      if (existing) {
        return list.map(i =>
          i.product.id === product.id ? { ...i, quantity: i.quantity + 1 } : i
        );
      }
      return [...list, { product, quantity: 1 }];
    });
  }

  removeFromCart(productId: number): void {
    this.items.update(list => list.filter(i => i.product.id !== productId));
  }

  updateQuantity(productId: number, quantity: number): void {
    this.items.update(list =>
      quantity <= 0
        ? list.filter(i => i.product.id !== productId)
        : list.map(i => (i.product.id === productId ? { ...i, quantity } : i))
    );
  }

  clearCart(): void {
    this.items.set([]);
  }

  readonly selectedMethod = signal<DeliveryMethod>('pickup');

  readonly selectedStore = signal<Store | null>(null);

  setDeliveryMethod(method: DeliveryMethod): void {
    this.selectedMethod.set(method);
    if (method === 'delivery') {
      this.selectedStore.set(null);
    }
  }

  setSelectedStore(store: Store): void {
    this.selectedStore.set(store);
    this.selectedMethod.set('pickup');
  }

  readonly selectedPayment = signal<string>('pix');

  setSelectedPayment(method: string): void {
    this.selectedPayment.set(method);
  }
}
