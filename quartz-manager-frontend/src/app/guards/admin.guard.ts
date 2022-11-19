import {Injectable} from '@angular/core';
import {Router, CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot} from '@angular/router';
import {UserService} from '../services';
import {Observable} from 'rxjs';

@Injectable()
export class AdminGuard implements CanActivate {
  constructor(private router: Router, private userService: UserService) {
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
    if (this.userService.isAnAnonymousUser) {
      return true;
    }
    if (this.userService.currentUser) {
      return true;
      // to be enable again in the scope of the card #65
      // if (JSON.stringify(this.userService.currentUser.authorities).search('ROLE_ADMIN') !== -1) {
      //   return true;
      // } else {
      //   this.router.navigate(['/403']);
      //   return false;
      // }
    } else {
      console.log('NOT AN ADMIN ROLE');
      this.router.navigate(['/login'], {queryParams: {returnUrl: state.url}});
      return false;
    }
  }
}

