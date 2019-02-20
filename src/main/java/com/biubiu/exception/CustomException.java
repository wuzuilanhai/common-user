package com.biubiu.exception;

/**
 * @author 张海彪
 * @create 2019-02-19 19:58
 */
public class CustomException extends RuntimeException {

    public CustomException() {
        super();
    }

    public CustomException(String message) {
        super(message);
    }

}
