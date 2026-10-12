import { HttpInterceptorFn } from '@angular/common/http';
import {inject} from '@angular/core';
import {LoadingService} from '../services/loading';
import {finalize} from 'rxjs';

export const loadingInterceptorInterceptor: HttpInterceptorFn = (req, next) => {
  const loading=inject(LoadingService);
  loading.setLoading(true);
  return next(req).pipe(finalize(()=>
  {
    loading.setLoading(false);
  }));
};
