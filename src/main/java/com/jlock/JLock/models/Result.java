package com.jlock.JLock.models;

public class Result<T> {

    private boolean isSuccess = false;
    private String errorMsg = null;
    private T value = null;
    private Object extraParameter = null;

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

    public Object getExtraParameter() {
        return extraParameter;
    }

    public static <T> Result<T> success(T value, Object extraParameter) {
        var result = new Result<T>();
        result.isSuccess = true;
        result.value = value;
        result.extraParameter = extraParameter;

        return result;
    }

    public static <T> Result<T> success(T value) {
        return Result.success(value, null);
    }

    public static <T> Result<T> failure(String errMsg, Object extraParameter) {
        var result = new Result<T>();
        result.isSuccess = false;
        result.errorMsg = errMsg;
        result.extraParameter = extraParameter;

        return result;
    }

    public static <T> Result<T> failure(String errMsg) {
        return failure(errMsg, null);
    }

    public static <T> Result<T> failure() {
        return failure(null, null);
    }
}
