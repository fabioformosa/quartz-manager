package it.fabioformosa.quartzmanager.api.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = ValidJobTargetValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidJobTarget {
  String message() default "Either jobClass or jobKey must be set";
  Class<?>[] groups() default {};
  Class<? extends Payload>[] payload() default {};
}
