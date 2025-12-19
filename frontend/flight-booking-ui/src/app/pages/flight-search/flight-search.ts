import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';

@Component({
  selector: 'app-flight-search',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './flight-search.html',
})
export class FlightSearchComponent implements OnInit {
  // form fields
  from = '';
  to = '';
  date = '';
  adults = 1;
  children = 0;

  // New property to store today's date for the 'min' attribute
  minDate = '';

  // results
  flights: any[] = [];
  isLoading = false;
  constructor(private http: HttpClient, private router: Router) {}

  ngOnInit() {
    this.setMinDate();
  }

  private setMinDate() {
    const today = new Date();
    const dd = String(today.getDate()).padStart(2, '0');
    const mm = String(today.getMonth() + 1).padStart(2, '0');
    const yyyy = today.getFullYear();

    this.minDate = `${yyyy}-${mm}-${dd}`;
  }

  search() {
    // payload EXACTLY like Postman
    const payload = {
      departingAirport: this.from,
      arrivalAirport: this.to,
      departDate: this.date,
      passengers: {
        adults: this.adults,
        children: this.children,
      },
    };

    this.http.post<any>('http://localhost:8765/api/flight/search', payload).subscribe({
      next: (response) => {
        this.flights = response.flights;
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Database fetch failed', err);
        this.isLoading = false;
        alert('Error fetching flights. Please check if the backend is running.');
      },
    });
  }

  selectFlight(flight: any) {
    this.router.navigate(['/booking'], {
      state: {
        flight: flight,
        adults: this.adults,
        children: this.children,
      },
    });
  }
}
