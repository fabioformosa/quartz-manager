import {RxStomp, RxStompConfig} from '@stomp/rx-stomp';

export class RxStompService extends RxStomp {

  constructor(rxStompConfig: RxStompConfig) {
    super();
    super.configure(rxStompConfig);
    super.activate();
  }

}
