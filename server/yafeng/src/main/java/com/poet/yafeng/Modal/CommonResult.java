package com.poet.yafeng.Modal;


import com.fasterxml.jackson.annotation.JsonView;

import java.io.Serializable;

public class CommonResult implements Serializable {
    public interface ResultView {};
    private static final long serialVersionUID = -7947136520933242349L;
    private int status;
    private String result;
    private String msg;
    private Object data;

    public CommonResult() {
        status=200;
        result="success";
        msg="接口调用成功";
    }

    @JsonView(ResultView.class)
    public int getStatus() {
        return status;
    }
    public void setStatus(int status) {
        this.status = status;
    }

    @JsonView(ResultView.class)
    public String getResult() {
        return result;
    }
    public void setResult(String result) {
        this.result = result;
    }

    @JsonView(ResultView.class)
    public String getMsg() {
        return msg;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }

    @JsonView(ResultView.class)
    public Object getData() {
        return data;
    }
    public void setData(Object data) {
        this.data = data;
    }
}
