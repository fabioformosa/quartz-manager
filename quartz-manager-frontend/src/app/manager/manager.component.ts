import { Component, OnInit } from '@angular/core';
import {
  FooService,
  ConfigService,
  UserService
} from '../service';

@Component({
  selector: 'manager',
  templateUrl: './manager.component.html',
  styleUrls: ['./manager.component.scss']
})
export class ManagerComponent implements OnInit {

  fooResponse = {};
  whoamIResponse = {};
  allUserResponse = {};
  constructor(
    private config: ConfigService,
    private fooService: FooService,
    private userService: UserService
  ) { }

  ngOnInit() {
  }

}
