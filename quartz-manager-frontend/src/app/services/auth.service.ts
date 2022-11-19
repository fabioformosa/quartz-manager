import {Injectable} from '@angular/core';
import {HttpHeaders, HttpResponse} from '@angular/common/http';
import {ApiService} from './api.service';
import {UserService} from './user.service';
import {ConfigService} from './config.service';
import {map} from 'rxjs/operators';

@Injectable()
export class AuthService {

  constructor(
    private apiService: ApiService,
    private userService: UserService,
    private config: ConfigService,
  ) {
  }

  login(user) {
    const loginHeaders = new HttpHeaders({
      'Accept': 'application/json',
      'Content-Type': 'application/x-www-form-urlencoded'
    });
    const body = `username=${user.username}&password=${user.password}`;
    return this.apiService.post(this.config.login_url, body, loginHeaders)
      .pipe(
        map(() => {
          console.log('Login success');
          this.userService.getUserInfo().subscribe();
        })
      );
  }

  logout() {
    return this.apiService.post(this.config.logout_url, {})
      .pipe(map(() => {
        this.apiService.setToken(null);
        this.userService.currentUser = null;
      }));
  }

}
