import { Component, inject, signal, computed, OnInit } from '@angular/core';
import { CurrencyPipe } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { ProductService } from '../../../core/services/product.service';
import { CartService } from '../../../core/services/cart.service';
import { Product } from '../../../core/models/product';

interface Category {
  icon: string;
  label: string;
}

@Component({
  selector: 'app-vitrine',
  standalone: true,
  imports: [
    CurrencyPipe,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatChipsModule,
  ],
  templateUrl: './vitrine.component.html',
  styleUrl: './vitrine.component.scss',
})
export class VitrineComponent implements OnInit {
  private readonly productService = inject(ProductService);
  private readonly cartService = inject(CartService);

  readonly products = signal<Product[]>([]);
  readonly loading = signal(true);
  readonly selectedCategory = signal<string | null>(null);

  readonly categories: Category[] = [
    { icon: 'smartphone', label: 'Eletrônicos' },
    { icon: 'restaurant', label: 'Alimentos' },
    { icon: 'checkroom', label: 'Roupas' },
    { icon: 'chair', label: 'Casa' },
    { icon: 'sports_soccer', label: 'Esportes' },
    { icon: 'menu_book', label: 'Livros' },
  ];

  readonly filteredProducts = computed(() => {
    const cat = this.selectedCategory();
    if (!cat) return this.products();
    return this.products().filter(p =>
      p.name.toLowerCase().includes(cat.toLowerCase())
    );
  });

  ngOnInit(): void {
    this.productService.getProducts().subscribe({
      next: page => {
        this.products.set(page.content);
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
      },
    });
  }

  filterByCategory(label: string): void {
    this.selectedCategory.set(
      this.selectedCategory() === label ? null : label
    );
  }

  clearFilter(): void {
    this.selectedCategory.set(null);
  }

  addToCart(product: Product): void {
    this.cartService.addToCart(product);
  }
}
