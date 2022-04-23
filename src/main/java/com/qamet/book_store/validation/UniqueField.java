package com.qamet.book_store.validation;

import com.qamet.book_store.entity.AbstractEntity;
import com.qamet.book_store.validation.validator.UniqueFieldValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = UniqueFieldValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueField {
    String message() default "This value is already used";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    Class<? extends AbstractEntity> entityClass();

    String fieldName();
}
