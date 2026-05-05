import { TestBed } from '@angular/core/testing';
import {RxStomp} from '@stomp/rx-stomp';
import {jest} from '@jest/globals';

import { ProgressRxWebsocketService } from './progress.rx-websocket.service';
import {ApiService} from './api.service';

describe('ProgressRxWebsocketService', () => {
  let service: ProgressRxWebsocketService;
  let configureSpy;
  let activateSpy;

  beforeEach(() => {
    configureSpy = jest.spyOn(RxStomp.prototype, 'configure');
    activateSpy = jest.spyOn(RxStomp.prototype, 'activate').mockImplementation(() => undefined);

    TestBed.configureTestingModule({
      providers: [
        {provide: ApiService, useValue: {getToken: () => 'test-token'}}
      ]
    });
    service = TestBed.inject(ProgressRxWebsocketService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should configure rx-stomp with the progress websocket endpoint', () => {
    expect(configureSpy).toHaveBeenCalled();
    expect(activateSpy).toHaveBeenCalled();

    const config = configureSpy.mock.calls[configureSpy.mock.calls.length - 1][0];
    expect(config.heartbeatIncoming).toEqual(0);
    expect(config.heartbeatOutgoing).toEqual(20000);
    expect(config.reconnectDelay).toEqual(200);
    expect(config.webSocketFactory.toString()).toContain('/progress?access_token=');
  });
});
