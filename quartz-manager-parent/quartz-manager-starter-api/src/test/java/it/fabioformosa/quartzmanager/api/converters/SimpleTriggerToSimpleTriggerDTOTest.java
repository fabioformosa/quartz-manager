package it.fabioformosa.quartzmanager.api.converters;

import it.fabioformosa.quartzmanager.api.controllers.utils.TriggerUtils;
import it.fabioformosa.quartzmanager.api.dto.SimpleTriggerDTO;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.quartz.SimpleTrigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.convert.ConversionService;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
class SimpleTriggerToSimpleTriggerDTOTest {

  @Autowired
  private ConversionService conversionService;

  @Order(1)
  @Test
  void givenSimpleTrigger_whenItIsConverted_thenADtoIsReturned() {
    SimpleTrigger simpleTrigger = TriggerUtils.buildSimpleTrigger();
    SimpleTriggerDTO simpleTriggerDTO = conversionService.convert(simpleTrigger, SimpleTriggerDTO.class);
    Assertions.assertThat(simpleTriggerDTO).isNotNull();
    Assertions.assertThat(simpleTriggerDTO.getRepeatCount()).isEqualTo(simpleTrigger.getRepeatCount());
    Assertions.assertThat(simpleTriggerDTO.getRepeatInterval()).isEqualTo(simpleTrigger.getRepeatInterval());
    Assertions.assertThat(simpleTriggerDTO.getJobDataMap()).containsEntry("data", "value");
    Assertions.assertThat(simpleTriggerDTO.getStartTime()).isEqualTo(simpleTrigger.getStartTime());
    Assertions.assertThat(simpleTriggerDTO.getEndTime()).isEqualTo(simpleTrigger.getEndTime());
  }
}

