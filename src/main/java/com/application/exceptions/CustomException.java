package com.application.exceptions;


import org.springframework.http.HttpStatus;

public class CustomException extends RuntimeException {
    private String exceptionMsg;
    private HttpStatus statusCode;

    public HttpStatus getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(HttpStatus statusCode) {
        this.statusCode = statusCode;
    }

    public CustomException(String exceptionMsg, HttpStatus statusCode) {
        this.exceptionMsg=exceptionMsg;
        this.statusCode= statusCode;
    }
    public String getExceptionMsg(){
        return this.exceptionMsg;
    }
    public void setExceptionMsg(String exceptionMsg) {
        this.exceptionMsg = exceptionMsg;
    }

}
