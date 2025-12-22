import { Routes } from '@angular/router';
import { LoginComponent } from './pages/login/login';
import { RegisterComponent } from './pages/register/register';
import { FlightSearchComponent } from './pages/flight-search/flight-search';
import { authGuard } from './guards/auth.guard';
import { BookingComponent } from './pages/booking/booking';
import { MyBookingsComponent } from './pages/MyBookings/MyBookings';
import { AddflightComponent } from './pages/addflight/addflight';
import { ProfileComponent } from './pages/profile/profile';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },

  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },

  { path: 'flights', component: FlightSearchComponent },

  { path: 'booking', component: BookingComponent },

  { path: 'mybookings', component: MyBookingsComponent },

  { path: 'addflight', component: AddflightComponent },

  { path: 'profile', component: ProfileComponent }
];
