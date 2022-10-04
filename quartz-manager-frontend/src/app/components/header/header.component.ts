import { Component, OnInit } from '@angular/core';
import {
  UserService,
  AuthService,
  // NO_AUTH
} from '../../services';
import { Router } from '@angular/router';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit {

  constructor(
    private userService: UserService,
    private authService: AuthService,
    private router: Router
  ) { }

  ngOnInit() {
  }

  logout() {
    this.authService.logout().subscribe(res => {
      this.router.navigate(['/login']);
    });
  }

  hasSignedIn() {
    return !!this.userService.currentUser;
  }

  noAuthenticationRequired = () => !this.hasSignedIn() && this.userService.isAnAnonymousUser === true;


  userName() {
    const user = this.userService.currentUser;
    return user.username;
  }

}
