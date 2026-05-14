package it.fabioformosa.quartzmanager.api.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidJobTargetValidator implements ConstraintValidator<ValidJobTarget, JobTargetDTO> {
  @Override
  public boolean isValid(JobTargetDTO jobTargetDTO, ConstraintValidatorContext constraintValidatorContext) {
    if (jobTargetDTO == null)
      return true;
    boolean hasJobClass = jobTargetDTO.getJobClass() != null && !jobTargetDTO.getJobClass().isBlank();
    boolean hasJobKey = jobTargetDTO.getJobKey() != null && jobTargetDTO.getJobKey().getName() != null && !jobTargetDTO.getJobKey().getName().isBlank();
    return hasJobClass || hasJobKey;
  }
}
