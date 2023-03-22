package com.application.utils.hibernate;

public class CustomParameter {
    private String name;
    private Object value;
    private Class objectClass = null;

    public Class getObjectClass() {
        return objectClass;
    }

    public void setObjectClass(Class objectClass) {
        this.objectClass = objectClass;
    }

    public CustomParameter() {
    }

    public CustomParameter(String name, Object value) {
        this.name = name;
        this.value = value;
    }
    public CustomParameter(String name, Object value, Class objectClass) {
        this.name = name;
        this.value = value;
        this.objectClass = objectClass;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
