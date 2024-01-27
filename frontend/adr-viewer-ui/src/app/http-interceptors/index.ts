import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { CachingInterceptor } from '../services/caching-interceptor.service';

/**
 * Configurations of the HTTP interceptor providers
 */
export const ADR_HTTP_INTERCEPTOR_PROVIDERS = [
  { provide: HTTP_INTERCEPTORS, useClass: CachingInterceptor, multi: true }
];

