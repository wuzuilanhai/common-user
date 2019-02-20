package com.biubiu.advice;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 张海彪
 * @create 2019-02-20 11:30
 */
@ControllerAdvice
public class CustomControllerAdvice {

    /**
     * 全局异常捕捉处理
     */
    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public Map errorHandler(Exception ex) {
        Map<String, Object> map = new HashMap<>();
        map.put("code", 100);
        map.put("msg", ex.getMessage());
        return map;
    }

}
