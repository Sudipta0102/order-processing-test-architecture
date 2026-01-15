package org.myApp.api;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;

public abstract class BaseApiTest {

    @BeforeAll
    static void setup(){

        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8081;

        // base path empty because endpoints are absolute. (/orders)
        RestAssured.basePath="";

        // help debugging, no other reason
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }
}
