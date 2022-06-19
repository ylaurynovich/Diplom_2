import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

@DisplayName("Create User")
public class CreateUserTest {
    private final UserClient userClient = new UserClient();
    private User user;
    private String accessToken;

    @Before
    @DisplayName("Create random user data")
    public void setUp() throws InterruptedException {
        user = UserFaker.getRandomUserData();
        TimeUnit.SECONDS.sleep(1);
    }

    @Test
    @DisplayName("Create unique user with valid fields")
    public void createUniqueUserTest(){
        Response response = userClient.createUser(user);
        accessToken = response.path("accessToken");
        response
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue());
    }

    @Test
    @DisplayName("Create duplicate user")
    public void createDuplicateUserTest(){
        User duplicateUser = UserFaker.getRandomUserData();
        userClient.createUser(duplicateUser);
        Response response = userClient.createUser(duplicateUser);
        response
                .then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("User already exists"));
    }

    @Test
    @DisplayName("Create user with missing data")
    public void createUserMissingDataTest(){
        User user = new User();
        Response response = userClient.createUser(user);
        response
                .then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @After
    @DisplayName("Delete user")
    public void tearDown(){
        if (accessToken != null) userClient.deleteUser(accessToken);
    }

}
