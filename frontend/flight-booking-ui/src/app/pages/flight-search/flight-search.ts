import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { ChangeDetectorRef } from '@angular/core';

const AVAILABLE_CITIES = ['SRI', 'AMR', 'KRL', 'KOL', 'PUN', 'GOA'];

@Component({
  selector: 'app-flight-search',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './flight-search.html',
})
export class FlightSearchComponent implements OnInit {
  from = '';
  to = '';
  date = '';
  adults = 1;
  children = 0;

  minDate = '';

  flights: any[] = [];

  cities = AVAILABLE_CITIES;

  isLoading = false;
  constructor(
    private http: HttpClient, 
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

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
    this.isLoading = true;
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
        this.cdr.detectChanges();
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
