package org.talon.exception;

import org.talon.rest.ResponseStatusEnum;

/**
 * Created by Zelong
 * On 2022/10/30
 **/

public class RedbookException extends RuntimeException {
    private ResponseStatusEnum responseStatusEnum;

    public RedbookException(ResponseStatusEnum responseStatusEnum) {
        super("StatusCode：" + responseStatusEnum.status()
                + "；Msg：" + responseStatusEnum.msg());
        this.responseStatusEnum = responseStatusEnum;
    }

    public ResponseStatusEnum getResponseStatusEnum() {
        return responseStatusEnum;
    }

    public void setResponseStatusEnum(ResponseStatusEnum responseStatusEnum) {
        this.responseStatusEnum = responseStatusEnum;
    }
}
