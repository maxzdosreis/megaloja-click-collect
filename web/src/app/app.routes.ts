import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  {
    path: 'login',
    loadComponent: () =>
      import('./features/auth/login.component').then(c => c.LoginComponent),
  },
  {
    path: '',
    loadComponent: () =>
      import('./layouts/main-layout/main-layout.component').then(
        c => c.MainLayoutComponent
      ),
    canActivate: [authGuard],
    children: [
      {
        path: 'home',
        loadComponent: () =>
          import('./features/store/vitrine/vitrine.component').then(
            c => c.VitrineComponent
          ),
      },
      {
        path: 'carrinho',
        loadComponent: () =>
          import('./features/store/carrinho/carrinho.component').then(
            c => c.CarrinhoComponent
          ),
      },
      {
        path: 'entrega',
        loadComponent: () =>
          import('./features/store/entrega/entrega.component').then(
            c => c.EntregaComponent
          ),
      },
      {
        path: 'pagamento',
        loadComponent: () =>
          import('./features/store/pagamento/pagamento.component').then(
            c => c.PagamentoComponent
          ),
      },
      {
        path: 'pedido-confirmado/:id',
        loadComponent: () =>
          import(
            './features/store/pedido-confirmado/pedido-confirmado.component'
          ).then(c => c.PedidoConfirmadoComponent),
      },
      {
        path: '',
        redirectTo: '/home',
        pathMatch: 'full',
      },
    ],
  },
  {
    path: '**',
    redirectTo: '/login',
  },
];
