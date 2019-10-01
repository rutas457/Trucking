package com.training.Trucking.validation;

import lombok.NoArgsConstructor;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@NoArgsConstructor
public class NameValidator implements ConstraintValidator<ValidName, String> {

    public void initialize(ValidName constraint) {
    }

    public boolean isValid(String name, ConstraintValidatorContext context) {
        return name.matches("([A-Z])([a-z]+)|([А-ЩЬ-ЯІЇ])([а-щь-яії]+)");
    }

}
