package com.basics;

import com.Payloads;
import io.restassured.RestAssured;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class BasicRestAssuredDemo {

    @BeforeMethod
    public void beforeMethod(){
        RestAssured.baseURI="https://rahulshettyacademy.com";
    }

    @Test
    public void test01AddNewPlace(){
        //add new place api
        given().log().all().queryParam("key", "qaclicks123").header("Content-Type", "application/json")
        .body(Payloads.testJson1()).when().post("maps/api/place/add/json")
        .then().log().all().assertThat().statusCode(200).body("scope", equalTo("APP"))
        .header("server", "Apache/2.4.18 (Ubuntu)");

    }
}
