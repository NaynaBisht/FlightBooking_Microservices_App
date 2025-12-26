import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators, FormGroup } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../core/auth/auth.service';
import { passwordStrengthValidator } from '../../utils/password-validator';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './register.html',
})
export class RegisterComponent {
  form!: FormGroup;

  showPassword = false;

  constructor(private fb: FormBuilder, private authService: AuthService, private router: Router) {
    this.form = this.fb.group({
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      mobileNumber: ['', Validators.required],
      username: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(8), passwordStrengthValidator()]],
      role: ['user'],
    });
  }

  togglePassword(): void {
    this.showPassword = !this.showPassword;
  }

  register() {
    if (this.form.invalid) return;

    const payload = {
      firstName: this.form.value.firstName,
      lastName: this.form.value.lastName,
      mobileNumber: this.form.value.mobileNumber,
      username: this.form.value.username,
      email: this.form.value.email,
      password: this.form.value.password,
      role: [this.form.value.role],
    };

    this.authService.register(payload).subscribe({
      next: () => {
        this.router.navigate(['/login']);
      },
      error: (err: any) => {
        console.error('Error Response:', err);
      },
    });
  }
}
