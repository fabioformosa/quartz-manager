import {Component} from '@angular/core';
import {Router} from '@angular/router';

import fontawesome from '@fortawesome/fontawesome';
import {
  faCheckCircle,
  faExclamationCircle,
  faExclamationTriangle,
  faPause,
  faPlay,
  faTimesCircle
} from '@fortawesome/fontawesome-free-solid';

fontawesome.library.add(faCheckCircle, faExclamationCircle, faExclamationTriangle, faPause, faPlay, faTimesCircle);

@Component({
    selector: 'app-root',
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.scss'],
    standalone: false
})

export class AppComponent {
  constructor(private router: Router) {
  }

  isOperationsConsoleRoute(): boolean {
    const url = this.router.url || '/';
    return url === '/' || url.startsWith('/manager');
  }
}
