import { ApplicationConfig } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withInterceptors, withFetch } from '@angular/common/http';
import { routes } from './app.routes';
import { jwtInterceptor } from './interceptors/jwt.interceptor';

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),
    // Setup HTTP Client with Interceptor and Fetch API
    provideHttpClient(
      withFetch(), // Standard for modern Angular
      withInterceptors([jwtInterceptor])
    )
  ]
};