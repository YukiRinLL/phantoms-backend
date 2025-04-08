package com.phantoms.phantomsbackend.common.exception;

public class ServiceException extends RuntimeException{
    public static final String UNKNOWN_ERROR = "000000";
    public static final String OBJECT_EXISTS = "000001";
    public static final String NOT_FOUND = "000002";
    public static final String PARAM_CHECK_FAILED = "000003";
    private String code;
    private String message;

    public ServiceException(String code, String message){
        super(message);
        this.code = code;
        this.message = message;
    }


    public static ServiceException UnknownException(){
        throw new ServiceException(UNKNOWN_ERROR,"未知异常");
    }

    public static ServiceException ObjectExisits(String message){
        String msg = "目标对象已存在";
        if (message!=null){
            msg = message;
        }
        throw  new ServiceException(OBJECT_EXISTS,msg);
    }
    public static ServiceException NotFound(String message){
        String msg = "目标对象未找到";
        if (message!=null){
            msg = message;
        }
        throw  new ServiceException(NOT_FOUND,msg);
    }

    public static ServiceException ParamInvalid(String message) {
        throw  new ServiceException(PARAM_CHECK_FAILED,message);
    }
}
