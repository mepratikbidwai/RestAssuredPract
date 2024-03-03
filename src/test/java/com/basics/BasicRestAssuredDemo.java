package com.basics;

import com.utils.Payloads;
import com.utils.TestUtil;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class BasicRestAssuredDemo {

    @BeforeMethod
    public void beforeMethod(){
        RestAssured.baseURI="https://rahulshettyacademy.com";
    }

    @Test
    public void test01AddNewPlace(){

        //add new place API

        System.out.println ("Payload is : " +Payloads.testJson1());
        given().log().all().queryParam("key", "qaclick123").header("Content-Type", "application/json")
        .body(Payloads.testJson1()).when().post("maps/api/place/add/json")
        .then().log().all().assertThat().statusCode(200).body("scope", equalTo("APP"))
        .header("server", "Apache/2.4.52 (Ubuntu)");

    }

    @Test
    public void test02AddNewPlacUsingJSONFile() throws IOException {
        String resp = given().log().all().queryParam("key", "qaclick123").header("Content-Type", "application/json")
                .body(new String(Files.readAllBytes(Paths.get("C:\\Users\\www.abcom.in\\Documents\\payload.json"))))
                .when().post("maps/api/place/add/json")
                .then().log().all().assertThat().statusCode(200).body("scope", equalTo("APP"))
                .header("server", "Apache/2.4.52 (Ubuntu)").extract().response().asString();
    }
    @Test
    public void test02SampleTest2(){

        Response resp = RestAssured.get("https://reqres.in/api/users?page=2");
        System.out.println("Status code is : " +resp.getStatusCode());
        System.out.println("Status time is : " +resp.getTime());
        System.out.println("Body is : " +resp.getBody().toString());
        System.out.println("Status line is : " +resp.getStatusLine());
        System.out.println("Header is : " +resp.getHeader("content-type"));

    }

    @Test
    public void test03SampleTest3(){
        String resp1 = given().log().all().queryParam("key", "qaclick123").header("Content-Type","application/json")
                .body(Payloads.testJson1()).when().post("maps/api/place/add/json").then().assertThat().statusCode(200)
                .body("scope",equalTo("APP")).extract().response().asString();

        System.out.println("Response1 is ---> " +resp1);

        JsonPath jsonPath = TestUtil.rawToJSON(resp1);
        String placeID = jsonPath.getString("place_id");

        System.out.println("PlaceID is --->" +placeID);
        String address = "72 Summer walk, USA";
        String resp2 = given().log().all().queryParam("key", "qaclick123").header("Content-Type","application/json")
                .body("{\n" +
                        "\"place_id\":\""+placeID+"\",\n" +
                        "\"address\":\""+address+"\",\n" +
                        "\"key\":\"qaclick123\"\n" +
                        "}")
                .when().put("maps/api/place/update/json").then().assertThat().statusCode(200)
                .body("msg", equalTo("Address successfully updated")).extract().response().asString();

        System.out.println("Response2 is ---> " +resp2);

        String resp3 = given().log().all().queryParam("key","qaclick123")
                .queryParam("place_id",placeID)
                .when().get("maps/api/place/get/json")
                .then().log().all().assertThat().statusCode(200)
                .body("address", equalTo(address)).extract().asString();

        JsonPath getAddr = TestUtil.rawToJSON(resp3);
        String extrAddress = getAddr.getString("address");
        System.out.println("Extracted address ---> " +extrAddress);
        Assert.assertEquals(extrAddress, "72 Summer walk, USA");
    }

    @Test
    public void testJSONDummy1(){
        JsonPath js = new JsonPath(Payloads.dummyResponse1());

        //Print number of courses
        int count = js.getInt("courses.size()");
        System.out.println("Number of courses : " +count);

        //print purchase amount
        int totalAmt = js.getInt("dashboard.purchaseAmount");
        System.out.println("Total amt : " +totalAmt);

        //Print second course
        String course2 = js.get("courses[1].title");
        System.out.println("Second course is : " +course2);

        //Iteration through JSON array
        for (int i=0; i<count; i++){
            String courseTitle = js.getString("courses["+i+"].title");
            System.out.println("Course " +i+ " : " +courseTitle);

            System.out.println("Price " +i+ " : " +js.get("courses["+i+"].price").toString());
            System.out.println("Copies " +i+ " : " +js.get("courses["+i+"].copies").toString());
        }
    }

    @Test
    public void verifyRPACourse(){
        JsonPath js = new JsonPath(Payloads.dummyResponse1());

        //Get number of courses
        int count = js.getInt("courses.size()");

        for (int i=0; i<count; i++) {
            if (js.get("courses[" + i + "].title").toString().equalsIgnoreCase("RPA")) {
                System.out.println("Details for RPA Course");
                System.out.println("Price " + i + " : " + js.get("courses[" + i + "].price").toString());
                System.out.println("Copies " + i + " : " + js.get("courses[" + i + "].copies").toString());
                break;
            }
        }
    }

    @Test
    public void verifySumOfPrices(){
        JsonPath js = new JsonPath(Payloads.dummyResponse1());

        //Get number of courses
        int count = js.getInt("courses.size()");
        int sum=0;
        for (int i=0; i<count; i++) {
                System.out.println("Price " + i + " : " + js.get("courses[" + i + "].price").toString());
                System.out.println("Copies " + i + " : " + js.get("courses[" + i + "].copies").toString());
                int price = js.getInt("courses[" + i + "].price");
                int copies = js.getInt("courses[" + i + "].copies");
                sum+=price*copies;
        }

        System.out.println("Sum of courses : " +sum);
        Assert.assertEquals(1890,sum);

        int purchaseAmt = js.getInt("dashboard.purchaseAmount");
        Assert.assertEquals(sum, purchaseAmt);
    }

}
