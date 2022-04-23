package com.qamet.book_store.validation.validator;

import com.qamet.book_store.validation.UniqueField;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UniqueFieldValidator implements ConstraintValidator<UniqueField, Object> {

    private String entityName;
    private String fieldName;

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public void initialize(UniqueField constraintAnnotation) {
        this.entityName = constraintAnnotation.entityClass().getSimpleName();
        this.fieldName = constraintAnnotation.fieldName();
    }

    @Override
    public boolean isValid(Object fieldValue, ConstraintValidatorContext context) {

        try {
            Query query = entityManager.createQuery("select t.id from " + entityName + " t where t." + fieldName + " = :fieldValue ");
            Integer id = (Integer) query.setParameter("fieldValue", fieldValue).getSingleResult();

            return id == null;
        } catch (NoResultException ignored) {
            return true;
        }
    }
}
