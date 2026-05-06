package it.fabioformosa.quartzmanager.api.converters;

import it.fabioformosa.quartzmanager.api.controllers.utils.TriggerUtils;
import it.fabioformosa.quartzmanager.api.dto.SimpleTriggerCommandDTO;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.quartz.SimpleTrigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.convert.ConversionService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
class SimpleTriggerCommandDTOToSimpleTriggerTest {

  @Autowired
  private ConversionService conversionService;

  @Order(1)
  @Test
  void givenSimpleTriggerCommandDTO_whenItIsConverted_thenASimpleTriggerIsReturned() throws ParseException {
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    Date startDate = dateFormat.parse("2024-02-02");
    Date endDate = dateFormat.parse("2024-03-02");
    SimpleTriggerCommandDTO simpleTriggerCommandDTO = TriggerUtils.buildSimpleTriggerCommandDTO("mytrigger");
    SimpleTrigger simpleTrigger = conversionService.convert(simpleTriggerCommandDTO, SimpleTrigger.class);
    Assertions.assertThat(simpleTrigger).isNotNull();
    Assertions.assertThat(simpleTrigger.getRepeatCount()).isEqualTo(simpleTriggerCommandDTO.getSimpleTriggerInputDTO().getRepeatCount());
    Assertions.assertThat(simpleTrigger.getRepeatInterval()).isEqualTo(simpleTriggerCommandDTO.getSimpleTriggerInputDTO().getRepeatInterval());
    Assertions.assertThat(simpleTrigger.getJobDataMap()).containsEntry("data", "value");
    Assertions.assertThat(simpleTrigger.getStartTime()).isEqualTo(startDate);
    Assertions.assertThat(simpleTrigger.getEndTime()).isEqualTo(endDate);
  }
}
