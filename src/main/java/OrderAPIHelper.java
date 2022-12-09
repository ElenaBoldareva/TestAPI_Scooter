import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class OrderAPIHelper {

    public Response createOrder(Order order) {
        Response response =
                given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(order)
                        .when()
                        .post("/api/v1/orders");
        return response;
    }

    public Response getOrders() {
        Response response =
                given()
                        .get("/api/v1/orders/");
        return response;
    }

    public Response getOrderByTrack(int t) {
        Response response =
                given()
                        .queryParam("t", t)
                        .get("/api/v1/orders/track");
        return response;
    }

    public Response getOrderWithoutTrack() {
        Response response =
                given()
                        .get("/api/v1/orders/track");
        return response;
    }

    public Response acceptOrder(int orderId, int courierId) {
        Response response =
                given()
                        .queryParam("courierId", courierId)
                        .put("/api/v1/orders/accept/" + orderId);
        return response;
    }

    public Response acceptWithoutOrderId(int courierId) {
        Response response =
                given()
                        .queryParam("courierId", courierId)
                        .put("/api/v1/orders/accept/");
        return response;
    }

    public Response acceptWithoutCourierId(int orderId) {
        Response response =
                given()
                        .put("/api/v1/orders/accept/" + orderId);
        return response;
    }

    public void cancelOrder(int track) {
        TrackOrder trackOrder = new TrackOrder(track);
        given()
                .header("Content-type", "application/json")
                .and()
                .body(trackOrder)
                .when()
                .post("/api/v1/orders/cancel");
    }

    public int getOrderId(int track) {
        Response response = getOrderByTrack(track);
        String json = response.asPrettyString();

        JsonParser jsonParser = new JsonParser();
        JsonElement jsonOrder = jsonParser.parse(json)
                .getAsJsonObject()
                .get("order");

        Gson gson = new Gson();
        int id = gson.fromJson(jsonOrder, Order.class).getId();
        return id;
    }
}