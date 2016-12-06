package com.romcharm.it;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.romcharm.Application;
import com.romcharm.domain.Family;
import com.romcharm.repositories.FamiliesRespository;
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
public class FamiliesControllerIT {

    @Value("${local.server.port}")
    private int port;

    @Autowired
    private FamiliesRespository familiesRespository;

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
                                      .familyName(foundName)
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
        String familyName = "toBeSaved";
        Family initialFamily = Family.builder().familyName(familyName).areAttending(false).build();
        familiesRespository.save(initialFamily);

        Family toSave = Family.builder().familyName(familyName).areAttending(true).numberAttending(4).build();

        given()
            .contentType(ContentType.JSON)
            .body(toSave)
        .when()
            .put("families/family")
        .then()
            .statusCode(HttpStatus.CREATED.value());

        Family result = familiesRespository.findOne(familyName);

        assertThat(result, is(toSave));
    }

    @Test
    public void whenSavingFamilyWithFamilyNameNullShouldReturn400Status() {
        Family toSave = Family.builder().areAttending(false).numberAttending(5).build();

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
        Family toSave = Family.builder().familyName("").areAttending(false).numberAttending(5).build();

        given()
            .contentType(ContentType.JSON)
            .body(toSave)
        .when()
            .put("families/family")
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void whenSavingFamilyWithFamilyNameIsBlankShouldReturn400Status() {
        Family toSave = Family.builder().familyName(" ").areAttending(false).numberAttending(5).build();

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
        Family toSave = Family.builder().familyName("test").numberAttending(5).build();

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
        Family toSave = Family.builder().familyName("test").areAttending(true).build();

        given()
            .contentType(ContentType.JSON)
            .body(toSave)
        .when()
            .put("families/family")
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.value());
    }
}
