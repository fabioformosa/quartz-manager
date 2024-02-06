package it.fabioformosa.quartzmanager.api.converters;


import it.fabioformosa.quartzmanager.api.dto.SimpleTriggerCommandDTO;
import org.quartz.JobDataMap;
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
    if (triggerCommandDTO.getSimpleTriggerInputDTO().getStartDate() != null)
      triggerTriggerBuilder.startAt(triggerCommandDTO.getSimpleTriggerInputDTO().getStartDate());
    if (triggerCommandDTO.getSimpleTriggerInputDTO().getEndDate() != null)
      triggerTriggerBuilder.endAt(triggerCommandDTO.getSimpleTriggerInputDTO().getEndDate());

    if (triggerCommandDTO.getSimpleTriggerInputDTO().getJobDataMap() != null)
      triggerTriggerBuilder.usingJobData(new JobDataMap(triggerCommandDTO.getSimpleTriggerInputDTO().getJobDataMap()));

    SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder.simpleSchedule();
    if (triggerCommandDTO.getSimpleTriggerInputDTO().getRepeatInterval() != null)
      scheduleBuilder.withIntervalInMilliseconds(triggerCommandDTO.getSimpleTriggerInputDTO().getRepeatInterval());

    if (triggerCommandDTO.getSimpleTriggerInputDTO().getRepeatCount() != null)
      scheduleBuilder.withRepeatCount(triggerCommandDTO.getSimpleTriggerInputDTO().getRepeatCount());

    setTheMisfireInstruction(triggerCommandDTO, scheduleBuilder);

    return triggerTriggerBuilder.withSchedule(
        scheduleBuilder
      )
      .withIdentity(triggerCommandDTO.getTriggerName()).build();
  }

  private static void setTheMisfireInstruction(SimpleTriggerCommandDTO triggerCommandDTO, SimpleScheduleBuilder scheduleBuilder) {
    switch (triggerCommandDTO.getSimpleTriggerInputDTO().getMisfireInstruction()) {
      case MISFIRE_INSTRUCTION_FIRE_NOW:
        scheduleBuilder.withMisfireHandlingInstructionFireNow();
        break;
      case MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_EXISTING_REPEAT_COUNT:
        scheduleBuilder.withMisfireHandlingInstructionNowWithExistingCount();
        break;
      case MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_REMAINING_REPEAT_COUNT:
        scheduleBuilder.withMisfireHandlingInstructionNowWithRemainingCount();
        break;
      case MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_REMAINING_COUNT:
        scheduleBuilder.withMisfireHandlingInstructionNextWithRemainingCount();
        break;
      case MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_EXISTING_COUNT:
        scheduleBuilder.withMisfireHandlingInstructionNextWithExistingCount();
        break;
    }
  }
}
