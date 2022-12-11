package helper;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import io.restassured.response.Response;
import pojo.Order;
import pojo.TrackOrder;

import static io.restassured.RestAssured.given;

public class OrderAPIHelper {
    private static final String ORDER_URL = "/api/v1/orders/";
    private static final String ORDER_TRACK_URL = ORDER_URL + "track";
    private static final String ORDER_ACCEPT_URL = ORDER_URL + "accept/";
    private static final String ORDER_CANCEL_URL = ORDER_URL + "cancel";

    public Response createOrder(Order order) {
        Response response =
                given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(order)
                        .when()
                        .post(ORDER_URL);
        return response;
    }

    public Response getOrders() {
        Response response =
                given()
                        .get(ORDER_URL);
        return response;
    }

    public Response getOrderByTrack(int t) {
        Response response =
                given()
                        .queryParam("t", t)
                        .get(ORDER_TRACK_URL);
        return response;
    }

    public Response getOrderWithoutTrack() {
        Response response =
                given()
                        .get(ORDER_TRACK_URL);
        return response;
    }

    public Response acceptOrder(int orderId, int courierId) {
        Response response =
                given()
                        .queryParam("courierId", courierId)
                        .put(ORDER_ACCEPT_URL + orderId);
        return response;
    }

    public Response acceptWithoutOrderId(int courierId) {
        Response response =
                given()
                        .queryParam("courierId", courierId)
                        .put(ORDER_ACCEPT_URL);
        return response;
    }

    public Response acceptWithoutCourierId(int orderId) {
        Response response =
                given()
                        .put(ORDER_ACCEPT_URL+ orderId);
        return response;
    }

    public void cancelOrder(int track) {
        TrackOrder trackOrder = new TrackOrder(track);
        given()
                .header("Content-type", "application/json")
                .and()
                .body(trackOrder)
                .when()
                .post(ORDER_CANCEL_URL);
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