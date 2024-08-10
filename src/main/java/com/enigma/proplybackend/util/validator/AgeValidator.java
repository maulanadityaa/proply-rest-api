package com.enigma.proplybackend.util.validator;

import com.enigma.proplybackend.util.annotation.ValidAge;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

public class AgeValidator implements ConstraintValidator<ValidAge, Long> {
    private static final int MIN_AGE = 20;
    private static final int MAX_AGE = 50;

    @Override
    public void initialize(ValidAge constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Long birthdateUnix, ConstraintValidatorContext constraintValidatorContext) {
        if (birthdateUnix == null) {
            return false;
        }

        LocalDate birthdate = Instant.ofEpochSecond(birthdateUnix)
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        LocalDate now = LocalDate.now();
        long age = ChronoUnit.YEARS.between(birthdate, now);

        return age >= MIN_AGE && age <= MAX_AGE;
    }
}
