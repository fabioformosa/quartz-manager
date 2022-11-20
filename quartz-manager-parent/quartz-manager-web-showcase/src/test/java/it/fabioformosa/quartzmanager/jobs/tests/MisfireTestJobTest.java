package it.fabioformosa.quartzmanager.jobs.tests;

import it.fabioformosa.quartzmanager.api.jobs.entities.LogRecord;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class MisfireTestJobTest {

  @Test
  void givenAMisfireTestJob_whenIsExecuted_shoulReturnALogRecord() {
    MisfireTestJob misfireTestJob = new MisfireTestJob(10L);
    LogRecord logRecord = misfireTestJob.doIt(null);
    Assertions.assertThat(logRecord.getMessage()).isEqualTo("Hello!");
  }

}


