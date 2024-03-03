package com.basics;

import com.utils.Payloads;
import com.utils.TestUtil;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import static io.restassured.RestAssured.*;

public class BookOperations {
    @BeforeMethod
    public void beforeMethod(){
        RestAssured.baseURI="https://rahulshettyacademy.com";
    }

    @Test
    public void getBook(){
        String resp = given().log().all().header("Content-Type","application/json")
                .body(Payloads.bookDetails()).when().post("Library/Addbook.php")
                .then().log().all().assertThat().statusCode(200)
                .extract().response().asString();
        JsonPath jp = TestUtil.rawToJSON(resp);
        //jp.get("ID");
        System.out.println("Response : " +resp);
    }

    @Test(dataProvider = "bookData")
    public void addBook(String isbn, String aisle){
        String resp = given().log().all().header("Content-Type","application/json")
                .body(Payloads.addBook(isbn, aisle)).when().post("Library/Addbook.php")
                .then().assertThat().statusCode(200)
                .extract().response().asString();
        System.out.println("Response is : " +resp);

        JsonPath jp = TestUtil.rawToJSON(resp);
        Assert.assertEquals(jp.getString("Msg"), "successfully added");

        //Clean-up the data
        String bookID = jp.getString("ID");
        String deleteResp = given().log().all().header("Content-Type","application/json")
                .body("{ \"ID\" : \""+bookID+"\"}").when().post("Library/DeleteBook.php")
                .then().assertThat().statusCode(200)
                .extract().response().asString();
        System.out.println(deleteResp);

        JsonPath jpDelete = TestUtil.rawToJSON(deleteResp);
        String deleteMsg = jpDelete.getString("msg");
        Assert.assertEquals(deleteMsg, "book is successfully deleted");

    }

    @DataProvider(name="bookData")
    public Object [][] bookDetails(){
        return  new Object[][] {{"pratik", "1234"},{"pratik", "1235"}, {"pratik", "1236"}};
    }
}
