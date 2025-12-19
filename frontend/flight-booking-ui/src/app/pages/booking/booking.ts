import { Component, OnInit } from '@angular/core';
import { FormArray, FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-booking',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './booking.html',
  styleUrl: './booking.css',
})
export class BookingComponent implements OnInit {
  bookingForm!: FormGroup;
  flight: any;
  isLoading = false;

  constructor(private fb: FormBuilder, private router: Router, private http: HttpClient) {}

ngOnInit(): void {
  this.flight = history.state?.flight;
  const adults = history.state?.adults || 1;  
  const children = history.state?.children || 0;
  const totalPassengers = adults + children;

  if (!this.flight) {
    alert('No flight selected');
    this.router.navigate(['/flights']);
    return;
  }

  this.bookingForm = this.fb.group({
    emailId: ['', [Validators.required, Validators.email]],
    contactNumber: ['', Validators.required],
    numberOfSeats: [totalPassengers, [Validators.required, Validators.min(1)]],
    passengers: this.fb.array([]),
  });

  for (let i = 0; i < totalPassengers; i++) {
    this.addPassenger();
  }
}

  get passengers(): FormArray {
    return this.bookingForm.get('passengers') as FormArray;
  }

  createPassenger(): FormGroup {
    return this.fb.group({
      passengerName: ['', Validators.required],
      age: ['', Validators.required],
      gender: ['Male', Validators.required],
      seatNum: [''],
      mealPref: ['Veg'],
    });
  }

  addPassenger() {
    this.passengers.push(this.createPassenger());
  }

  removePassenger(index: number) {
    this.passengers.removeAt(index);
  }

  bookFlight() {
    if (this.bookingForm.invalid) {
      this.bookingForm.markAllAsTouched();
      return;
    }

    this.isLoading = true;

    // payload EXACTLY like Postman
    const payload = {
      emailId: this.bookingForm.value.emailId,
      contactNumber: this.bookingForm.value.contactNumber,
      numberOfSeats: this.bookingForm.value.numberOfSeats,
      passengers: this.bookingForm.value.passengers,
    };

    const flightNumber = this.flight.flightNumber;

    this.http.post(`http://localhost:8765/api/flight/booking/${flightNumber}`, payload).subscribe({
      next: (response) => {
        console.log('Booking successful', response);
        this.isLoading = false;
        this.router.navigate(['/profile']);
      },
      error: (err) => {
        console.error('Booking failed', err);
        this.isLoading = false;
        alert('Booking failed. Please try again.');
      },
    });
  }
}
