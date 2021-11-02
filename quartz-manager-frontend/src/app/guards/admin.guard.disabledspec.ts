import { TestBed, async, inject } from '@angular/core/testing';
import { Router } from '@angular/router';
import { NO_AUTH, UserService } from '../services';
import { AdminGuard } from './admin.guard';
import {jest} from '@jest/globals'

export class RouterStub {
  navigate(commands?: any[], extras?: any) {}
}

const RouterSpy = jest.spyOn(RouterStub.prototype, 'navigate');

const MockUserServiceNoAuth = jest.fn(() => ({currentUser: NO_AUTH}));
const MockUserService = jest.fn(() => ({
  currentUser: {
    authorities: ['ROLE_ADMIN']
  }
}));
const MockUserServiceForbidden = jest.fn(() => ({
  currentUser: {
    authorities: ['ROLE_GUEST']
  }
}));

// describe('AdminGuard NoAuth', () => {
//   beforeEach(() => {
//     TestBed.configureTestingModule({
//       providers: [
//         AdminGuard,
//         {
//           provide: Router,
//           useClass: RouterStub
//         },
//         {
//           provide: UserService,
//           useClass: MockUserServiceNoAuth
//         }
//       ]
//     });
//   });
//
//   test.skip('should run', inject([AdminGuard], (guard: AdminGuard) => {
//     expect(guard).toBeTruthy();
//   }));
//
//   test.skip('returns true if user is NO_AUTH', inject([AdminGuard], (guard: AdminGuard) => {
//     expect(guard.canActivate(null, null)).toBeTruthy();
//   }));
//
// });

// describe('AdminGuard activates the route', () => {
//   beforeEach(() => {
//     TestBed.configureTestingModule({
//       providers: [
//         AdminGuard,
//         {
//           provide: Router,
//           useClass: RouterStub
//         },
//         {
//           provide: UserService,
//           useClass: MockUserService
//         }
//       ]
//     });
//   });
//
//   test.skip('should run', inject([AdminGuard], (guard: AdminGuard) => {
//     expect(guard).toBeTruthy();
//   }));
//
//   test.skip('returns true if user has admin role', inject([AdminGuard], (guard: AdminGuard) => {
//     expect(guard.canActivate(null, null)).toBeTruthy();
//   }));
//
// });

// describe('AdminGuard redirects to 403', () => {
//   beforeEach(() => {
//     TestBed.configureTestingModule({
//       providers: [
//         AdminGuard,
//         {
//           provide: Router,
//           useClass: RouterStub
//         },
//         {
//           provide: UserService,
//           useClass: MockUserServiceForbidden
//         }
//       ]
//     });
//   });
//
//   test.skip('should run', inject([AdminGuard], (guard: AdminGuard) => {
//     expect(guard).toBeTruthy();
//   }));
//
//   test.skip('returns false if user is not authorized', inject([AdminGuard], (guard: AdminGuard) => {
//     expect(guard.canActivate(null, null)).toBeFalsy();
//     expect(RouterSpy).toHaveBeenCalledTimes(1);
//   }));
//
// });
