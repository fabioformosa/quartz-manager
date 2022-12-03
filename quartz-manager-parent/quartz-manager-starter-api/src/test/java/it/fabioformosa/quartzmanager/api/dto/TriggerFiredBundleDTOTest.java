package it.fabioformosa.quartzmanager.api.dto;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class TriggerFiredBundleDTOTest {

  @CsvSource({
    "10, 100, 10",
    "23, 1000, 2",
    "26, 1000, 3"
  })
  @ParameterizedTest
  void givenARepeatCount_whenTheTriggerHasFiredXTimes_thenThePercentageIsCalculatedAccordingly(int timesTriggered, int repeatCount, int expectedPercentage){
    TriggerFiredBundleDTO triggerFiredBundleDTO = TriggerFiredBundleDTO.builder().build();
    triggerFiredBundleDTO.setTimesTriggered(timesTriggered);
    triggerFiredBundleDTO.setRepeatCount(repeatCount);
    Assertions.assertThat(triggerFiredBundleDTO.getPercentage()).isEqualTo(expectedPercentage);
  }

  @Test
  void givenAnInfiniteRecursion_whenTheTriggerHasFired10_thenThePercentageIsMinus1(){
    TriggerFiredBundleDTO triggerFiredBundleDTO = TriggerFiredBundleDTO.builder().build();
    triggerFiredBundleDTO.setTimesTriggered(10);
    triggerFiredBundleDTO.setRepeatCount(-1);
    Assertions.assertThat(triggerFiredBundleDTO.getPercentage()).isEqualTo(-1);
  }

}
