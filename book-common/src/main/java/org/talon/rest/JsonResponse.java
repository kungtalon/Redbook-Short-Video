package org.talon.rest;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

/**
 * Created by Zelong
 * On 2022/10/29
 **/

@Data
public class JsonResponse {
    private Integer status;

    private String msg;

    private Boolean success;

    private Object data;

    public JsonResponse() {
    }

    public JsonResponse(ResponseStatusEnum responseStatus) {
        this.status = responseStatus.status();
        this.msg = responseStatus.msg();
        this.success = responseStatus.success();
    }

    public JsonResponse(ResponseStatusEnum responseStatus, Object data) {
        this.status = responseStatus.status();
        this.msg = responseStatus.msg();
        this.success = responseStatus.success();
        this.data = data;
    }

    public JsonResponse(ResponseStatusEnum responseStatus, String msg) {
        this.status = responseStatus.status();
        this.msg = msg;
        this.success = responseStatus.success();
    }

    public JsonResponse(Object data) {
        this.status = ResponseStatusEnum.SUCCESS.status();
        this.msg = ResponseStatusEnum.SUCCESS.msg();
        this.success = ResponseStatusEnum.SUCCESS.success();
        this.data = data;
    }

    public static JsonResponse ok(Object data) {
        return new JsonResponse(data);
    }

    public static JsonResponse ok() {
        return new JsonResponse(ResponseStatusEnum.SUCCESS);
    }

    public static JsonResponse error() {
        return new JsonResponse(ResponseStatusEnum.FAILED);
    }

    public static JsonResponse errorMap(Map map) {
        return new JsonResponse(ResponseStatusEnum.FAILED, map);
    }

    public static JsonResponse errorMsg(String msg) {
        return new JsonResponse(ResponseStatusEnum.FAILED, msg);
    }

    public static JsonResponse errorTicket() {
        return new JsonResponse(ResponseStatusEnum.TICKET_INVALID);
    }

    public static JsonResponse errorCustom(ResponseStatusEnum responseStatus) {
        return new JsonResponse(responseStatus);
    }

    public static JsonResponse exception(ResponseStatusEnum responseStatus) {
        return new JsonResponse(responseStatus);
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }
}
