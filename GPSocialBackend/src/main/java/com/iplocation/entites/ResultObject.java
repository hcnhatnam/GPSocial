/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iplocation.entites;

import com.google.gson.Gson;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author namhcn
 */
public class ResultObject {
    public static ResultObject getUnknownExResultObject() {
        return new ResultObject(ResultObject.ERROR, "Unknown Exception");
    }
    private static final Gson gson = new Gson();
    public static final int SUCCESS = 0;
    public static final int ERROR = -1;

    private int error = SUCCESS;
    private String message = "";
    private Map<String, Object> data;

    public ResultObject() {
        this.data = new HashMap<>();
    }

    public ResultObject(int err, String mesg) {
        this.data = new HashMap<>();
        this.error = err;
        this.message = mesg;
    }

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void putData(String key, Object obj) {
        data.put(key, obj);
    }

    public Map<String, Object> getData() {
        return data;
    }

    @Override
    public String toString() {
        return  gson.toJson(this); //To change body of generated methods, choose Tools | Templates.
    }

}
