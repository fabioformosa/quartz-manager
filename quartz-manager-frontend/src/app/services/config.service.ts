import {Injectable} from '@angular/core';
import {environment} from '../../environments/environment';


const WEBJAR_PATH = '/quartz-manager-ui/';

export const CONTEXT_PATH = '/quartz-manager';

export function getHtmlBaseUrl() {
  const baseUrl = getBaseUrl() || '/';
  return environment.production ? getBaseUrl() + WEBJAR_PATH : '/';
}

export function getBaseUrl() {
  if (environment.production) {
    let contextPath: string = window.location.pathname.split('/')[1] || '';
    if (contextPath && ('/' + contextPath + '/') === WEBJAR_PATH) {
      return '';
    }
    if (contextPath) {
      contextPath = '/' + contextPath;
    }
    return contextPath;
  }
  return '';
}

@Injectable()
export class ConfigService {

  private _auth_url = getBaseUrl() + `${CONTEXT_PATH}/auth`

  private _refresh_token_url = this._auth_url + '/refresh';

  private _login_url = this._auth_url + '/login';

  private _logout_url = this._auth_url + '/logout';

  private _whoami_url = this._auth_url + '/whoami';

  get refresh_token_url(): string {
    return this._refresh_token_url;
  }

  get whoami_url(): string {
    return this._whoami_url;
  }

  get login_url(): string {
    return this._login_url;
  }

  get logout_url(): string {
    return this._logout_url;
  }

}
