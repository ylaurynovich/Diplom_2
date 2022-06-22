import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;

public class BaseClient {
    protected static final String JSON = "application/json";
    public static final String BASEURL = "https://stellarburgers.nomoreparties.site";

    public static RequestSpecification getBaseSpec() {
        return new RequestSpecBuilder()
                .setContentType(JSON)
                .setBaseUri(BASEURL)
                .build();
}
}
