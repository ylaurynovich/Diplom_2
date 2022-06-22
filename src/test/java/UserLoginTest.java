import io.qameta.allure.junit4.DisplayName;
import io.restassured.matcher.ResponseAwareMatcher;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

@DisplayName("User login")
public class UserLoginTest {

    private final UserClient userClient = new UserClient();
    private User user;
    private String accessToken;

    @Before
    @DisplayName("Create user with generated user data")
    public void setUp() throws InterruptedException {
        user = UserFaker.getRandomUserData();
        Response response = userClient.createUser(user);
        accessToken = response.path("accessToken");
        TimeUnit.SECONDS.sleep(2);
    }

    @Test
    @DisplayName("User login")
    public void userLoginTest(){
        Response response = userClient.loginUser(user);
        ResponseAwareMatcher<Response> equalTo;
        response
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue())
                .body("user", notNullValue());
    }

    @Test
    @DisplayName("Invalid User login")
    public void userInvalidLoginTest(){
        User user = UserFaker.getRandomUserData();
        user.setName("");
        Response response = userClient.loginUser(user);
        response
                .then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }

    @After
    @DisplayName("Delete user")
    public void tearDown() {
        if (accessToken != null) userClient.deleteUser(accessToken);
    }
}
