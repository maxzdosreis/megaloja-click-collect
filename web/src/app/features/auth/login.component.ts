import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatDividerModule } from '@angular/material/divider';
import { AuthService } from '../../core/services/auth.service';
import { ErrorResponse } from '../../core/models/error-response';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatDividerModule,
  ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss',
})
export class LoginComponent {
  private readonly fb = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  readonly isRegisterMode = signal(false);
  readonly loading = signal(false);
  errorMessage = signal('');

  readonly form = this.fb.group({
    name: [''],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(6)]],
  });

  toggleMode(): void {
    this.isRegisterMode.update(v => !v);
    this.errorMessage.set('');
    if (!this.isRegisterMode()) {
      this.form.get('name')?.clearValidators();
      this.form.get('name')?.setValue('');
    } else {
      this.form.get('name')?.setValidators([Validators.required]);
    }
    this.form.get('name')?.updateValueAndValidity();
    this.form.reset();
  }

  onSubmit(): void {
    if (this.form.invalid) return;

    this.loading.set(true);
    this.errorMessage.set('');

    const { name, email, password } = this.form.value;

    const request$ = this.isRegisterMode()
      ? this.authService.register({ name: name!, email: email!, password: password! })
      : this.authService.login({ email: email!, password: password! });

    request$.subscribe({
      next: () => {
        this.router.navigate(['/home']);
      },
      error: (err: HttpErrorResponse) => {
        const body = err.error as ErrorResponse | undefined;
        this.errorMessage.set(body?.message || 'Erro inesperado. Tente novamente.');
        this.loading.set(false);
      },
    });
  }

  get email() {
    return this.form.get('email');
  }

  get password() {
    return this.form.get('password');
  }

  get name() {
    return this.form.get('name');
  }
}
