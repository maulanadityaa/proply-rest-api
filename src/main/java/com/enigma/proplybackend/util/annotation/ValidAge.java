package com.enigma.proplybackend.util.annotation;

import com.enigma.proplybackend.util.validator.AgeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = AgeValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidAge {
    String message() default "Invalid age. Age must be between 20 and 50 years.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
