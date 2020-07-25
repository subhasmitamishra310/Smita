package com.zomato.ZomatoAutomation.setUp;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import org.testng.annotations.BeforeClass;

public class Base {

    @BeforeClass
    public void initiateRestAssured() {
        RestAssured.baseURI = "https://developers.zomato.com";
    }

}
