import { Component, OnInit } from '@angular/core';
import {
  ConfigService,
  UserService
} from '../service';

@Component({
  selector: 'manager',
  templateUrl: './manager.component.html',
  styleUrls: ['./manager.component.scss']
})
export class ManagerComponent implements OnInit {

  whoamIResponse = {};
  allUserResponse = {};
  constructor(
    private config: ConfigService,
    private userService: UserService
  ) { }

  ngOnInit() {
  }

}
