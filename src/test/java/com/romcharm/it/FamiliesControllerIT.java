package com.romcharm.it;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.romcharm.Application;
import com.romcharm.config.MongoConfig;
import com.romcharm.domain.Family;
import com.romcharm.repositories.FamiliesRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.when;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableAutoConfiguration(exclude = MongoConfig.class)
@TestPropertySource(value = "classpath:config.properties")
public class FamiliesControllerIT {

    private static final String FIRST_NAME = "firstName";

    private static final String LAST_NAME = "lastName";

    private static final String EMAIL = "email@email.com";

    @Value("${local.server.port}")
    private int port;

    @Autowired
    private FamiliesRepository familiesRespository;

    @Before
    public void setup() {
        RestAssured.port = port;
    }

    @Test
    public void whenFamilyDoesNotExistThenReturn404() {
        when()
            .get("/families/notFoundName")
        .then()
            .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    public void whenFamilyDoesExistThenReturnObject() {
        String foundName = "foundName";
        Family expectedFamily = Family.builder()
                                      .email(foundName)
                                      .firstName(FIRST_NAME)
                                      .lastName(LAST_NAME)
                                      .areAttending(true)
                                      .numberAttending(5)
                                      .build();

        familiesRespository.save(expectedFamily);

        Family result =
            given()
                .contentType(ContentType.JSON)
            .when()
                .get(String.format("families/%s", foundName))
            .getBody()
                .as(Family.class);

        assertThat(result, is(expectedFamily));
    }

    @Test
    public void whenSavingFamilyThoseDetailsShouldBeSaved() {
        String email = "toBeSaved";
        Family initialFamily = Family.builder()
                                     .email(email)
                                     .build();
        familiesRespository.save(initialFamily);

        Family toSave = Family.builder()
                              .email(email)
                              .firstName(FIRST_NAME)
                              .lastName(LAST_NAME)
                              .areAttending(true)
                              .numberAttending(4)
                              .build();

        given()
            .contentType(ContentType.JSON)
            .body(toSave)
        .when()
            .put("families/family")
        .then()
            .statusCode(HttpStatus.CREATED.value());

        Family result = familiesRespository.findOne(email);

        assertThat(result, is(toSave));
    }

    @Test
    public void whenSavingFamilyWithFamilyNameNullShouldReturn400Status() {
        Family toSave = Family.builder()
                              .firstName(FIRST_NAME)
                              .lastName(LAST_NAME)
                              .areAttending(true)
                              .numberAttending(4)
                              .build();

        given()
            .contentType(ContentType.JSON)
            .body(toSave)
        .when()
            .put("families/family")
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void whenSavingFamilyWithFamilyNameIsEmptyShouldReturn400Status() {
        Family toSave = Family.builder()
                              .email("")
                              .firstName(FIRST_NAME)
                              .lastName(LAST_NAME)
                              .areAttending(true)
                              .numberAttending(4)
                              .build();

        given()
            .contentType(ContentType.JSON)
            .body(toSave)
        .when()
            .put("families/family")
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void whenSavingFamilyWithFirstNameIsNullShouldReturn400Status() {
        Family toSave = Family.builder()
                              .email(EMAIL)
                              .lastName(LAST_NAME)
                              .areAttending(true)
                              .numberAttending(4)
                              .build();

        given()
            .contentType(ContentType.JSON)
            .body(toSave)
            .when()
            .put("families/family")
            .then()
            .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void whenSavingFamilyWithFirstNameIsEmptyShouldReturn400Status() {
        Family toSave = Family.builder()
                              .email(EMAIL)
                              .firstName("")
                              .lastName(LAST_NAME)
                              .areAttending(true)
                              .numberAttending(4)
                              .build();

        given()
            .contentType(ContentType.JSON)
            .body(toSave)
            .when()
            .put("families/family")
            .then()
            .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void whenSavingFamilyWithLastNameIsNullShouldReturn400Status() {
        Family toSave = Family.builder()
                              .email(EMAIL)
                              .firstName(FIRST_NAME)
                              .areAttending(true)
                              .numberAttending(4)
                              .build();

        given()
            .contentType(ContentType.JSON)
            .body(toSave)
            .when()
            .put("families/family")
            .then()
            .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void whenSavingFamilyWithLastNameIsEmptyShouldReturn400Status() {
        Family toSave = Family.builder()
                              .email(EMAIL)
                              .firstName(FIRST_NAME)
                              .lastName("")
                              .areAttending(true)
                              .numberAttending(4)
                              .build();

        given()
            .contentType(ContentType.JSON)
            .body(toSave)
            .when()
            .put("families/family")
            .then()
            .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void whenSavingFamilyWithIsAttendingFieldIsNullShouldReturn400Status() {
        Family toSave = Family.builder()
                              .email(EMAIL)
                              .firstName(FIRST_NAME)
                              .lastName("")
                              .numberAttending(4)
                              .build();

        given()
            .contentType(ContentType.JSON)
            .body(toSave)
        .when()
            .put("families/family")
         .then()
            .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void whenSavingFamilyWithIsNumberOfPeopleAttendingFieldIsNullShouldReturn400Status() {
        Family toSave = Family.builder()
                              .email(EMAIL)
                              .firstName(FIRST_NAME)
                              .lastName("")
                              .areAttending(true)
                              .build();

        given()
            .contentType(ContentType.JSON)
            .body(toSave)
        .when()
            .put("families/family")
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.value());
    }
}
