package com.jlock.JLock.models;

public class Result<T> {

    private boolean isSuccess = false;
    private String errorMsg = null;
    private T value = null;

    private Result() {
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public T getValue() {
        return value;
    }

    public String getErrorMessage() {
        return errorMsg;
    }

    public static <T> Result<T> success(T value) {
        var result = new Result<T>();
        result.isSuccess = true;
        result.value = value;
        return result;
    }

    public static <T> Result<T> failure(String errMsg) {
        var result = new Result<T>();
        result.isSuccess = false;
        result.errorMsg = errMsg;
        return result;
    }

    public static <T> Result<T> failure() {
        return failure(null);
    }
}
