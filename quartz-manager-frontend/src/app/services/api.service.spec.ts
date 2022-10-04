import {TestBed} from '@angular/core/testing';
import {HttpClientTestingModule, HttpTestingController} from '@angular/common/http/testing';
import {ApiService} from './api.service';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Router} from '@angular/router';
import {jest} from '@jest/globals'

class Data {
  name: string
}

class HttpResponseMock {
  constructor(
    public body: unknown,
    public opts?: {
      headers?:
        | HttpHeaders
        | {
        [name: string]: string | string[];
      };
      status?: number;
      statusText?: string;
    }
  ) {
  }
}

const routerSpy = jest.spyOn(Router.prototype, 'navigateByUrl');

describe('ApiServiceTest', () => {

  let apiService: ApiService;
  let httpClient: HttpClient;
  let httpTestingController: HttpTestingController;

  const SAMPLE_URL = '/sample-url';
  const URL_401 = '/url-response-401';
  const testData: Data = {name: 'Test Data'};

  beforeEach(() => {

    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [ApiService, {provide: Router, useValue: routerSpy}]
    });
    apiService = TestBed.inject(ApiService);

    httpClient = TestBed.inject(HttpClient);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  it('should be created', (): void => {
    expect(apiService).toBeTruthy();
  });

  it('can test HttpClient.get', (): void => {

    apiService.get(SAMPLE_URL).subscribe((res: Data) => {
      expect(res).toEqual(testData);
    });

    const req = httpTestingController.expectOne(SAMPLE_URL)
    expect(req.request.method).toEqual('GET');
    req.flush(new HttpResponseMock(testData));
    httpTestingController.verify();
  });

  it('doesn\'t do anything if 401 is received', (): void => {

    apiService.get(URL_401).subscribe((res: Data) => {
      expect(false);
    }, (error) => {
      expect(error.status).toBe(401);
      expect(routerSpy).toHaveBeenCalledTimes(1);
    });

    const req = httpTestingController.expectOne(URL_401)
    expect(req.request.method).toEqual('GET');
    req.flush(null, {status: 401, statusText: 'unauthenticated'});
    httpTestingController.verify();
  });

});
