package com.example.relay.catalog.internal.controller;

import com.example.relay.catalog.internal.exceptions.DuplicateSubcategoryException;
import com.example.relay.catalog.internal.exceptions.CategoryCycleException;
import com.example.relay.catalog.internal.exceptions.EntityNotFoundException;
import org.hibernate.exception.ConstraintViolationException;
import org.jspecify.annotations.NonNull;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackageClasses = BrandController.class)
public class CatalogExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ProblemDetail handleEntityNotFound(@NonNull EntityNotFoundException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(CategoryCycleException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ProblemDetail handleCategoryCycle(@NonNull CategoryCycleException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, e.getMessage());
    }

    @ExceptionHandler(DuplicateSubcategoryException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ProblemDetail handleDuplicateSubcategory(@NonNull DuplicateSubcategoryException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, e.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ProblemDetail handleDataIntegrityViolation(@NonNull DataIntegrityViolationException e) {
        String message = "A brand with conflicting details already exists";

        if (e.getCause() instanceof ConstraintViolationException cve) {
            assert cve.getConstraintName() != null;
            message = switch (cve.getConstraintName()) {
                case "brand_name_key" -> "A brand with this name already exists";
                case "brand_slug_key" -> "A brand with this slug already exists";
                case "brand_logo_url_key" -> "This logo is already in use by another brand";
                case "category_name_key" -> "A category with this name already exists";
                case "product_name_key" -> "A product with this name already exists";
                case "product_slug_key" -> "A product with this slug already exists";
                case "sku_sku_code_key" -> "A SKU with this code already exists";
                default -> message;
            };
        }

        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, message);
    }
}
