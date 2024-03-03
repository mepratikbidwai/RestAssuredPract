package com.basics;

import com.utils.Payloads;
import com.utils.TestUtil;
import io.restassured.RestAssured;
import io.restassured.filter.session.SessionFilter;
import io.restassured.path.json.JsonPath;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;

import static io.restassured.RestAssured.*;

public class JiraOperations {

    SessionFilter session;
    String responseLogin;

    @BeforeClass
    public void classSetup(){
        RestAssured.baseURI = "http://localhost:8080";
        session = new SessionFilter();
        responseLogin = given().header("Content-Type","application/json").body("{\"username\" : \"mepratikbidwai\", \"password\" : \"Java@cpp007\"}")
                .log().all().filter(session).when().post("/rest/auth/1/session/").then().log().all()
                .extract().response().asString();
    }

    @Test
    public void testAddCommentToJIRA(){

        String responseCreateJira = given().header("Content-Type","application/json")
                        .body(Payloads.addJira()).filter(session).log().all().when().post("/rest/api/2/issue")
                        .then().log().all().assertThat().statusCode(201).extract().response().asString();

        JsonPath jp = TestUtil.rawToJSON(responseCreateJira);
        String responseAddComment = given().header("Content-Type","application/json").pathParams("key", jp.getString("id"))
                .body("{\n" +
                        "    \"body\": \"Comment 1 - This is a comment regarding the quality of the response.\"\n" +
                        "}").filter(session).when().post("/rest/api/2/issue/{key}/comment").then().assertThat().statusCode(201)
                .log().all().extract().response().asString();

        JsonPath jpCommendAdd = TestUtil.rawToJSON(responseAddComment);
        Assert.assertEquals(jpCommendAdd.getString("body"), "Comment 1 - This is a comment regarding the quality of the response.");
    }

    /**
     * Test to upload attachment to JIRA
     */
    @Test
    public void addAttachmentToJira(){

        String flUplResp = given().header("Content-Type", "multipart/form-data").header("X-Atlassian-Token","no-check").filter(session).pathParams("key", "10105")
                .multiPart("file", new File("src/test/resources/jira.txt")).when().post("/rest/api/2/issue/{key}/attachments")
                .then().log().all().assertThat().statusCode(200).extract().response().asString();
    }

    /**
     * Test to extract a particular comment using query parameter and apply assertion
     */
    @Test
    public void testPathParamsAndQueryParams(){

        String expectedMsg = "This is test comment by automation";
        String responseAddComment = given().header("Content-Type","application/json").pathParams("key", "10105")
                .body("{\n" +
                        "    \"body\": \""+expectedMsg+"\"\n" +
                        "}").filter(session).when().post("/rest/api/2/issue/{key}/comment").then().assertThat().statusCode(201)
                .log().all().extract().response().asString();

        JsonPath jp = TestUtil.rawToJSON(responseAddComment);
        String commentID = jp.getString("id");

        String issueDetails = given().filter(session).pathParams("key", "10105").queryParam("fields", "comment")
                .log().all().get("/rest/api/2/issue/{key}")
                .then().log().all().extract().response().asString();

        System.out.println("Issue Details ---> " +issueDetails);
        JsonPath allComments = TestUtil.rawToJSON(issueDetails);
        int csize = allComments.getInt("fields.comment.comments.size()");
        for (int i=0;i<csize;i++){
            String commentStr = allComments.get("fields.comment.comments["+i+"].id").toString();
            if (commentStr.equalsIgnoreCase(commentID)){
                String msg = allComments.get("fields.comment.comments["+i+"].body").toString();
                Assert.assertEquals(msg, expectedMsg);
            }
        }
    }
}
