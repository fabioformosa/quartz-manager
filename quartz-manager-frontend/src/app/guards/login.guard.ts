import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { UserService } from '../services';
import { Observable } from 'rxjs';

@Injectable()
export class LoginGuard  {

  constructor(private router: Router, private userService: UserService) {}

  canActivate(): boolean {
    if (this.userService.currentUser) {
      return true;
    } else {
      this.router.navigate(['/']);
      return false;
    }
  }
}
