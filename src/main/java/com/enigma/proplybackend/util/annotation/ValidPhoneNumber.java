package com.enigma.proplybackend.util.annotation;

import com.enigma.proplybackend.util.validator.PhoneNumberValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = PhoneNumberValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPhoneNumber {
    String message() default "Invalid phone number. Phone number must be a valid Indonesian phone number.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
