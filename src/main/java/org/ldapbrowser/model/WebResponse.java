package org.ldapbrowser.model;

import java.io.Serializable;

/**
 * Created by zaporozhec on 10/8/16.
 */
public class WebResponse<T> implements Serializable {
    private String message;
    private T returnObject;
    private ResponseCode responseCode;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getReturnObject() {
        return returnObject;
    }

    public void setReturnObject(T returnObject) {
        this.returnObject = returnObject;
    }

    public ResponseCode getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(ResponseCode responseCode) {
        this.responseCode = responseCode;
    }
}
