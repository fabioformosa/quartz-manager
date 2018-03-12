import {Component} from '@angular/core';

// I remove temporary fontawesome5 and downgrade to fontawesome4
import fontawesome from '@fortawesome/fontawesome';
import solid from '@fortawesome/fontawesome-free-solid/';
fontawesome.library.add(solid);

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})

export class AppComponent {
}
