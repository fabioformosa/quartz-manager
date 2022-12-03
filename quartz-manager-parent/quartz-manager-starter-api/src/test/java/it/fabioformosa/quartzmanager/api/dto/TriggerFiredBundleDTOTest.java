package it.fabioformosa.quartzmanager.api.dto;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TriggerFiredBundleDTOTest {

  @Test
  void givenARecursionOf1000_whenTheTriggerHasFired10_thenThePercentageIs10(){
    TriggerFiredBundleDTO triggerFiredBundleDTO = TriggerFiredBundleDTO.builder().build();
    triggerFiredBundleDTO.setTimesTriggered(10);
    triggerFiredBundleDTO.setRepeatCount(100);
    Assertions.assertThat(triggerFiredBundleDTO.getPercentage()).isEqualTo(10);
  }

  @Test
  void givenARecursionOf1000_whenTheTriggerHasFired23_thenThePercentageIsRoundedDown(){
    TriggerFiredBundleDTO triggerFiredBundleDTO = TriggerFiredBundleDTO.builder().build();
    triggerFiredBundleDTO.setTimesTriggered(23);
    triggerFiredBundleDTO.setRepeatCount(1000);
    Assertions.assertThat(triggerFiredBundleDTO.getPercentage()).isEqualTo(2);
  }

  @Test
  void givenARecursionOf1000_whenTheTriggerHasFired26_thenThePercentageIsRoundedUp(){
    TriggerFiredBundleDTO triggerFiredBundleDTO = TriggerFiredBundleDTO.builder().build();
    triggerFiredBundleDTO.setTimesTriggered(26);
    triggerFiredBundleDTO.setRepeatCount(1000);
    Assertions.assertThat(triggerFiredBundleDTO.getPercentage()).isEqualTo(3);
  }

  @Test
  void givenAnInfiniteRecursion_whenTheTriggerHasFired10_thenThePercentageIsMinus1(){
    TriggerFiredBundleDTO triggerFiredBundleDTO = TriggerFiredBundleDTO.builder().build();
    triggerFiredBundleDTO.setTimesTriggered(10);
    triggerFiredBundleDTO.setRepeatCount(-1);
    Assertions.assertThat(triggerFiredBundleDTO.getPercentage()).isEqualTo(-1);
  }

}
