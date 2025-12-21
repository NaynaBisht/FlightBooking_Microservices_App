import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators, FormGroup } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../core/auth/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './login.html'
})
export class LoginComponent {

  loginForm!: FormGroup;
  error = '';

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.loginForm = this.fb.group({
      username: ['', Validators.required],
      password: ['', Validators.required]
    });
  }

  login() {
  if (this.loginForm.invalid) return;

  this.authService.login(this.loginForm.value).subscribe({
    next: (res) => {
      this.authService.saveUser(res); 
      
      const roles = res.roles || []; 
      
      if (roles.includes('admin') || roles.includes('ROLE_ADMIN')) {
        this.router.navigate(['/addflight']);
      } else {
        this.router.navigate(['/flights']);
      }
    },
    error: () => {
      this.error = 'Invalid username or password';
    }
  });
}
}
