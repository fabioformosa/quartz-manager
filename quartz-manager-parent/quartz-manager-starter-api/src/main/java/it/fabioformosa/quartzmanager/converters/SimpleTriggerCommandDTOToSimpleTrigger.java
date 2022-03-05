package it.fabioformosa.quartzmanager.converters;


import it.fabioformosa.quartzmanager.dto.SimpleTriggerCommandDTO;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class SimpleTriggerCommandDTOToSimpleTrigger implements Converter<SimpleTriggerCommandDTO, SimpleTrigger> {
  @Override
  public SimpleTrigger convert(SimpleTriggerCommandDTO triggerCommandDTO) {
    TriggerBuilder<Trigger> triggerTriggerBuilder = TriggerBuilder.newTrigger();
    if(triggerCommandDTO.getSimpleTriggerInputDTO().getStartDate() != null)
      triggerTriggerBuilder.startAt(triggerCommandDTO.getSimpleTriggerInputDTO().getStartDate());
    if(triggerCommandDTO.getSimpleTriggerInputDTO().getEndDate() != null)
      triggerTriggerBuilder.endAt(triggerCommandDTO.getSimpleTriggerInputDTO().getEndDate());

    SimpleTrigger newSimpleTrigger = triggerTriggerBuilder.withSchedule(
        SimpleScheduleBuilder.simpleSchedule()
          .withIntervalInMilliseconds(triggerCommandDTO.getSimpleTriggerInputDTO().getRepeatInterval())
          .withRepeatCount(triggerCommandDTO.getSimpleTriggerInputDTO().getRepeatCount())
          .withMisfireHandlingInstructionNextWithRemainingCount()
      )
      .withIdentity(triggerCommandDTO.getTriggerName())
      .build();
    return newSimpleTrigger;
  }
}
