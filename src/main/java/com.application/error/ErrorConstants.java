package com.application.error;

import java.net.URI;

public final class ErrorConstants {

    public static final String PROBLEM_BASE_URL = "http://www.jhipster.tech/problem";
    public static final URI DEFAULT_TYPE = URI.create(PROBLEM_BASE_URL + "/problem-with-message");

    private ErrorConstants() {
    }
}