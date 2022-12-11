import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import helper.CourierAPIHelper;
import helper.OrderAPIHelper;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import pojo.Courier;
import pojo.Order;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class OrderTest {
    CourierAPIHelper courierAPIHelper;
    OrderAPIHelper orderAPIHelper;

    @Before
    public void setUp() {
        RestAssured.baseURI = "http://qa-scooter.praktikum-services.ru";
        orderAPIHelper = new OrderAPIHelper();
        courierAPIHelper = new CourierAPIHelper();
    }

    @Test
    @DisplayName("Check status code of get orders")
    public void getOrdersTest() {
        Response response = orderAPIHelper.getOrders();
        response.then().assertThat().body("orders", notNullValue())
                .and()
                .statusCode(200);
    }

    @Test
    @DisplayName("Check status code of accept orders")
    public void acceptOrderTest() {
        Courier courier = new Courier("Tomuhnbyggbg", "1234", "saske");
        courierAPIHelper.createCourier(courier);
        int courierId = courierAPIHelper.getCourierId(courier);

        Order order = new Order("John", "Dym", "Moscow, 111 str", "4", "+7 800 355 35 35", 4, "2020-06-06", "Thanks", new String[]{"BLACK"});
        Response createResponse = orderAPIHelper.createOrder(order);
        String json = createResponse.asPrettyString();
        JsonParser jsonParser = new JsonParser();
        JsonObject rootObject = jsonParser.parse(json).getAsJsonObject();
        int track = rootObject.get("track").getAsInt();
        int orderId = orderAPIHelper.getOrderId(track);

        Response acceptOrder = orderAPIHelper.acceptOrder(orderId, courierId);
        acceptOrder.then().assertThat().body("ok", equalTo(true))
                .and()
                .statusCode(200);
        orderAPIHelper.cancelOrder(track);
        courierAPIHelper.deleteCourier(courierId);
    }

    @Test
    @DisplayName("Check status code of wrong id orders order")
    public void acceptOrderWithWrongIdTest() {
        Courier courier = new Courier("Tomuhnbyggbg", "1234", "saske");
        courierAPIHelper.createCourier(courier);
        int courierId = courierAPIHelper.getCourierId(courier);
        Response response = orderAPIHelper.acceptOrder(Integer.MAX_VALUE, courierId);
        response.then().assertThat().body("message", equalTo("Заказа с таким id не существует"))
                .and()
                .statusCode(404);
        courierAPIHelper.deleteCourier(courierId);
    }

    @Test
    @DisplayName("Check status code of wrong id courier order")
    public void acceptOrderWithWrongCourierIdTest() {
        Order order = new Order("John", "Dym", "Moscow, 111 str", "4", "+7 800 355 35 35", 4, "2020-06-06", "Thanks", new String[]{"BLACK"});
        Response createResponse = orderAPIHelper.createOrder(order);
        String json = createResponse.asPrettyString();
        JsonParser jsonParser = new JsonParser();
        JsonObject rootObject = jsonParser.parse(json).getAsJsonObject();
        int track = rootObject.get("track").getAsInt();
        int orderId = orderAPIHelper.getOrderId(track);

        Response acceptOrder = orderAPIHelper.acceptOrder(orderId, Integer.MAX_VALUE);
        acceptOrder.then().assertThat().body("message", equalTo("Курьера с таким id не существует"))
                .and()
                .statusCode(404);
        orderAPIHelper.cancelOrder(track);
    }

    /**
     * The test fails due the system returns an error with a code '404' and a message 'Not Found'.
     */
    @Test
    @DisplayName("Check status code of without id orders order")
    public void acceptOrderWithoutIdTest() {
        Courier courier = new Courier("Tomuhnbyggbg", "1234", "saske");
        courierAPIHelper.createCourier(courier);
        int courierId = courierAPIHelper.getCourierId(courier);
        Response response = orderAPIHelper.acceptWithoutOrderId(courierId);
        response.then().assertThat().body("message", equalTo("Недостаточно данных для поиска"))
                .and()
                .statusCode(400);
        courierAPIHelper.deleteCourier(courierId);
    }

    @Test
    @DisplayName("Check status code of wrong id orders order")
    public void acceptOrderWithoutCourierIdTest() {
        Order order = new Order("John", "Dym", "Moscow, 111 str", "4", "+7 800 355 35 35", 4, "2020-06-06", "Thanks", new String[]{"BLACK"});
        Response createResponse = orderAPIHelper.createOrder(order);
        String json = createResponse.asPrettyString();
        JsonParser jsonParser = new JsonParser();
        JsonObject rootObject = jsonParser.parse(json).getAsJsonObject();
        int track = rootObject.get("track").getAsInt();
        int orderId = orderAPIHelper.getOrderId(track);

        Response acceptResponse = orderAPIHelper.acceptWithoutCourierId(orderId);
        acceptResponse.then().assertThat().body("message", equalTo("Недостаточно данных для поиска"))
                .and()
                .statusCode(400);
        orderAPIHelper.cancelOrder(track);
    }

    @Test
    @DisplayName("Check status code of pojo.Order by track")
    public void getOrderByTrackTest() {
        Order order = new Order("John", "Dym", "Moscow, 111 str", "4", "+7 800 355 35 35", 4, "2020-06-06", "Thanks", new String[]{"BLACK"});
        Response createResponse = orderAPIHelper.createOrder(order);
        String json = createResponse.asPrettyString();
        JsonParser jsonParser = new JsonParser();
        JsonObject rootObject = jsonParser.parse(json).getAsJsonObject();
        int track = rootObject.get("track").getAsInt();
        Response acceptResponse = orderAPIHelper.getOrderByTrack(track);
        acceptResponse.then().assertThat().body("order", notNullValue())
                .and()
                .statusCode(200);
        orderAPIHelper.cancelOrder(track);
    }

    @Test
    @DisplayName("Check status code of order without track")
    public void getOrderWithoutTrackTest() {
        Response response = orderAPIHelper.getOrderWithoutTrack();
        response.then().assertThat().body("message", equalTo("Недостаточно данных для поиска"))
                .and()
                .statusCode(400);
    }

    @Test
    @DisplayName("Check status code of order wrong track")
    public void getOrderWrongTrackTest() {
        Response response = orderAPIHelper.getOrderByTrack(Integer.MAX_VALUE);
        response.then().assertThat().body("message", equalTo("Заказ не найден"))
                .and()
                .statusCode(404);
    }
}