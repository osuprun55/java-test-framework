package com.template.autoqa.tests.http_api;

import com.template.autoqa.core.http_api.BaseHttpRequest;
import com.template.autoqa.core.http_api.BaseHttpResponse;
import org.testng.annotations.*;

import java.util.*;


public class BaseHttpApiTest {
    BaseHttpRequest request;

    @BeforeClass
    public void beforeClass() {
        Map headers=new HashMap<String,String>();
        headers.put("accept", "text/json");
        request = new BaseHttpRequest("http://testme.com", headers);

    }

    @BeforeMethod
    public void beforeMethod() {
    }

    @Test
    public void testme() {
        String[] a = {"module=API", "method=testmeGetData","format=JSON", "id=19", "period=range", "token_auth=b8087ca6d6b9694b732df88f288e591d", "filter_limit=50000", "flat=1", "date=2012-09-19,2012-09-21"};
        List<String> parameters = new ArrayList<String>(Arrays.asList(a));
        BaseHttpResponse resp = request.doGet("/index.php", parameters);
        System.out.println(resp.getResponseBody());
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethod() {
    }

    @AfterClass
    public void tearDown() {

    }
}
