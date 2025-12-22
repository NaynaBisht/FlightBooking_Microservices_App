import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { isPlatformBrowser } from '@angular/common'; // Important import

@Injectable({ providedIn: 'root' })
export class AuthService {
  private baseUrl = 'http://localhost:8765/api/auth';
  private isBrowser: boolean;

  constructor(
    private http: HttpClient,
    private router: Router,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {
    // Initialize the check once in the constructor
    this.isBrowser = isPlatformBrowser(this.platformId);
  }

  login(credentials: { username: string; password: string }): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/signin`, credentials);
  }

  register(user: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/signup`, user);
  }

  saveUser(data: any) {
    if (this.isBrowser) {
      const token = data.accessToken || data.token;
      if (token) {
        localStorage.setItem('token', token);
        localStorage.setItem('user', JSON.stringify(data));
        localStorage.setItem('mustChangePassword', JSON.stringify(data.mustChangePassword));
      }
    }
  }

  isAdmin(): boolean {
    const user = this.getUser();
    const roles: string[] = user.roles || user.role || [];

    return roles.some(
      (role) => role.toLowerCase() === 'admin' || role.toLowerCase() === 'role_admin'
    );
  }

  getToken(): string | null {
    if (this.isBrowser) {
      return localStorage.getItem('token');
    }
    return null;
  }

  getUser() {
    if (this.isBrowser) {
      return JSON.parse(localStorage.getItem('user') || '{}');
    }
    return {};
  }

  logout() {
    if (this.isBrowser) {
      localStorage.clear();
      this.router.navigate(['/login']);
    }
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  updatePassword(passwordData: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/change-password`, passwordData);
  }

  checkPasswordExpiry(): boolean {
    const user = this.getUser();
    if (!user || !user.lastPasswordChangeDate) return false;

    const lastChange = new Date(user.lastPasswordChangeDate).getTime();
    const today = new Date().getTime();
    const diffInDays = (today - lastChange) / (1000 * 60 * 60 * 24);

    const MAX_DAYS = 90; // Your limit (X days)
    return diffInDays > MAX_DAYS;
  }
}
