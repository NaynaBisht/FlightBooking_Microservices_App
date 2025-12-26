import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators, FormGroup } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../core/auth/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './login.html',
})
export class LoginComponent {
  loginForm!: FormGroup;
  error = '';
  loading = false;

  showPassword = false;

  constructor(private fb: FormBuilder, private authService: AuthService, private router: Router) {
    this.loginForm = this.fb.group({
      username: ['', Validators.required],
      password: ['', Validators.required],
    });
  }
  togglePassword(): void {
    this.showPassword = !this.showPassword;
  }
  login(): void {
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    this.error = '';
    this.loading = true;

    this.authService.login(this.loginForm.value).subscribe({
      next: (res: any) => {
        this.loading = false;

        this.authService.saveUser(res);

        if (res.mustChangePassword === true) {
          this.router.navigate(['/profile'], {
            queryParams: { forced: true },
          });
          return;
        }

        const roles: string[] = res.roles || [];

        if (roles.includes('admin') || roles.includes('ROLE_ADMIN')) {
          this.router.navigate(['/addflight']);
        } else {
          this.router.navigate(['/flights']);
        }
      },
      error: () => {
        this.loading = false;
        this.error = 'Invalid username or password';
      },
    });
  }
}
