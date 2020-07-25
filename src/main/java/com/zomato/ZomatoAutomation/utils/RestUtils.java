package com.zomato.ZomatoAutomation.utils;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.util.Map;

public class RestUtils {
    public Response get(String endPoint, Map<String, String> queryParam) throws Exception {
        Response response = RestAssured.given().queryParams(queryParam)
                .header("user-key", "95f9cf3cb0bc523129a5cc771a3cd36d")
                .accept(ContentType.JSON)
                .log().all()
                .get(endPoint)
                .then().extract().response();
        if (response.getStatusCode() != 200)
            throw new Exception();

        return response;
    }

    public Response get(String endPoint) throws Exception {
        Response response = RestAssured.given()
                .header("user-key", "95f9cf3cb0bc523129a5cc771a3cd36d")
                .accept(ContentType.JSON)
                .log().all()
                .get(endPoint)
                .then().extract().response();
        if (response.getStatusCode() != 200)
            throw new Exception();

        return response;
    }
}


