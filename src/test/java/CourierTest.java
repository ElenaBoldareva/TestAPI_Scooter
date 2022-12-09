import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class CourierTest {
    private CourierAPIHelper courierAPIHelper;

    @Before
    public void setUp() {
        RestAssured.baseURI = "http://qa-scooter.praktikum-services.ru";
        courierAPIHelper = new CourierAPIHelper();
    }

    @Test
    @DisplayName("Check status code of create courier")
    public void createNewCourierTest() {
        Courier courier = new Courier("limgyghl", "1234", "saske");
        Response response = courierAPIHelper.createCourier(courier);
        response.then().assertThat().body("ok", equalTo(true))
                .and()
                .statusCode(201);
        int id = courierAPIHelper.getCourierId(courier);
        courierAPIHelper.deleteCourier(id);
    }

    @Test
    @DisplayName("Check status code of create copy courier")
    public void createCopyCourierTest() {
        Courier courier = new Courier("limgyghl", "4321", "saske");
        courierAPIHelper.createCourier(courier);
        Response response = courierAPIHelper.createCourier(courier);
        response.then().assertThat().body("message", equalTo("Этот логин уже используется. Попробуйте другой."))
                .and()
                .statusCode(409);
        int id = courierAPIHelper.getCourierId(courier);
        courierAPIHelper.deleteCourier(id);
    }

    @Test
    @DisplayName("Check status code of create courier without login")
    public void courierCreateWithoutLoginTest() {
        Courier courier = new Courier("", "1234", "saske");
        Response response = courierAPIHelper.createCourier(courier);
        response.then().assertThat().body("message", equalTo("Недостаточно данных для создания учетной записи"))
                .and()
                .statusCode(400);
    }


    @Test
    @DisplayName("Check status code of create courier without password")
    public void courierCreateWithoutPasswordTest() {
        Courier courier = new Courier("John", "", "saske");
        Response response = courierAPIHelper.createCourier(courier);
        response.then().assertThat().body("message", equalTo("Недостаточно данных для создания учетной записи"))
                .and()
                .statusCode(400);
    }

    @Test
    @DisplayName("Check status code of authorisation courier")
    public void courierAuthorisationTest() {
        Courier courier = new Courier("John", "1234", "saske");
        courierAPIHelper.createCourier(courier);
        Response response = courierAPIHelper.getAuthorisationCourier(courier);
        response.then().assertThat().body("id", notNullValue())
                .and()
                .statusCode(200);
        int id = courierAPIHelper.getCourierId(courier);
        courierAPIHelper.deleteCourier(id);
    }

    @Test
    @DisplayName("Check status code of authorisation courier without login")
    public void courierAuthorisationWithoutLoginTest() {
        Courier courier = new Courier("", "1234", "saske");
        Response response = courierAPIHelper.getAuthorisationCourier(courier);
        response.then().assertThat().body("message", equalTo("Недостаточно данных для входа"))
                .and()
                .statusCode(400);
    }

    @Test
    @DisplayName("Check status code of authorisation courier without password")
    public void courierAuthorisationWithoutPasswordTest() {
        Courier courier = new Courier("wertyuiop", "1234", "saske");
        courierAPIHelper.createCourier(courier);
        Courier courierWithoutPassword = new Courier("wertyuiop", "", "saske");
        Response response = courierAPIHelper.getAuthorisationCourier(courierWithoutPassword);
        response.then().assertThat().body("message", equalTo("Недостаточно данных для входа"))
                .and()
                .statusCode(400);
        int id = courierAPIHelper.getCourierId(courier);
        courierAPIHelper.deleteCourier(id);
    }

    @Test
    @DisplayName("Check status code of authorisation courier wrong password")
    public void courierAuthorisationWrongPasswordTest() {
        Courier courier = new Courier("John", "54343.", "saske");
        courierAPIHelper.createCourier(courier);
        Courier courierWrongPassword = new Courier("John", "11111", "saske");
        Response response = courierAPIHelper.getAuthorisationCourier(courierWrongPassword);
        response.then().assertThat().body("message", equalTo("Учетная запись не найдена"))
                .and()
                .statusCode(404);
        int id = courierAPIHelper.getCourierId(courier);
        courierAPIHelper.deleteCourier(id);
    }

    @Test
    @DisplayName("Check status code of authorisation courier wrong login")
    public void courierAuthorisationWrongLoginTest() {
        Courier courier = new Courier("John", "54343.", "saske");
        courierAPIHelper.createCourier(courier);
        Courier courierWrongLogin = new Courier("Tjkhiuhn", "11111", "saske");
        Response response = courierAPIHelper.getAuthorisationCourier(courierWrongLogin);
        response.then().assertThat().body("message", equalTo("Учетная запись не найдена"))
                .and()
                .statusCode(404);
        int id = courierAPIHelper.getCourierId(courier);
        courierAPIHelper.deleteCourier(id);
    }

    @Test
    @DisplayName("Check status code of delete courier")
    public void deleteCourierTest() {
        Courier courier = new Courier("limgyghl", "1234", "saske");
        courierAPIHelper.createCourier(courier);
        int id = courierAPIHelper.getCourierId(courier);
        Response response = courierAPIHelper.deleteCourier(id);
        response.then().assertThat().body("ok", equalTo(true))
                .and()
                .statusCode(200);
    }

    /**
     * The test fails due the system returns an error with a code '404' and a message 'Not Found'.
     */
    @Test
    @DisplayName("Check status code of delete courier without id")
    public void deleteCourierWithoutIdTest() {
       Response response = courierAPIHelper.deleteCourier();
        response.then().assertThat().body("message", equalTo("Недостаточно данных для удаления курьера"))
                .and()
                .statusCode(400);
    }

    @Test
    @DisplayName("Check status code of delete courier wrong id")
    public void deleteCourierWrongIdTest() {
        Response response = courierAPIHelper.deleteCourier(Integer.MAX_VALUE);
        response.then().assertThat().body("message", equalTo("Курьера с таким id нет."))
                .and()
                .statusCode(404);
    }
}
