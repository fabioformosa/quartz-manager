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
public @interface ValidRepetition {

  String message() default "Invalid repetition values";
  Class<?>[] groups() default {};
  Class<? extends Payload>[] payload() default {};

}
