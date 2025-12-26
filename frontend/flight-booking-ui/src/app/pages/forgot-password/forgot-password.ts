import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router, RouterModule } from '@angular/router'; // Added RouterModule for routerLink
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-forgot-password',
  standalone: true, // Assuming standalone based on your imports
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './forgot-password.html',
})
export class ForgotPasswordComponent implements OnInit {
  forgotForm!: FormGroup;
  otpForm!: FormGroup;
  resetForm!: FormGroup;
  step: number = 1;
  submittedEmail: string = '';

  private readonly API_URL = 'http://localhost:8765/api/auth';

  constructor(
    private fb: FormBuilder,
    private http: HttpClient, 
    private router: Router
  ) {}

  ngOnInit(): void {
    this.forgotForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]]
    });

    this.otpForm = this.fb.group({
      otp: ['', [Validators.required, Validators.minLength(6), Validators.maxLength(6)]]
    });

    this.resetForm = this.fb.group({
      newPassword: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

  sendOtp() {
    if (this.forgotForm.valid) {
      this.submittedEmail = this.forgotForm.value.email;
      this.http.post(`${this.API_URL}/forgot-password`, { email: this.submittedEmail })
        .subscribe({
          next: () => this.step = 2,
          error: () => alert('Error sending OTP. Please check if the email is registered.')
        });
    }
  }

  verifyOtp() {
    if (this.otpForm.valid) {
      this.step = 3;
    }
  }

  resetPassword() {
    if (this.resetForm.valid && this.otpForm.valid) {
      const data = { 
        email: this.submittedEmail, 
        otp: this.otpForm.value.otp, 
        newPassword: this.resetForm.value.newPassword 
      };
      
      this.http.post(`${this.API_URL}/reset-password`, data)
        .subscribe({
          next: () => {
            alert('Password changed successfully!');
            this.router.navigate(['/login']);
          },
          error: () => alert('Invalid OTP or password update failed.')
        });
    }
  }
}