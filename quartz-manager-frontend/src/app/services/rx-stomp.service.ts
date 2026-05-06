import {RxStomp} from '@stomp/rx-stomp';
import {RxStompConfig} from '@stomp/rx-stomp/esm6/rx-stomp-config';

export class RxStompService extends RxStomp {

  constructor(rxStompConfig: RxStompConfig) {
    super();
    super.configure(rxStompConfig);
    super.activate();
  }

}
