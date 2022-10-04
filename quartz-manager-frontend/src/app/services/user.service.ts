import {Injectable} from '@angular/core';
import {ApiService} from './api.service';
import {ConfigService} from './config.service';

import {map} from 'rxjs/operators'
import {HttpErrorResponse} from '@angular/common/http';
import {Router} from '@angular/router';

@Injectable()
export class UserService {

  isAnAnonymousUser: boolean;
  currentUser: any;

  constructor(
    private apiService: ApiService,
    private config: ConfigService,
    private router: Router
  ) {
  }

  refreshToken() {
    const promise = this.apiService.get(this.config.refresh_token_url).toPromise()
      .then(res => {
        if (res.access_token !== null) {
          return this.getUserInfo().toPromise()
            .then(user => {
              this.currentUser = user;
            });
        }
      })
      .catch(() => null);
    return promise;
  }

  fetchLoggedUser() {
    this.getUserInfo().subscribe(user => {
      this.currentUser = user;
      this.router.initialNavigation();
    }, err => {
      console.log(`error retrieving current user due to ` + err);
      const httpErrorResponse = err as HttpErrorResponse;
      if (httpErrorResponse.status === 404) {
        this.isAnAnonymousUser = true;
        this.router.initialNavigation();
      }
      // TODO generic error!
    });
  }

  getUserInfo() {
    return this.apiService.get(this.config.whoami_url)
      .pipe(map(user => this.currentUser = user));
  }

}
