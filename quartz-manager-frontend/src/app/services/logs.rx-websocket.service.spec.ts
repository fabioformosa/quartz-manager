import { TestBed } from '@angular/core/testing';

import { LogsRxWebsocketService } from './logs.rx-websocket.service';
import {ApiService} from './api.service';
import {HttpClientTestingModule} from '@angular/common/http/testing';

describe('LogsRxWebsocketService', () => {
  let service: LogsRxWebsocketService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [ApiService]
    });
    service = TestBed.inject(LogsRxWebsocketService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
