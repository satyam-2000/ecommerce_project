package com.example.ecommerce.exception;

public class ResourceNotFoundException extends RuntimeException {
    String resource;
    String field;
    String fieldName;
    Long id;

    public ResourceNotFoundException(String resource, String field, Long id) {
        super(String.format("%s not found with %s:%d", resource, field, id));
        this.resource = resource;
        this.field = field;
        this.id = id;
    }

    public ResourceNotFoundException(String resource, String field, String fieldName) {
        super(String.format("%s not found with %s:%s", resource, field, fieldName));
        this.resource = resource;
        this.field = field;
        this.fieldName = fieldName;
    }

}
