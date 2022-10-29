package it.fabioformosa.quartzmanager.api.validators;

import it.fabioformosa.quartzmanager.api.dto.TriggerRepetitionDTO;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ValidRepetitionValidator implements ConstraintValidator<ValidTriggerRepetition, TriggerRepetitionDTO> {

  @Override
  public boolean isValid(TriggerRepetitionDTO repetitionDTO, ConstraintValidatorContext constraintValidatorContext) {
    return (repetitionDTO.getRepeatCount() == null && repetitionDTO.getRepeatInterval() == null) ||
      (repetitionDTO.getRepeatCount() != null && repetitionDTO.getRepeatInterval() != null);
  }
}
