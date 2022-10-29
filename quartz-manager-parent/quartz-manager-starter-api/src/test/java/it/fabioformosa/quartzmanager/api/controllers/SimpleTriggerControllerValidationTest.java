package it.fabioformosa.quartzmanager.api.controllers;

import it.fabioformosa.quartzmanager.api.QuartManagerApplicationTests;
import it.fabioformosa.quartzmanager.api.controllers.utils.InvalidSimpleTriggerCommandDTOProvider;
import it.fabioformosa.quartzmanager.api.controllers.utils.TestUtils;
import it.fabioformosa.quartzmanager.api.controllers.utils.TriggerUtils;
import it.fabioformosa.quartzmanager.api.dto.SimpleTriggerDTO;
import it.fabioformosa.quartzmanager.api.dto.SimpleTriggerInputDTO;
import it.fabioformosa.quartzmanager.api.exceptions.TriggerNotFoundException;
import it.fabioformosa.quartzmanager.api.services.SimpleTriggerService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@ContextConfiguration(classes = {QuartManagerApplicationTests.class})
@WebMvcTest(controllers = SimpleTriggerController.class, properties = {
  "quartz-manager.jobClassPackages=it.fabioformosa.quartzmanager.jobs"
})
class SimpleTriggerControllerValidationTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private SimpleTriggerService simpleTriggerService;

  @AfterEach
  void cleanUp(){
    Mockito.reset(simpleTriggerService);
  }


  @Test
  void givenANotExistingTrigger_whenGetIsCalled_then404IsReturned() throws Exception {
    Mockito.when(simpleTriggerService.getSimpleTriggerByName("not_existing_trigger_name")).thenThrow(new TriggerNotFoundException("not_existing_trigger_name"));

    mockMvc.perform(get(SimpleTriggerController.SIMPLE_TRIGGER_CONTROLLER_BASE_URL + "/not_existing_trigger_name")
        .contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isNotFound());
  }


  @Test
  void givenAMinimalSimpleTriggerCommandDTO_whenPosted_thenANewSimpleTriggerIsCreated() throws Exception {
    SimpleTriggerInputDTO simpleTriggerInputDTO = buildAMinimalSimpleTriggerCommandDTO();
    SimpleTriggerDTO expectedSimpleTriggerDTO = TriggerUtils.getSimpleTriggerInstance("my-minimal-trigger");
    Mockito.when(simpleTriggerService.scheduleSimpleTrigger(any())).thenReturn(expectedSimpleTriggerDTO);
    mockMvc.perform(
        post(SimpleTriggerController.SIMPLE_TRIGGER_CONTROLLER_BASE_URL + "/my-minimal-trigger")
          .contentType(MediaType.APPLICATION_JSON)
          .content(TestUtils.toJson(simpleTriggerInputDTO))
      )
      .andExpect(MockMvcResultMatchers.status().isCreated())
      .andExpect(MockMvcResultMatchers.content().json(TestUtils.toJson(expectedSimpleTriggerDTO)))
    ;
  }

  private SimpleTriggerInputDTO buildAMinimalSimpleTriggerCommandDTO() {
    return SimpleTriggerInputDTO.builder()
      .jobClass("it.fabioformosa.quartzmanager.api.jobs.SampleJob")
      .build();
  }

  @Test
  void givenStartDateAndEndDateEqual_whenScheduled_thenANewSimpleTriggerIsCreated() throws Exception {
    SimpleTriggerInputDTO simpleTriggerInputDTO = buildAMinimalSimpleTriggerCommandDTO();
    Date now = new Date();
    simpleTriggerInputDTO.setStartDate(now);
    simpleTriggerInputDTO.setEndDate(now);
    SimpleTriggerDTO expectedSimpleTriggerDTO = TriggerUtils.getSimpleTriggerInstance("my-puntual-trigger");
    Mockito.when(simpleTriggerService.scheduleSimpleTrigger(any())).thenReturn(expectedSimpleTriggerDTO);
    mockMvc.perform(
        post(SimpleTriggerController.SIMPLE_TRIGGER_CONTROLLER_BASE_URL + "/my-puntual-trigger")
          .contentType(MediaType.APPLICATION_JSON)
          .content(TestUtils.toJson(simpleTriggerInputDTO))
      )
      .andExpect(MockMvcResultMatchers.status().isCreated())
      .andExpect(MockMvcResultMatchers.content().json(TestUtils.toJson(expectedSimpleTriggerDTO)))
    ;
  }

  @ParameterizedTest
  @ArgumentsSource(InvalidSimpleTriggerCommandDTOProvider.class)
  void givenAnInvalidSimpleTriggerCommandDTO_whenPostedANewTrigger_thenAnErrorIsReturned(SimpleTriggerInputDTO invalidSimpleTriggerComandDTO) throws Exception {
    mockMvc.perform(post(SimpleTriggerController.SIMPLE_TRIGGER_CONTROLLER_BASE_URL + "/mytrigger")
      .contentType(MediaType.APPLICATION_JSON)
      .content(TestUtils.toJson(invalidSimpleTriggerComandDTO)))
      .andExpect(MockMvcResultMatchers.status().is4xxClientError());
  }

  @ParameterizedTest
  @ArgumentsSource(InvalidSimpleTriggerCommandDTOProvider.class)
  void givenAnInvalidSimpleTriggerCommandDTO_whenATriggerIsRescheduled_thenAnErrorIsReturned(SimpleTriggerInputDTO invalidSimpleTriggerCommandTO) throws Exception {
    mockMvc.perform(put(SimpleTriggerController.SIMPLE_TRIGGER_CONTROLLER_BASE_URL + "/mytrigger")
        .contentType(MediaType.APPLICATION_JSON)
        .content(TestUtils.toJson(invalidSimpleTriggerCommandTO)))
      .andExpect(MockMvcResultMatchers.status().is4xxClientError());
  }

}
