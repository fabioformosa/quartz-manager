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
    this.apiService.get(this.config.refresh_token_url).subscribe(res => {
        if (res.accessToken !== null) {
          return this.getUserInfo().toPromise()
            .then(user => {
              this.currentUser = user;
            });
        }
      })
  }

  fetchLoggedUser() {
    this.getUserInfo().subscribe(user => {
      this.currentUser = user;
      this.router.initialNavigation();
    }, err => {
      console.log(`error retrieving current user due to ` + JSON.stringify(err));
      const httpErrorResponse = err as HttpErrorResponse;
      if (httpErrorResponse.status === 404) {
        this.isAnAnonymousUser = true;
        this.router.initialNavigation();
        return;
      }
      if (httpErrorResponse.status !== 401 && (httpErrorResponse.status < 200 || httpErrorResponse.status > 399)) {
        this.router.navigateByUrl('/error');
      }
    });
  }

  getUserInfo() {
    return this.apiService.get(this.config.whoami_url)
      .pipe(map(user => this.currentUser = user));
  }

}
