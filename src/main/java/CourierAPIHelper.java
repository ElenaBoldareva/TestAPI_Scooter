import com.google.gson.Gson;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class CourierAPIHelper {
    public Response createCourier(Courier courier) {
        Response response =
                given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(courier)
                        .when()
                        .post("/api/v1/courier");
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
                        .delete("/api/v1/courier/" + id);
        return response;
    }

    public Response deleteCourier() {
        Response response =
                given()
                        .delete("/api/v1/courier/");
        return response;
    }

    public Response getAuthorisationCourier(Courier courier) {
        Response response =
                given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(courier)
                        .when()
                        .post("/api/v1/courier/login");
        return response;
    }

}