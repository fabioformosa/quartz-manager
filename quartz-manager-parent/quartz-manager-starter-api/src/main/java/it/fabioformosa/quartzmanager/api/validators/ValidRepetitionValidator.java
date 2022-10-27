package it.fabioformosa.quartzmanager.api.validators;

import it.fabioformosa.quartzmanager.api.dto.RepetitionDTO;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ValidRepetitionValidator implements ConstraintValidator<ValidRepetition, RepetitionDTO> {

  @Override
  public boolean isValid(RepetitionDTO repetitionDTO, ConstraintValidatorContext constraintValidatorContext) {
    return (repetitionDTO.getRepeatCount() == null && repetitionDTO.getRepeatInterval() == null) ||
      (repetitionDTO.getRepeatCount() != null && repetitionDTO.getRepeatInterval() != null);
  }
}
