import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.equalTo;


@DisplayName("Modify User Data")
public class ModifyUserDataTest {

    private final UserClient userClient = new UserClient();
    private String accessToken;

    @Before
    @DisplayName("Create user and extract access token")
    public void setUp() throws InterruptedException {
        User user = UserFaker.getRandomUserData();
        Response response = userClient.createUser(user);
        accessToken = response.path("accessToken");
        TimeUnit.SECONDS.sleep(1);
    }

    @Test
    @DisplayName("Mofidy user data with authorization")
    public void modifyUserDataAuthorizedTest() {
        User newUser = UserFaker.getRandomUserData();
        Response responseChangedData = userClient.changeUserData(newUser, accessToken);
        responseChangedData
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(newUser.email))
                .body("user.name", equalTo(newUser.name));
    }

    @Test
    @DisplayName("Mofidy user data without authorization")
    public void modifyUserDataWithoutAuthorizationTest() {
        User user = UserFaker.getRandomUserData();
        Response responseChangedData = userClient.changeUserData(user, "");
        responseChangedData
                .then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }

    @After
    @DisplayName("Delete user")
    public void tearDown() {
        userClient.deleteUser(accessToken);
    }

}