package com.romcharm.it;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Header;
import com.romcharm.authorization.JWTUtil;
import com.romcharm.defaults.Role;
import com.romcharm.domain.romcharm.Family;
import com.romcharm.domain.User;
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
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@Profile("test")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(value = "classpath:config.properties")
public class FamiliesControllerIT {

    private static final String FIRST_NAME = "firstName";

    private static final String LAST_NAME = "lastName";

    private static final String EMAIL = "email@email.com";

    @Value("${local.server.port}")
    private int port;

    @MockBean
    private DynamoDBMapper dynamoDBMapper;

    @Autowired
    private JWTUtil jwtUtil;

    @Before
    public void setup() {
        RestAssured.port = port;
    }

    @Test
    public void whenFamilyDoesNotExistThenReturn404() {
        Mockito.when(dynamoDBMapper.load(Family.class, EMAIL)).thenReturn(null);

        given()
            .header(new Header("Authorization", getToken(Role.ROLE_ROMCHARM_APP)))
        .when()
            .get("/families/notFoundName")
        .then()
            .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    public void whenTryingToGetFamilyWithNonRomCharmRoleExpectForbiddenAccess() {
        Mockito.when(dynamoDBMapper.load(Family.class, EMAIL)).thenReturn(null);

        given()
            .header(new Header("Authorization", getToken(Role.ROLE_MYPAGE_APP)))
        .when()
            .get("/families/notFoundName")
        .then()
            .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    public void whenFamilyDoesExistThenReturnObject() {
        String foundName = "foundName";
        Family expectedFamily = new Family(foundName, FIRST_NAME, LAST_NAME, true, 5);

        Mockito.when(dynamoDBMapper.load(Family.class, foundName)).thenReturn(expectedFamily);

        Family result =
            given()
                .contentType(ContentType.JSON)
                .header(new Header("Authorization", getToken(Role.ROLE_ROMCHARM_APP)))
            .when()
                .get(String.format("families/%s", foundName))
            .getBody()
                .as(Family.class);

        assertThat(result, is(expectedFamily));
    }

    @Test
    public void whenTryingOverwriteFamilyReturn400Error() {
        String email = "toBeSaved";
        Family initialFamily = new Family(email, null, null, false, 0);

        Mockito.when(dynamoDBMapper.load(Family.class, initialFamily)).thenReturn(initialFamily);

        Family toSave = new Family(email, FIRST_NAME, null, false, 0);

        given()
            .contentType(ContentType.JSON)
            .header(new Header("Authorization", getToken(Role.ROLE_ROMCHARM_APP)))
            .body(toSave)
        .when()
            .post("families/family")
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void whenSavingFamilyWithFamilyNameNullShouldReturn400Status() {
        Family toSave = new Family(null, FIRST_NAME, LAST_NAME, true, 4);

        given()
            .contentType(ContentType.JSON)
            .header(new Header("Authorization", getToken(Role.ROLE_ROMCHARM_APP)))
            .body(toSave)
        .when()
            .post("families/family")
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void whenSavingFamilyWithFamilyNameIsEmptyShouldReturn400Status() {
        Family toSave = new Family("", FIRST_NAME, LAST_NAME, false, 0);

        given()
            .contentType(ContentType.JSON)
            .header(new Header("Authorization", getToken(Role.ROLE_ROMCHARM_APP)))
            .body(toSave)
        .when()
            .post("families/family")
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void whenSavingFamilyWithFirstNameIsNullShouldReturn400Status() {
        Family toSave = new Family(EMAIL, null, LAST_NAME, false, 4);

        given()
            .contentType(ContentType.JSON)
            .header(new Header("Authorization", getToken(Role.ROLE_ROMCHARM_APP)))
            .body(toSave)
        .when()
            .post("families/family")
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void whenSavingFamilyWithFirstNameIsEmptyShouldReturn400Status() {
        Family toSave = new Family(EMAIL, "", LAST_NAME, false, 4);

        given()
            .contentType(ContentType.JSON)
            .header(new Header("Authorization", getToken(Role.ROLE_ROMCHARM_APP)))
            .body(toSave)
        .when()
            .post("families/family")
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void whenSavingFamilyWithLastNameIsNullShouldReturn400Status() {
        Family toSave = new Family(EMAIL, FIRST_NAME, null, false, 4);

        given()
            .contentType(ContentType.JSON)
            .header(new Header("Authorization", getToken(Role.ROLE_ROMCHARM_APP)))
            .body(toSave)
        .when()
            .post("families/family")
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void whenSavingFamilyWithLastNameIsEmptyShouldReturn400Status() {
        Family toSave = new Family(EMAIL, FIRST_NAME, "", false, 4);

        given()
            .contentType(ContentType.JSON)
            .header(new Header("Authorization", getToken(Role.ROLE_ROMCHARM_APP)))
            .body(toSave)
        .when()
            .post("families/family")
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void whenSavingFamilyWithIsAttendingFieldIsNullShouldReturn400Status() {
        Family toSave = new Family(EMAIL, FIRST_NAME, LAST_NAME, null, 4);

        given()
            .contentType(ContentType.JSON)
            .header(new Header("Authorization", getToken(Role.ROLE_ROMCHARM_APP)))
            .body(toSave)
        .when()
            .post("families/family")
         .then()
            .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void whenSavingFamilyWithIsNumberOfPeopleAttendingFieldIsNullShouldReturn400Status() {
        Family toSave = new Family(EMAIL, FIRST_NAME, LAST_NAME, true, null);

        given()
            .contentType(ContentType.JSON)
            .header(new Header("Authorization", getToken(Role.ROLE_ROMCHARM_APP)))
            .body(toSave)
        .when()
            .post("families/family")
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void whenSavingFamilyANonRomCharmRoleExpectForbiddenAccess() {
        Family toSave = new Family(EMAIL, FIRST_NAME, LAST_NAME, false, 4);

        given()
            .contentType(ContentType.JSON)
            .header(new Header("Authorization", getToken(Role.ROLE_MYPAGE_APP)))
            .body(toSave)
        .when()
            .post("families/family")
        .then()
            .statusCode(HttpStatus.FORBIDDEN.value());
    }

    private String getToken(Role role) {
        User user = new User("user", "pass", role.getName());
        return jwtUtil.generateToken(user);
    }
}
