package com.velti.template.core.http_api;

public class BaseHttpResponse {
    int statusCode;
    String responseBody;

    public BaseHttpResponse(int statusCode, String responseBody) {
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    public void setStatusCode(int statusCode) {

        this.statusCode = statusCode;
    }
}
