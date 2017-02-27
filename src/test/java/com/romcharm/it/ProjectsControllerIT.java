package com.romcharm.it;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.parsing.Parser;
import com.jayway.restassured.response.Header;
import com.romcharm.authorization.JWTUtil;
import com.romcharm.defaults.ProjectButtonTypes;
import com.romcharm.defaults.Role;
import com.romcharm.domain.User;
import com.romcharm.domain.mypage.GalleryLink;
import com.romcharm.domain.mypage.Project;
import com.romcharm.repositories.Repository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@Profile("test")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(value = "classpath:config.properties")
public class ProjectsControllerIT {

    private static final String PROJECT_ID = "projectId";

    @Value("${local.server.port}")
    private int port;

    @Mock
    PaginatedScanList<Project> paginatedScanList;

    @MockBean
    private DynamoDBMapper dynamoDBMapper;

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private Repository<Project> userRepository;

    @Before
    public void setup() {
        RestAssured.port = port;
        RestAssured.defaultParser = Parser.JSON;
        Mockito.when(dynamoDBMapper.scan(Mockito.eq(Project.class), Mockito.any(DynamoDBScanExpression.class))).thenReturn(paginatedScanList);
    }

    @Test
    public void whenProjectIsNotFoundExpectA404() {
        given()
            .header(new Header("Authorization", getToken(Role.ROLE_MYPAGE_APP)))
        .when()
            .get("/projects/" + PROJECT_ID)
        .then()
            .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    public void whenProjectDoesExistReturnTheProject() {
        Project expectedProject = getDefaultProject(PROJECT_ID);

        Mockito.when(dynamoDBMapper.load(Project.class, PROJECT_ID)).thenReturn(expectedProject);

        Project result =
            given()
                .header(new Header("Authorization", getToken(Role.ROLE_MYPAGE_APP)))
            .when()
                .get("/projects/"+PROJECT_ID)
            .getBody()
                .as(Project.class);

        assertThat(result, is(expectedProject));
    }

    @Test
    public void whenProjectDoesExistButRoleIsNotAMyPageRoleExpectForbidden() throws JsonProcessingException {
        Project expectedProject = getDefaultProject(PROJECT_ID);
        Mockito.when(dynamoDBMapper.load(Project.class, PROJECT_ID)).thenReturn(expectedProject);

        given()
            .header(new Header("Authorization", getToken(Role.ROLE_ROMCHARM_APP)))
        .when()
            .get("/projects/"+PROJECT_ID)
        .then()
            .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    public void whenSavingProjectSaveToDynamoShouldBeCompleted() throws JsonProcessingException {
        Project expectedProject = getDefaultProject(PROJECT_ID);

        Mockito.when(paginatedScanList.stream()).thenReturn(Stream.empty());

        Project result =
            given()
                .contentType(ContentType.JSON)
                .header(new Header("Authorization", getToken(Role.ROLE_ADMIN)))
                .body(expectedProject)
            .when()
                .post("/projects/add")
            .getBody()
                .as(Project.class);

        Mockito.verify(dynamoDBMapper).save(expectedProject);
        assertThat(result, is(expectedProject));
    }

    @Test
    public void whenSavingProjectWithProjectIDNullExpect400() throws JsonProcessingException {
        Project expectedProject = getDefaultProject(null);

        given()
            .contentType(ContentType.JSON)
            .header(new Header("Authorization", getToken(Role.ROLE_ADMIN)))
            .body(expectedProject)
        .when()
            .post("/projects/add")
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.value());

        Mockito.verify(dynamoDBMapper, Mockito.never()).save(expectedProject);
    }

    @Test
    public void whenSavingProjectWithProjectIDEmptyExpect400() throws JsonProcessingException {
        Project expectedProject = getDefaultProject("");

        given()
            .contentType(ContentType.JSON)
            .header(new Header("Authorization", getToken(Role.ROLE_ADMIN)))
            .body(expectedProject)
        .when()
            .post("/projects/add")
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.value());

        Mockito.verify(dynamoDBMapper, Mockito.never()).save(expectedProject);
    }

    @Test
    public void whenSavingProjectWithEmptyGalleryLinkThenReturnCreatedHttpStatus() throws JsonProcessingException {
        Project expectedProject = getDefaultProjectWithGalleryLink(PROJECT_ID, Collections.emptyList());

        Mockito.when(paginatedScanList.stream()).thenReturn(Stream.empty());

        given()
            .contentType(ContentType.JSON)
            .header(new Header("Authorization", getToken(Role.ROLE_ADMIN)))
            .body(expectedProject)
        .when()
            .post("/projects/add")
        .then()
            .statusCode(HttpStatus.CREATED.value());

        Mockito.verify(dynamoDBMapper).save(expectedProject);
    }

    @Test
    public void whenSavingProjectWithNullGalleryLinkThenReturnCreatedHttpStatus() throws JsonProcessingException {
        Project expectedProject = getDefaultProjectWithGalleryLink(PROJECT_ID, null);

        Mockito.when(paginatedScanList.stream()).thenReturn(Stream.empty());

        given()
            .contentType(ContentType.JSON)
            .header(new Header("Authorization", getToken(Role.ROLE_ADMIN)))
            .body(expectedProject)
        .when()
            .post("/projects/add")
        .then()
            .statusCode(HttpStatus.CREATED.value());

        Mockito.verify(dynamoDBMapper).save(expectedProject);
    }

    @Test
    public void whenSavingProjectWithAGalleryLinkContainingNullObjectsThenReturn400HttpStatus() throws JsonProcessingException {
        Project expectedProject = getDefaultProjectWithGalleryLink(PROJECT_ID,
                                                                   Collections.singletonList(getGalleryLink(null)));

        given()
            .contentType(ContentType.JSON)
            .header(new Header("Authorization", getToken(Role.ROLE_ADMIN)))
            .body(expectedProject)
        .when()
            .post("/projects/add")
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.value());

        Mockito.verify(dynamoDBMapper, Mockito.never()).save(expectedProject);
    }

    @Test
    public void whenSavingProjectWithAGalleryLinkContainingEmptyObjectsThenReturn400HttpStatus() throws JsonProcessingException {
        Project expectedProject = getDefaultProjectWithGalleryLink(PROJECT_ID,
                                                                   Collections.singletonList(getGalleryLink("")));

        given()
            .contentType(ContentType.JSON)
            .header(new Header("Authorization", getToken(Role.ROLE_ADMIN)))
            .body(expectedProject)
        .when()
            .post("/projects/add")
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.value());

        Mockito.verify(dynamoDBMapper, Mockito.never()).save(expectedProject);
    }

    @Test
    public void whenSavingProjectWithANonAdminRoleExpectForbiddenAccess() throws JsonProcessingException {
        Project expectedProject = getDefaultProjectWithGalleryLink(PROJECT_ID,
                                                                   Collections.singletonList(getGalleryLink("")));

        given()
            .contentType(ContentType.JSON)
            .header(new Header("Authorization", getToken(Role.ROLE_MYPAGE_APP)))
            .body(expectedProject)
        .when()
            .post("/projects/add")
        .then()
            .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    public void whenRetrieveingAListOfProjectsReturnTheCorrectList() throws JsonProcessingException {
        List<Project> projectList = getDefaultProjectList();

        Mockito.when(paginatedScanList.stream()).thenReturn(projectList.stream());

        String result =
            given()
                .header(new Header("Authorization", getToken(Role.ROLE_MYPAGE_APP)))
            .when()
                .get("/projects")
            .getBody().asString();

        ObjectMapper objectMapper = new ObjectMapper();
        String expectedProjectListString = objectMapper.writeValueAsString(projectList);

        assertThat(result, is(expectedProjectListString));
    }

    @Test
    public void whenRetrieveingAListOfProjectsWithROMCHARMRoleThenExpect401() throws JsonProcessingException {
        given()
            .header(new Header("Authorization", getToken(Role.ROLE_ROMCHARM_APP)))
        .when()
            .get("/projects")
        .then()
            .statusCode(HttpStatus.FORBIDDEN.value());
    }

    private List<Project> getDefaultProjectList() {
        return Arrays.asList(getDefaultProject("projectId"), getDefaultProject("projectId2"));
    }

    private Project getDefaultProject(String projectId) {
        return getDefaultProjectWithGalleryLink(projectId,
                                                Arrays.asList(getGalleryLink("link1"),
                                                              getGalleryLink("link2")));
    }

    private Project getDefaultProjectWithGalleryLink(String projectId, List<GalleryLink> galleryLinks) {
        return new Project(projectId,
                           "projectTitle",
                           "imageLink",
                           Arrays.asList(ProjectButtonTypes.GALLERY, ProjectButtonTypes.GITHUB),
                           "github",
                           null,
                           galleryLinks,
                           "directLink",
                           1);
    }

    private String getToken(Role role) {
        User user = new User("user", "pass", role.getName());
        return jwtUtil.generateToken(user);
    }

    private GalleryLink getGalleryLink(String link) {
        return new GalleryLink(link);
    }
}
