package com.littlejenny.common.constraints;

import com.littlejenny.common.validanno.ListVal;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.Set;

public class ListValIntegerValidator implements ConstraintValidator<ListVal, Integer> {
    private Set intSet = new HashSet<Integer>();
    @Override
    public void initialize(ListVal constraintAnnotation) {
        for (int val : constraintAnnotation.vals()) {
            intSet.add(val);
        }
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        return intSet.contains(value) || value == null;
    }
}
