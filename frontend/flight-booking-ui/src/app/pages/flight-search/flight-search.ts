import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-flight-search',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './flight-search.html'
})
export class FlightSearchComponent {

  // form fields
  from = '';
  to = '';
  date = '';
  adults = 1;
  children = 0;

  // results
  flights: any[] = [];

  search() {
    // 🔹 payload EXACTLY like Postman
    const payload = {
      departingAirport: this.from,
      arrivalAirport: this.to,
      departDate: this.date,
      passengers: {
        adults: this.adults,
        children: this.children
      }
    };

    console.log('Flight search payload:', payload);

    // 🔹 mock response (same structure UI expects)
    this.flights = [
      {
        flightNo: 'AI101',
        departure: this.from,
        arrival: this.to,
        price: 4500
      },
      {
        flightNo: 'IND202',
        departure: this.from,
        arrival: this.to,
        price: 5200
      }
    ];
  }
}
