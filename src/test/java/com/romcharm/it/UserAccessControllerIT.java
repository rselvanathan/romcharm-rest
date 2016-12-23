package com.romcharm.it;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.romcharm.Application;
import com.romcharm.defaults.Role;
import com.romcharm.domain.UserRole;
import com.romcharm.repositories.UserAccessRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.when;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration({Application.class, EmbeddedMongoAutoConfiguration.class})
@WebAppConfiguration
@IntegrationTest("server.port:10")
public class UserAccessControllerIT {

    @Value("${local.server.port}")
    private int port;

    @Autowired
    private UserAccessRepository userAccessRepository;

    @Before
    public void setup() {
        RestAssured.port = port;
    }

    @Test
    public void whenUsernameIsNotFoundReturn404() {
        when()
                .get("users/notFound")
        .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    public void whenUserNameDoesExistThenReturnUserAccess() {
        String userName = "anotherUser";
        UserRole charmiRom = UserRole.builder().userAccessName(userName).role(Role.GUEST.name()).build();

        userAccessRepository.save(charmiRom);

        UserRole userRole =
                given()
                    .contentType(ContentType.JSON)
                .when()
                    .get("users/" + userName)
                .getBody()
                    .as(UserRole.class);

        assertThat(userRole, is(charmiRom));
    }

    @Test
    public void whenSavingUserNameThoseDetailsShouldBeSaved() {
        String userName = "newUsername";
        UserRole charmiRom = UserRole.builder().userAccessName(userName).role(Role.GUEST.name()).build();

        given()
                .contentType(ContentType.JSON)
                .body(charmiRom)
        .when()
                .put("users/add")
        .then()
                .statusCode(HttpStatus.CREATED.value());

        UserRole result = userAccessRepository.findOne(userName);

        assertThat(result, is(charmiRom));
    }

    @Test
    public void whenSavingUserRoleWithNoUsernameThenExpect400() {
        UserRole userRole = UserRole.builder().role(Role.GUEST.name()).build();

        given()
                .contentType(ContentType.JSON)
                .body(userRole)
        .when()
                .put("users/add")
        .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void whenSavingUserRoleWithRoleThenExpect400() {
        UserRole userRole = UserRole.builder().userAccessName("someName").build();

        given()
                .contentType(ContentType.JSON)
                .body(userRole)
        .when()
                .put("users/add")
        .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void whenTryingToAddSameUserAccessNameTwiceExpect400Error() {
        String userAccessName = "repeatName";
        UserRole userRole = UserRole.builder().userAccessName(userAccessName).role(Role.GUEST.name()).build();

        userAccessRepository.save(userRole);

        given()
                .contentType(ContentType.JSON)
                .body(userRole)
        .when()
                .put("users/add")
        .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }
}
