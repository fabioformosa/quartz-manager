package it.fabioformosa.quartzmanager.api.validators;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = ValidTriggerPeriodValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidTriggerPeriod {
  String message() default "Invalid period values. The end date cannot be before the start date";
  Class<?>[] groups() default {};
  Class<? extends Payload>[] payload() default {};
}
