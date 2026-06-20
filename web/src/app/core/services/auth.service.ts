import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { LoginRequest } from '../models/login-request';
import { RegisterRequest } from '../models/register-request';
import { AuthResponse } from '../models/auth-response';

const AUTH_API = '/api/v1/auth';
const TOKEN_KEY = 'megaloja_token';
const REFRESH_KEY = 'megaloja_refresh_token';
const USER_KEY = 'megaloja_user';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);

  login(credentials: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${AUTH_API}/login`, credentials).pipe(
      tap(res => this.saveSession(res))
    );
  }

  register(data: RegisterRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${AUTH_API}/register`, data).pipe(
      tap(res => this.saveSession(res))
    );
  }

  logout(): void {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(REFRESH_KEY);
    localStorage.removeItem(USER_KEY);
    this.router.navigate(['/login']);
  }

  isAuthenticated(): boolean {
    return !!localStorage.getItem(TOKEN_KEY);
  }

  getToken(): string | null {
    return localStorage.getItem(TOKEN_KEY);
  }

  private saveSession(res: AuthResponse): void {
    localStorage.setItem(TOKEN_KEY, res.token);
    localStorage.setItem(REFRESH_KEY, res.refreshToken);
    localStorage.setItem(USER_KEY, JSON.stringify(res.user));
  }
}
