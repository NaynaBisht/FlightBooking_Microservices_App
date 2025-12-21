import { Component, OnInit, PLATFORM_ID, Inject } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { ChangeDetectorRef } from '@angular/core';

@Component({
  selector: 'app-mybookings',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './MyBookings.html',
})
export class MyBookingsComponent implements OnInit {
  bookings: any[] = [];
  isLoading = true;

  showCancelDialog = false;
  selectedBookingId: any = null;

  constructor(
    private http: HttpClient,
    private cdr: ChangeDetectorRef,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {}

  ngOnInit(): void {
    this.fetchUserBookings();
  }

  fetchUserBookings() {
    if (isPlatformBrowser(this.platformId)) {
      const userJson = localStorage.getItem('user');
      const token = localStorage.getItem('token');
      let userEmail = '';

      if (userJson) {
        const userData = JSON.parse(userJson);
        userEmail = userData.email;
      }

      if (!userEmail || !token) {
        this.isLoading = false;
        return;
      }

      const headers = new HttpHeaders({
        Authorization: `Bearer ${token}`,
      });

      this.http
        .get<any[]>(`http://localhost:8765/api/flight/booking/history/${userEmail}`, { headers })
        .subscribe({
          next: (data) => {
            this.bookings = data;
            this.isLoading = false;
            this.cdr.detectChanges();
          },
          error: (err) => {
            console.error('Error fetching bookings', err);
            this.isLoading = false;
          },
        });
    }
  }

  canCancel(departureTime: string): boolean {
    if (!departureTime) return false;
    const flightDateTime = new Date(departureTime);
    const now = new Date();
    const diffInMs = flightDateTime.getTime() - now.getTime();
    const hoursDifference = diffInMs / (1000 * 60 * 60);
    return hoursDifference > 24;
  }

  // cancelBooking(bookingId: any) {
  //   if (confirm('Are you sure you want to cancel?')) {
  //     if (isPlatformBrowser(this.platformId)) {
  //       const token = localStorage.getItem('token');
  //       const headers = new HttpHeaders({ Authorization: `Bearer ${token}` });

  //       this.http
  //         .delete(`http://localhost:8765/api/flight/booking/cancel/${bookingId}`, { headers })
  //         .subscribe({
  //           next: () => {
  //             alert('Cancelled successfully');
  //             this.bookings = this.bookings.map(b =>
  //               b.pnr === bookingId ? { ...b, status: 'CANCELLED' } : b
  //             );

  //             this.cdr.detectChanges();
  //           },
  //           error: (err) => {
  //             console.error('Cancellation failed', err);
  //             alert('Failed to cancel. Please try again later.');
  //           }
  //         });
  //     }
  //   }
  // }

  openCancelDialog(bookingId: any) {
    this.selectedBookingId = bookingId;
    this.showCancelDialog = true;
  }

  confirmCancel() {
    this.showCancelDialog = false;

    if (isPlatformBrowser(this.platformId)) {
      const token = localStorage.getItem('token');
      const headers = new HttpHeaders({ Authorization: `Bearer ${token}` });

      this.http
        .delete(`http://localhost:8765/api/flight/booking/cancel/${this.selectedBookingId}`, {
          headers,
        })
        .subscribe({
          next: () => {
            this.bookings = this.bookings.map((b) =>
              b.pnr === this.selectedBookingId ? { ...b, status: 'CANCELLED' } : b
            );
            this.cdr.detectChanges();
          },
          error: () => {
            alert('Failed to cancel. Please try again later.');
          },
        });
    }
  }

  closeDialog() {
    this.showCancelDialog = false;
    this.selectedBookingId = null;
  }
}
