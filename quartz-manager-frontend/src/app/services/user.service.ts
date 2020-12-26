import { Injectable } from '@angular/core';
import { ApiService } from './api.service';
import { ConfigService } from './config.service';

import { map } from 'rxjs/operators'

export const NO_AUTH: string = 'NO_AUTH'

@Injectable()
export class UserService {

  currentUser;

  constructor(
    private apiService: ApiService,
    private config: ConfigService
  ) { }

  jwtInitUser() {
    const promise = this.apiService.get(this.config.refresh_token_url).toPromise()
    .then(res => {
      if (res.access_token !== null) {
        return this.getMyInfo().toPromise()
        .then(user => {
          this.currentUser = user;
        });
      }
    })
    .catch(() => null);
    return promise;
  }

  jsessionInitUser() {
    return this.getMyInfo().toPromise()
        .then(user => {
          this.currentUser = user;
        }, err => {
          //not logged
          console.log(`error retrieving current user due to ` + err);
        });
  }

  resetCredentials() {
    return this.apiService.get(this.config.reset_credentials_url);
  }

  getMyInfo() {
    return this.apiService.get(this.config.whoami_url).pipe(map(user => this.currentUser = user));
  }

  getAll() {
    return this.apiService.get(this.config.users_url);
  }

}
