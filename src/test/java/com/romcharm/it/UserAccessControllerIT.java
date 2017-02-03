package com.romcharm.it;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Header;
import com.romcharm.authorization.JWTUtil;
import com.romcharm.defaults.Role;
import com.romcharm.domain.Login;
import com.romcharm.domain.Token;
import com.romcharm.domain.User;
import com.romcharm.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@Profile("test")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(value = "classpath:config.properties")
public class UserAccessControllerIT {

    @Value("${local.server.port}")
    private int port;

    @MockBean
    private DynamoDBMapper dynamoDBMapper;

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;
    private String username;
    private String password;

    @Before
    public void setup() {
        RestAssured.port = port;
    }

    @Test
    public void whenUserNameDoesExistThenReturnToken() {
        String userName = "user";
        String password = "password";
        String expectedToken = addAndRetireveUserToken(userName, password, Role.ROLE_CLIENT_APP);

        Login login = new Login(userName, password);

        Token token =
                given()
                    .contentType(ContentType.JSON)
                    .body(login)
                .when()
                    .post("users/auth")
                .getBody()
                    .as(Token.class);

        assertThat(token, is(notNullValue()));
        assertThat(token.getToken(), is(expectedToken));
    }

    @Test
    public void whenUserNameDoesNotExistInLoginObjectThenReturn400Status() {
        Login login = new Login(null, "password");

        given()
               .contentType(ContentType.JSON)
               .body(login)
        .when()
               .post("users/auth")
        .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void whenPasswordDoesNotExistInLoginObjectThenReturn400Status() {
        Login login = new Login("username", null);

        given()
                .contentType(ContentType.JSON)
                .body(login)
        .when()
                .post("users/auth")
        .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void whenUserIsNotFoundReturn404() {
        Login login = new Login("random", "so");

        given()
                .contentType(ContentType.JSON)
                .body(login)
        .when()
                .post("users/auth")
        .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    public void whenUserIsFoundButPasswordDoesNotMatchReturn404() {
        username = "userCheck";
        password = "randomSo";
        User user = new User(username, password, Role.ROLE_ADMIN.getName());
        userRepository.save(user);

        Login login = new Login(username, "anotherPass");

        given()
                .contentType(ContentType.JSON)
                .body(login)
        .when()
                .post("users/auth")
        .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    public void whenSavingUserNameThoseDetailsShouldBeSavedIfAuthorizationIsOfRoleAdmin() {
        String usernameAdmin = "admin";
        String adminToken = addAndRetireveUserToken(usernameAdmin, usernameAdmin, Role.ROLE_ADMIN);

        String newUsername = "newUser";
        User newUser = new User(newUsername, "newPass", Role.ROLE_CLIENT_APP.getName());

        given()
                .contentType(ContentType.JSON)
                .header(new Header("Authorization", adminToken))
                .body(newUser)
        .when()
                .post("users/add")
        .then()
                .statusCode(HttpStatus.CREATED.value());

        Mockito.verify(dynamoDBMapper).save(newUser);
    }

    @Test
    public void whenSavingUserNameWithClientAppRoleThenForbiddenResponseExpected() {
        String clientToken = addAndRetireveUserToken("client", "client", Role.ROLE_CLIENT_APP);

        String newUsername = "newUser";
        User newUser = new User(newUsername, "newPass", Role.ROLE_CLIENT_APP.getName());

        given()
                .contentType(ContentType.JSON)
                .header(new Header("Authorization", clientToken))
                .body(newUser)
        .when()
                .post("users/add")
        .then()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    public void whenSavingUserWithNoUsernameThenExpect400() {
        String adminToken = addAndRetireveUserToken("admin", "admin", Role.ROLE_ADMIN);

        User user = User.builder().password("password").role(Role.ROLE_CLIENT_APP.name()).build();

        given()
                .contentType(ContentType.JSON)
                .header(new Header("Authorization", adminToken))
                .body(user)
        .when()
                .post("users/add")
        .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void whenSavingUserWithNoRoleThenExpect400() {
        String adminToken = addAndRetireveUserToken("admin", "admin", Role.ROLE_ADMIN);

        User user = User.builder().username("someName").password("password").build();

        given()
                .contentType(ContentType.JSON)
                .header(new Header("Authorization", adminToken))
                .body(user)
        .when()
                .post("users/add")
        .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void whenSavingUserWithNoPasswordThenExpect400() {
        String adminToken = addAndRetireveUserToken("admin", "admin", Role.ROLE_ADMIN);

        User user = User.builder().username("someName").role(Role.ROLE_CLIENT_APP.getName()).build();

        given()
                .contentType(ContentType.JSON)
                .header(new Header("Authorization", adminToken))
                .body(user)
        .when()
                .post("users/add")
        .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void whenTryingToAddSameusernameTwiceExpect400Error() {
        String adminToken = addAndRetireveUserToken("admin", "admin", Role.ROLE_ADMIN);

        String username = "repeatName";
        User user = new User(username, "repeat", Role.ROLE_CLIENT_APP.getName());

        Mockito.when(dynamoDBMapper.load(User.class, username)).thenReturn(user);

        given()
                .contentType(ContentType.JSON)
                .header(new Header("Authorization", adminToken))
                .body(user)
        .when()
                .post("users/add")
        .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    private String addAndRetireveUserToken(String username, String password, Role role) {
        User user = new User(username, password, role.getName());
        Mockito.when(dynamoDBMapper.load(User.class, username)).thenReturn(user);
        return jwtUtil.generateToken(user);
    }
}
