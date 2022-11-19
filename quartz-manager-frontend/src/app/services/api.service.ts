import {HttpClient, HttpHeaders, HttpResponse, HttpRequest, HttpEventType, HttpParams} from '@angular/common/http';
import {Router} from '@angular/router';
import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {catchError, map, filter, tap} from 'rxjs/operators'
import {serialize} from '../shared/utilities/serialize';

export enum RequestMethod {
  Get = 'GET',
  Head = 'HEAD',
  Post = 'POST',
  Put = 'PUT',
  Delete = 'DELETE',
  Options = 'OPTIONS',
  Patch = 'PATCH'
}

@Injectable()
export class ApiService {

  headers = new HttpHeaders({
    'Accept': 'application/json',
    'Content-Type': 'application/json'
  });

  private jwtToken: string;

  private static extractTokenFromHttpResponse(res: HttpResponse<any>): string {
    let authorization: string = null;
    const headers: HttpHeaders = res.headers;
    if (headers && headers.has('Authorization')) {
      authorization = headers.get('Authorization');
      if (authorization.startsWith('Bearer ')) {
        authorization = authorization.substring(7);
      }
    }
    return authorization;
  }

  constructor(private http: HttpClient, private router: Router) {
  }

  setToken(token: string) {
    this.jwtToken = token;
  }

  getToken = () => this.jwtToken;

  get(path: string, args?: any): Observable<any> {
    const options = {
      headers: this.headers,
      withCredentials: true
    };

    if (args) {
      options['params'] = serialize(args);
    }

    return this.http.get(path, options)
      .pipe(catchError(this.checkError.bind(this)));
  }

  post(path: string, body: any, customHeaders?: HttpHeaders): Observable<any> {
    return this.request(path, body, RequestMethod.Post, customHeaders);
  }

  put(path: string, body: any): Observable<any> {
    return this.request(path, body, RequestMethod.Put);
  }

  delete(path: string, body?: any): Observable<any> {
    return this.request(path, body, RequestMethod.Delete);
  }

  private request(path: string, body: any, method = RequestMethod.Post, customHeaders?: HttpHeaders): Observable<any> {
    const options = {
      headers: customHeaders || this.headers,
      withCredentials: true
    }

    const req = new HttpRequest(method, path, body, options);

    return this.http.request(req)
      .pipe(
        filter(response => response instanceof HttpResponse),
        tap((resp: HttpResponse<any>) => {
          const jwtToken = ApiService.extractTokenFromHttpResponse(resp);
          if (jwtToken) {
            this.setToken(jwtToken);
          }
        }),
        map((response: HttpResponse<any>) => response.body),
        catchError(error => this.checkError(error))
      )
  }

  // Display error if logged in, otherwise redirect to IDP
  private checkError(error: any): any {
    if (error && error.status === 401) {
      this.router.navigate(['/login']);
    } else {
      // this.displayError(error);
    }
    throw error;
  }


}
