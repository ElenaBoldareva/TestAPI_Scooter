import helper.OrderAPIHelper;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import pojo.Order;

import static org.hamcrest.CoreMatchers.notNullValue;

@RunWith(Parameterized.class)
public class OrderParameterizedTest {

    OrderAPIHelper orderAPIHelper;
    Order order;

    public OrderParameterizedTest(Order order) {
        this.order = order;
        RestAssured.baseURI = "http://qa-scooter.praktikum-services.ru";
        orderAPIHelper = new OrderAPIHelper();
    }

    @Parameterized.Parameters
    public static Object[][] getTestCreateNewOrder() {
        return new Object[][]{
                {new Order("John", "Dym", "Moscow, 111 str", "4", "+7 800 355 35 35", 4, "2020-06-06", "Thanks", new String[]{"BLACK"})},
                {new Order("John", "Dym", "Moscow, 111 str", "4", "+7 800 355 35 35", 4, "2020-06-06", "Thanks", new String[]{"GREY"})},
                {new Order("John", "Dym", "Moscow, 111 str", "4", "+7 800 355 35 35", 4, "2020-06-06", "Thanks", new String[]{"BLACK", "GREY"})},
                {new Order("John", "Dym", "Moscow, 111 str", "4", "+7 800 355 35 35", 4, "2020-06-06", "Thanks", null)}

        };
    }

    @Test
    public void createNewOrderTest() {
        Response response = orderAPIHelper.createOrder(order);
        response.then().assertThat().body("track", notNullValue())
                .and()
                .statusCode(201);
    }
}