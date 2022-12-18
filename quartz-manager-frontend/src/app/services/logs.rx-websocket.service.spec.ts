import { TestBed } from '@angular/core/testing';

import { LogsRxWebsocketService } from './logs.rx-websocket.service';

describe('LogsRxWebsocketService', () => {
  let service: LogsRxWebsocketService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(LogsRxWebsocketService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
