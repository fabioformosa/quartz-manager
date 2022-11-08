package it.fabioformosa.quartzmanager.api.services;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;


class JobServiceTest {

  @Test
  void givenTwoJobClassesInTwoPackages_whenTheJobServiceIsCalled_shouldReturnTwoJobClasses(){
    JobService jobService = new JobService("it.fabioformosa.quartzmanager.api.jobs, it.fabioformosa.samplepackage");
    jobService.initJobClassList();
    Assertions.assertThat(jobService).isNotNull();
    Assertions.assertThat(jobService.getJobClasses()).hasSize(2);
  }

  @ParameterizedTest
  @ValueSource(strings = {
    "it.fabioformosa.quartzmanager.api.jobs",
    "it.fabioformosa.quartzmanager.api.jobs,",
    ",it.fabioformosa.quartzmanager.api.jobs"
  })
  void givenOnePackage_whenTheJobServiceIsCalled_shouldReturnOneJobClasses(String packageStr){
    JobService jobService = new JobService(packageStr);
    jobService.initJobClassList();
    Assertions.assertThat(jobService).isNotNull();
    Assertions.assertThat(jobService.getJobClasses()).hasSize(1);
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
