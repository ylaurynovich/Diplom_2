import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;



import static org.hamcrest.CoreMatchers.*;

@DisplayName("Create order")
public class CreateOrderTest {

    public static final String invalidOrderId = "6d58a2d7-77b3-4c67-8004-dbd31e937fce";
    private final OrderClient orderClient = new OrderClient();
    private Order orderIngredientsIdList;
    private final UserClient userClient = new UserClient();
    private String accessToken;
    private User user;

    @Before
    @DisplayName("Create user and extract access token")
    public void setUp() {
        orderIngredientsIdList = new Order(orderClient.getIngredientsId());
        user = UserFaker.getRandomUserData();
    }

    @Test
    @DisplayName("Create order without authorization")
    public void createOrderWithoutAuthorizationTest(){
        Response response = orderClient.createOrder(orderIngredientsIdList,"");
        response
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("name", notNullValue());
    }

    @Test
    @DisplayName("Create order with authorization")
    public void createOrderWithAuthorizationTest(){
        Response userCreationResponse = userClient.createUser(user);
        accessToken = userCreationResponse.path("accessToken");

        Response createOrderResponse = orderClient.createOrder(orderIngredientsIdList, accessToken);
        createOrderResponse
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("name", notNullValue());
    }

    @Test
    @DisplayName("Create order with no ingredients")
    public void createOrderNoIngredientsTest(){
        Response response = orderClient.createOrder(new Order(), "");
        response
                .then()
                .statusCode(400)
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Create order with ingredient invalid id")
    public void createOrderWithInvalidIngredientId(){
        ArrayList<String> listInvalidId = new ArrayList<>();
        listInvalidId.add(invalidOrderId);

        Response response = orderClient.createOrder(new Order(listInvalidId),"");
        response
                .then()
                .statusCode(500)
                .body("$", hasItem("Internal Server Error"));
    }

    @After
    @DisplayName("Delete user")
    public void tearDown() {
        if (accessToken != null) userClient.deleteUser(accessToken);
    }
}
