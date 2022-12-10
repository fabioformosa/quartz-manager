import {Component, OnDestroy, OnInit} from '@angular/core';
import {UntypedFormBuilder, UntypedFormGroup, Validators} from '@angular/forms';
import {ActivatedRoute, Router} from '@angular/router';
import {DisplayMessage} from '../../shared/models/display-message';
import {Subject} from 'rxjs';
import {delay, takeUntil} from 'rxjs/operators'

import {AuthService, UserService} from '../../services';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit, OnDestroy {
  title = 'Login';
  githubLink = 'https://github.com/fabioformosa/quartz-manager';
  form: UntypedFormGroup;

  submitted = false;

  notification: DisplayMessage;

  returnUrl: string;
  private ngUnsubscribe: Subject<void> = new Subject<void>();

  constructor(
    private userService: UserService,
    private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute,
    private formBuilder: UntypedFormBuilder
  ) {

  }

  ngOnInit() {
    this.route.params
        .pipe(takeUntil(this.ngUnsubscribe))
        .subscribe((params: DisplayMessage) => {
          this.notification = params;
        });
        this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/';
        this.form = this.formBuilder.group({
          username: ['', Validators.compose([Validators.required, Validators.minLength(3), Validators.maxLength(64)])],
          password: ['', Validators.compose([Validators.required, Validators.minLength(3), Validators.maxLength(48)])]
        });
  }

  ngOnDestroy() {
    this.ngUnsubscribe.next();
    this.ngUnsubscribe.complete();
  }

  repository() {
    window.location.href = this.githubLink;
  }

  onSubmit() {
    this.notification = undefined;
    this.submitted = true;

    this.authService.login(this.form.value)
    .pipe(delay(1000))
    .subscribe(data => {
      this.router.navigate([this.returnUrl]);
    },
    error => {
      this.submitted = false;
      this.notification = { msgType: 'error', msgBody: 'Incorrect username or password.' };
    });

  }


}
