package com.phantoms.phantomsbackend.common.bean;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class ResultInfo<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private int code;
    private boolean success;
    private String message;
    private T data;
    private LocalDateTime timestamp;

    public static <T> ResultInfo<T> ok() {
        return new ResultInfo<>(Status.SUCCESS);
    }

    public static <T> ResultInfo<T> ok(T data) {
        ResultInfo<T> resultInfo = new ResultInfo<>(Status.SUCCESS);
        resultInfo.setData(data);
        return resultInfo;
    }

    public static <T> ResultInfo<T> error(Exception e) {
        return new ResultInfo<>(Status.ERROR, e.getMessage());
    }

    public static <T> ResultInfo<T> error(Exception e, T data) {
        ResultInfo<T> resultInfo = new ResultInfo<>(Status.ERROR, e.getMessage());
        resultInfo.setData(data);
        return resultInfo;
    }

    public static <T> ResultInfo<T> error(String message) {
        return new ResultInfo<>(Status.ERROR, message);
    }

    public static <T> ResultInfo<T> userInvalid(Exception e) {
        return new ResultInfo<>(Status.USER_INVALID, e.getMessage());
    }

    public static <T> ResultInfo<T> userInvalid(Exception e, T data) {
        ResultInfo<T> resultInfo = new ResultInfo<>(Status.USER_INVALID, e.getMessage());
        resultInfo.setData(data);
        return resultInfo;
    }

    public static <T> ResultInfo<T> userInvalid(String message) {
        return new ResultInfo<>(Status.USER_INVALID, message);
    }

    public static <T> ResultInfo<T> paramInvalid(Exception e) {
        return new ResultInfo<>(Status.PARAM_INVALID, e.getMessage());
    }

    public static <T> ResultInfo<T> paramInvalid(Exception e, T data) {
        ResultInfo<T> resultInfo = new ResultInfo<>(Status.PARAM_INVALID, e.getMessage());
        resultInfo.setData(data);
        return resultInfo;
    }

    public static <T> ResultInfo<T> paramInvalid(String message) {
        return new ResultInfo<>(Status.PARAM_INVALID, message);
    }

    public static <T> ResultInfo<T> notFound(Exception e) {
        return new ResultInfo<>(Status.NOT_FOUND, e.getMessage());
    }

    public static <T> ResultInfo<T> notFound(Exception e, T data) {
        ResultInfo<T> resultInfo = new ResultInfo<>(Status.NOT_FOUND, e.getMessage());
        resultInfo.setData(data);
        return resultInfo;
    }

    public static <T> ResultInfo<T> notFound(String message) {
        return new ResultInfo<>(Status.NOT_FOUND, message);
    }

    public static <T> ResultInfo<T> forbidden(Exception e) {
        return new ResultInfo<>(Status.FORBIDDEN, e.getMessage());
    }

    public static <T> ResultInfo<T> forbidden(Exception e, T data) {
        ResultInfo<T> resultInfo = new ResultInfo<>(Status.FORBIDDEN, e.getMessage());
        resultInfo.setData(data);
        return resultInfo;
    }

    public static <T> ResultInfo<T> forbidden(String message) {
        return new ResultInfo<>(Status.FORBIDDEN, message);
    }

    public static <T> ResultInfo<T> internalServerError(Exception e) {
        return new ResultInfo<>(Status.INTERNAL_SERVER_ERROR, e.getMessage());
    }

    public static <T> ResultInfo<T> internalServerError(Exception e, T data) {
        ResultInfo<T> resultInfo = new ResultInfo<>(Status.INTERNAL_SERVER_ERROR, e.getMessage());
        resultInfo.setData(data);
        return resultInfo;
    }

    public static <T> ResultInfo<T> internalServerError(String message) {
        return new ResultInfo<>(Status.INTERNAL_SERVER_ERROR, message);
    }

    private ResultInfo() {
        this.timestamp = LocalDateTime.now();
    }

    private ResultInfo(Status status) {
        this.code = status.getCode();
        this.message = status.getMessage();
        this.success = status.isSuccess();
        this.timestamp = LocalDateTime.now();
    }

    private ResultInfo(Status status, String message) {
        this.code = status.getCode();
        this.message = message;
        this.success = status.isSuccess();
        this.timestamp = LocalDateTime.now();
    }

    // 内部枚举类
    public enum Status {
        SUCCESS(200, "Success", true),
        ERROR(500, "Error", false),
        USER_INVALID(401, "User invalid", false),
        PARAM_INVALID(400, "Invalid parameters", false),
        NOT_FOUND(404, "Not found", false),
        FORBIDDEN(403, "Forbidden", false),
        INTERNAL_SERVER_ERROR(500, "Internal server error", false);

        private final int code;
        private final String message;
        private final boolean success;

        Status(int code, String message, boolean success) {
            this.code = code;
            this.message = message;
            this.success = success;
        }

        public int getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }

        public boolean isSuccess() {
            return success;
        }
    }
}