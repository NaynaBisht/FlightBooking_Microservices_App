import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

@Component({
  selector: 'app-addflight',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './addflight.html',
})
export class AddflightComponent implements OnInit {
  flightForm!: FormGroup;
  private apiUrl = 'http://localhost:8765/api/flight/airline/inventory/add';

  submitStatus: 'idle' | 'success' | 'error' = 'idle';
  errorMessage = '';

  minDateTime!: string;

  constructor(private fb: FormBuilder, private http: HttpClient, private router: Router) {}

  ngOnInit(): void {
    this.minDateTime = this.getCurrentDateTime();

    this.flightForm = this.fb.group({
      flightNumber: ['', Validators.required],
      airlineName: ['', Validators.required],
      departingAirport: ['', Validators.required],
      arrivalAirport: ['', Validators.required],
      departureTime: ['', Validators.required],
      arrivalTime: ['', Validators.required],
      price: [null, [Validators.required, Validators.min(1)]],
      totalSeats: [null, [Validators.required, Validators.min(1)]],
    });
  }

  private getCurrentDateTime(): string {
    const now = new Date();
    now.setSeconds(0, 0);
    return now.toISOString().slice(0, 16);
  }

  onSubmit(): void {
    if (this.flightForm.invalid) return;

    this.submitStatus = 'idle';
    const flightData = this.flightForm.value;

    this.http.post(this.apiUrl, flightData).subscribe({
      next: (response) => {
        this.submitStatus = 'success';
        this.flightForm.reset();

        setTimeout(() => {
          this.router.navigate(['/flights']);
        }, 2000);
      },
      error: (err) => {
        this.submitStatus = 'error';
        this.errorMessage = err.error?.message || 'Failed to add flight. Please try again.';
      },
    });
  }
}
