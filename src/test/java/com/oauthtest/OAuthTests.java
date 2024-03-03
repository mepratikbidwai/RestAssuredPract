package com.oauthtest;

import com.utils.TestUtil;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import org.testng.annotations.Test;
import static io.restassured.RestAssured.*;

public class OAuthTests {

    /**
     * The test uses OAuth to get extract courses. It helps understand OAuth mechanism
     */
    @Test
    public void testGetCourses(){

        baseURI="https://rahulshettyacademy.com";
        String tokenResp = given().formParam("client_id", "692183103107-p0m7ent2hk7suguv4vq22hjcfhcr43pj.apps.googleusercontent.com")
                .formParam("client_secret", "erZOWM9g3UtwNRj340YYaK_W")
                .formParam("grant_type", "client_credentials")
                .formParam("scope","trust").when().log().all()
                .post("/oauthapi/oauth2/resourceOwner/token").asString();

        JsonPath jResp1 = TestUtil.rawToJSON(tokenResp);
        String accessToken = jResp1.getString("access_token");

        String resp2 = given().queryParam("access_token", accessToken).when().log().all().get("/oauthapi/getCourseDetails").asString();

        System.out.println("Response ---> " +resp2);
    }
}
