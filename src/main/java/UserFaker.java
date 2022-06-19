import com.github.javafaker.Faker;
import io.qameta.allure.Step;

public class UserFaker {
    @Step("Create random user data")
    public static User getRandomUserData() {
        Faker faker = new Faker();
        return new User(
                faker.internet().emailAddress(),
                faker.internet().password(),
                faker.name().firstName());
    }
}
