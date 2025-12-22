import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { passwordStrengthValidator } from '../../utils/password-validator';
import { AuthService } from '../../core/auth/auth.service';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './profile.html',
})
export class ProfileComponent implements OnInit {
  profileForm!: FormGroup;
  user: any;

  statusMsg = '';
  isError = false;
  loading = false;

  showCurrentPassword = false;
  showNewPassword = false;
  showConfirmPassword = false;

  constructor(private fb: FormBuilder, private authService: AuthService) {}

  ngOnInit(): void {
    this.user = this.authService.getUser();
    this.initForm();
  }

  private initForm() {
    this.profileForm = this.fb.group(
      {
        currentPassword: ['', Validators.required],
        newPassword: [
          '',
          [Validators.required, Validators.minLength(8), passwordStrengthValidator()],
        ],
        confirmPassword: ['', Validators.required],
      },
      { validators: this.passwordMatchValidator }
    );
  }

  passwordMatchValidator(group: FormGroup) {
    return group.get('newPassword')?.value === group.get('confirmPassword')?.value
      ? null
      : { mismatch: true };
  }

  onSubmit() {
    if (this.profileForm.invalid || this.loading) return;

    this.loading = true;
    this.statusMsg = '';
    this.isError = false;

    this.authService.updatePassword(this.profileForm.value).subscribe({
      next: () => {
        this.loading = false;
        this.statusMsg = 'Password updated successfully!';
        this.isError = false;
        this.profileForm.reset();
      },
      error: (err) => {
        this.loading = false;
        this.isError = true;

        this.profileForm.get('currentPassword')?.setErrors({ incorrect: true });
        this.statusMsg = err.error?.message || 'Current password is incorrect.';
      },
    });
  }
}
