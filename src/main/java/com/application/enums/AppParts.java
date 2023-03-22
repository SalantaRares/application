package com.application.enums;

public enum AppParts {
    F_REPORTING("F_REPORTING");

    AppParts(String value) {
        this.value = value;
    }

    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
