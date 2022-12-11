package helper;

import com.google.gson.Gson;
import io.restassured.response.Response;
import pojo.Courier;

import static io.restassured.RestAssured.given;

public class CourierAPIHelper {
    private static final String COURIER_URL = "/api/v1/courier/";
    private static final String COURIER_LOGIN_URL = COURIER_URL + "login";
    public Response createCourier(Courier courier) {
        Response response =
                given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(courier)
                        .when()
                        .post(COURIER_URL);
        return response;
    }

    public int getCourierId(Courier courier) {
        Response response = getAuthorisationCourier(courier);
        String json = response.body().asString();
        Gson gson = new Gson();
        int id = gson.fromJson(json, Courier.class).getId();
        return id;
    }

    public Response deleteCourier(int id) {
        Response response =
                given()
                        .header("Content-type", "application/json")
                        .delete(COURIER_URL + id);
        return response;
    }

    public Response deleteCourier() {
        Response response =
                given()
                        .delete(COURIER_URL);
        return response;
    }

    public Response getAuthorisationCourier(Courier courier) {
        Response response =
                given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(courier)
                        .when()
                        .post(COURIER_LOGIN_URL);
        return response;
    }

}