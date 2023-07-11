package tests;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import preparation.CreateBookingBody;


import static io.restassured.RestAssured.given;

public class RestAssuredTests {

    public String cookiesToken;
    private int bookingId;
    private int testBookingId;

    private String baseURI;

    static final String FIRST_NAME = "Ivan";
    static final String LAST_NAME = "Ivanov";
    static final String ADDITIONAL_NEEDS = "Water";
    static final int TOTAL_PRICE = 200;
    @BeforeMethod
    public void setup() {
        baseURI = "https://restful-booker.herokuapp.com";

        RestAssured.requestSpecification = new RequestSpecBuilder()
                .setBaseUri(baseURI)
                .setContentType(ContentType.JSON)
                .build();
        Response response = given()
                .body("{\"username\":\"admin\",\"password\":\"password123\"}")
                .post("/auth");

        String token = response.jsonPath().getString("token");

        if (cookiesToken == null) {
            cookiesToken = token;

            RestAssured.requestSpecification.cookie("token", token);

            System.out.println(">>> token = "+ cookiesToken);
        }

        response.then().statusCode(200);
    }
    @Test
    public void CreateBooking() {

        CreateBookingBody.BookingDates bookingDates = new CreateBookingBody.BookingDates().builder()
                .checkin("2023-01-07")
                .checkout("2023-01-08")
                .build();

        CreateBookingBody requestBody = new CreateBookingBody().builder()
                .firstname("Ivan")
                .lastname("Ivanov")
                .totalprice(200)
                .depositpaid(true)
                .bookingdates(bookingDates)
                .additionalneeds("Dinner")
                .build();

        Response response = given().log().all()
                .baseUri(baseURI)
                .contentType("application/json")
                .header("Accept", "application/json")
                .cookies("token", cookiesToken)
                .body(requestBody)
                .post("/booking");


        String responseBody = response.getBody().asString();

        JSONObject responseJson = new JSONObject(responseBody);

        bookingId = responseJson.getInt("bookingid");

        System.out.println(">>> create bookingid = "+bookingId);

        response.then().statusCode(200);
    }

    @Test
    public void getAllBookingIdsTest() {

        Response response = given().log().all()
                .baseUri(baseURI)
                .contentType("application/json")
                .header("Accept", "application/json")
                .cookies("token", cookiesToken)
                .get("/booking");

        response.then().statusCode(200);
        testBookingId = response.jsonPath().getInt("[0].bookingid");
    }



    @Test
    public void deleteBookingTest() {

        // get some id
        Response response = given().log().all()
                .baseUri(baseURI)
                .contentType("application/json")
                .header("Accept", "application/json")
                .cookies("token", cookiesToken)
                .get("/booking");

        int id = response.jsonPath().getInt("[0].bookingid");

        // delete this id
        Response response2 = given().log().all()
                .baseUri(baseURI)
                .header("Authorization", "Basic YWRtaW46cGFzc3dvcmQxMjM=")
                .contentType("text/plain")
                .cookies("token", cookiesToken)
                .pathParam("id", id)
                .delete("/booking/{id}");

        response2.then().statusCode(201);
    }
}