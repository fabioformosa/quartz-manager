package it.fabioformosa.quartzmanager.api.validators;

import it.fabioformosa.quartzmanager.api.dto.TriggerPeriodDTO;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidTriggerPeriodValidator implements ConstraintValidator<ValidTriggerPeriod, TriggerPeriodDTO> {
  @Override
  public boolean isValid(TriggerPeriodDTO triggerPeriodDTO, ConstraintValidatorContext constraintValidatorContext) {
    if(triggerPeriodDTO.getStartDate() != null && triggerPeriodDTO.getEndDate() != null)
      return !triggerPeriodDTO.getEndDate().before(triggerPeriodDTO.getStartDate());
    return true;
  }
}
