package it.fabioformosa.quartzmanager.services;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;


class JobServiceTest {

  @Test
  void givenTwoJobClassesInTwoPackages_whenTheJobServiceIsCalled_shouldReturnTwoJobClasses(){
    JobService jobService = new JobService("it.fabioformosa.quartzmanager.jobs, it.fabioformosa.samplepackage");
    jobService.initJobClassList();
    Assertions.assertThat(jobService).isNotNull();
    Assertions.assertThat(jobService.getJobClasses().size()).isEqualTo(2);
  }

  @ParameterizedTest
  @ValueSource(strings = {
    "it.fabioformosa.quartzmanager.jobs",
    "it.fabioformosa.quartzmanager.jobs,",
    ",it.fabioformosa.quartzmanager.jobs"
  })
  void givenOnePackage_whenTheJobServiceIsCalled_shouldReturnOneJobClasses(String packageStr){
    JobService jobService = new JobService(packageStr);
    jobService.initJobClassList();
    Assertions.assertThat(jobService).isNotNull();
    Assertions.assertThat(jobService.getJobClasses().size()).isEqualTo(1);
  }

  @ParameterizedTest
  @ValueSource(strings = {
    "",
    ",",
    ", "
  })
  void givenNoPackages_whenTheJobServiceIsCalled_shouldReturnNoJobClasses(String packageStr){
    JobService jobService = new JobService(packageStr);
    jobService.initJobClassList();
    Assertions.assertThat(jobService).isNotNull();
    Assertions.assertThat(jobService.getJobClasses()).isEmpty();
  }

}
