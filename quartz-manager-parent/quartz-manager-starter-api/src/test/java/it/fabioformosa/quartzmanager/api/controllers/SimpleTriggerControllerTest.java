package it.fabioformosa.quartzmanager.api.controllers;

import it.fabioformosa.quartzmanager.api.QuartManagerApplicationTests;
import it.fabioformosa.quartzmanager.api.common.utils.DateUtils;
import it.fabioformosa.quartzmanager.api.controllers.utils.TestUtils;
import it.fabioformosa.quartzmanager.api.controllers.utils.TriggerUtils;
import it.fabioformosa.quartzmanager.api.dto.MisfireInstruction;
import it.fabioformosa.quartzmanager.api.dto.SimpleTriggerCommandDTO;
import it.fabioformosa.quartzmanager.api.dto.SimpleTriggerDTO;
import it.fabioformosa.quartzmanager.api.dto.SimpleTriggerInputDTO;
import it.fabioformosa.quartzmanager.api.services.SimpleTriggerService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Date;
import java.util.Map;
import static java.util.Map.entry;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@ContextConfiguration(classes = {QuartManagerApplicationTests.class})
@WebMvcTest(controllers = SimpleTriggerController.class, properties = {
  "quartz-manager.jobClassPackages=it.fabioformosa.quartzmanager.jobs"
})
class SimpleTriggerControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private SimpleTriggerService simpleTriggerService;

  @AfterEach
  void cleanUp(){
    Mockito.reset(simpleTriggerService);
  }

  @Test
  void whenGetIsCalled_thenASimpleTriggerIsReturned() throws Exception {
    SimpleTriggerDTO expectedSimpleTriggerDTO = TriggerUtils.getSimpleTriggerInstance("mytrigger");
    Mockito.when(simpleTriggerService.getSimpleTriggerByName("mytrigger")).thenReturn(expectedSimpleTriggerDTO);

    mockMvc.perform(get(SimpleTriggerController.SIMPLE_TRIGGER_CONTROLLER_BASE_URL + "/mytrigger")
        .contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isOk())
      .andExpect(MockMvcResultMatchers.content().json(TestUtils.toJson(expectedSimpleTriggerDTO)));
  }

  @Test
  void givenACompleteSimpleTriggerCommandDTO_whenPosted_thenANewSimpleTriggerIsCreated() throws Exception {
    SimpleTriggerInputDTO simpleTriggerInputDTO = buildACompleteSimpleTriggerCommandDTO();
    SimpleTriggerDTO expectedSimpleTriggerDTO = TriggerUtils.getSimpleTriggerInstance("mytrigger", simpleTriggerInputDTO);
    Mockito.when(simpleTriggerService.scheduleSimpleTrigger(any())).thenReturn(expectedSimpleTriggerDTO);
    mockMvc.perform(
      post(SimpleTriggerController.SIMPLE_TRIGGER_CONTROLLER_BASE_URL + "/mytrigger")
        .contentType(MediaType.APPLICATION_JSON)
        .content(TestUtils.toJson(simpleTriggerInputDTO))
      )
      .andExpect(MockMvcResultMatchers.status().isCreated())
      .andExpect(MockMvcResultMatchers.content().json(TestUtils.toJson(expectedSimpleTriggerDTO)))
    ;
  }

  private SimpleTriggerInputDTO buildACompleteSimpleTriggerCommandDTO() {
    Map<String, ?> triggerJobDataMap = Map.ofEntries(
        entry("customTriggerData1", "value1"),
        entry("customTriggerData2",  "value2")
    );
    return SimpleTriggerInputDTO.builder()
      .jobClass("it.fabioformosa.quartzmanager.api.jobs.SampleJob")
      .startDate(new Date())
      .endDate(DateUtils.addHoursToNow(6))
      .misfireInstruction(MisfireInstruction.MISFIRE_INSTRUCTION_FIRE_NOW)
      .repeatCount(5)
      .repeatInterval(1000L * 60 * 60)
      .jobDataMap(triggerJobDataMap)
      .build();
  }

  @Test
  void givenATriggerName_whenPutSimpleTriggerCommandDTO_thenTheSimpleTriggerIsRescheduled() throws Exception {
    SimpleTriggerInputDTO simpleTriggerInputDTO = buildACompleteSimpleTriggerCommandDTO();
    SimpleTriggerDTO expectedSimpleTriggerDTO = TriggerUtils.getSimpleTriggerInstance("mytrigger", simpleTriggerInputDTO);
    SimpleTriggerCommandDTO simpleTriggerCommandDTO = SimpleTriggerCommandDTO.builder()
      .triggerName("mytrigger")
      .simpleTriggerInputDTO(simpleTriggerInputDTO)
      .build();
    Mockito.when(simpleTriggerService.rescheduleSimpleTrigger(simpleTriggerCommandDTO)).thenReturn(expectedSimpleTriggerDTO);

    mockMvc.perform(put(SimpleTriggerController.SIMPLE_TRIGGER_CONTROLLER_BASE_URL + "/mytrigger")
      .contentType(MediaType.APPLICATION_JSON)
      .content(TestUtils.toJson(simpleTriggerInputDTO)))
      .andExpect(MockMvcResultMatchers.status().isOk())
      .andExpect(MockMvcResultMatchers.content().json(TestUtils.toJson(expectedSimpleTriggerDTO)));
  }

}
