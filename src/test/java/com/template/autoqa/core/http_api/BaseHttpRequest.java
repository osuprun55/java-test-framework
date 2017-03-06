package com.template.autoqa.core.http_api;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.eclipse.jetty.http.HttpStatus;


public class BaseHttpRequest {
     String baseUrl;
     HashMap<String, String> requestHeaders;
     Object requestBody;

    private BaseHttpRequest() {
        //do nothing
    }

    public BaseHttpRequest(String baseUrl, Map<String, String> requestHeaders) {
        this.baseUrl=baseUrl;
        if (requestHeaders!=null){
            this.requestHeaders=new HashMap<String, String>(requestHeaders);
        }
        else{
            this.requestHeaders=new HashMap<String, String>();
        }

    }


    public BaseHttpResponse doGet(String resourceUrl, List<String> urlParameters){
        String fullUrl=stitchUrlAndParameters(resourceUrl, urlParameters);
        System.out.println(fullUrl);
        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(fullUrl);
        for (Map.Entry<String, String> header : requestHeaders.entrySet())
            request.setHeader(header.getKey(), header.getValue());
        try {
            HttpResponse response = client.execute(request);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            String responseBody = responseHandler.handleResponse(response);
            int response_code=response.getStatusLine().getStatusCode();
            return new BaseHttpResponse(response_code, responseBody);
            }
        catch (IOException e) {
            e.printStackTrace();
            return new BaseHttpResponse(-1, "Error executing request");
        }
        finally {
              request.releaseConnection();
        }
    }

    private String stitchUrlAndParameters(String resourceUrl, List<String> urlParameters){
        StringBuilder paramsString = new StringBuilder();
        for (int i=0; i<urlParameters.size();i++){
            if (i<urlParameters.size()-1)   {
                paramsString.append(urlParameters.get(i)).append("&");
            }
            else {
                paramsString.append(urlParameters.get(i));
            }

        }
        String fullUrl="";
        if (urlParameters.size()>0){
            fullUrl = baseUrl+resourceUrl+"?"+paramsString.toString();
        }
        else{
            fullUrl = baseUrl+resourceUrl;
        }

        return fullUrl;
    }

    public BaseHttpResponse doPost(String resourceUrl, List<String> urlParameters, Map<String, String> bodyParameters){
        String fullUrl=stitchUrlAndParameters(resourceUrl, urlParameters);
        HttpClient client = new DefaultHttpClient();
        HttpPost request = new HttpPost(fullUrl);
        for (Map.Entry<String, String> header : requestHeaders.entrySet())
            request.setHeader(header.getKey(), header.getValue());

        try {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
            for (Map.Entry<String, String> parameter : bodyParameters.entrySet())
                nameValuePairs.add(new BasicNameValuePair(parameter.getKey(), parameter.getValue()));
            request.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response = client.execute(request);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            String responseBody = responseHandler.handleResponse(response);
            int response_code=response.getStatusLine().getStatusCode();
            return new BaseHttpResponse(response_code, responseBody);
        }
        catch (IOException e) {
            e.printStackTrace();
            return new BaseHttpResponse(-1, "Error executing request");
        }
        finally {
            request.releaseConnection();
        }
    }

    public BaseHttpResponse doPostWithArbitraryBody(String resourceUrl, List<String> urlParameters, Object requestBody){
        String fullUrl=stitchUrlAndParameters(resourceUrl, urlParameters);
        HttpClient client = new DefaultHttpClient();
        HttpPost request = new HttpPost(fullUrl);
        for (Map.Entry<String, String> header : requestHeaders.entrySet())
            request.setHeader(header.getKey(), header.getValue());

        try {
            request.setEntity(new StringEntity(requestBody.toString()));
            HttpResponse response = client.execute(request);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            String responseBody = responseHandler.handleResponse(response);
            int response_code=response.getStatusLine().getStatusCode();
            return new BaseHttpResponse(response_code, responseBody);
        }
        catch (IOException e) {
            e.printStackTrace();
            return new BaseHttpResponse(-1, "Error executing request");
        }
        finally {
            request.releaseConnection();
        }
    }

}
