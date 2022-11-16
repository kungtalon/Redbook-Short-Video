package org.talon.exception;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.talon.rest.JsonResponse;
import org.talon.rest.ResponseStatusEnum;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Zelong
 * On 2022/10/30
 **/

@ControllerAdvice
public class RedbookExceptionHandler {
    @ExceptionHandler(RedbookException.class)
    @ResponseBody
    public JsonResponse returnMyException(RedbookException e) {
        e.printStackTrace();
        return JsonResponse.exception(e.getResponseStatusEnum());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public JsonResponse returnMethodArgumentNotValid(MethodArgumentNotValidException e) {
        BindingResult result = e.getBindingResult();
        Map<String, String> map = getErrors(result);
        return JsonResponse.errorMap(map);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseBody
    public JsonResponse returnMaxUploadSize(MaxUploadSizeExceededException e) {
//        e.printStackTrace();
        return JsonResponse.errorCustom(ResponseStatusEnum.FILE_MAX_SIZE_2MB_ERROR);
    }

    private Map<String, String> getErrors(BindingResult result) {
        Map<String, String> map = new HashMap<>();
        List<FieldError> errorList = result.getFieldErrors();
        for (FieldError ff : errorList) {
            String field = ff.getField();
            String msg = ff.getDefaultMessage();
            map.put(field, msg);
        }
        return map;
    }
}
