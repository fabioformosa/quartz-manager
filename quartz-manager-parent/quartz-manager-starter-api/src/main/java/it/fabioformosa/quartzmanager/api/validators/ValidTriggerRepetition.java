package it.fabioformosa.quartzmanager.api.validators;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = ValidRepetitionValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidTriggerRepetition {
  String message() default "Invalid repetition values. Repeat Count and Repeat interval must be both set or unset.";
  Class<?>[] groups() default {};
  Class<? extends Payload>[] payload() default {};
}
