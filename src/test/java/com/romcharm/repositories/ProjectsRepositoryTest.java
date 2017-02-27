package com.romcharm.repositories;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList;
import com.romcharm.domain.mypage.Project;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * @author Romesh Selvan
 */
@RunWith(MockitoJUnitRunner.class)
public class ProjectsRepositoryTest {

    @Mock
    private DynamoDBMapper dynamoDBMapper;

    @Mock
    private PaginatedScanList<Project> projectPaginatedScanList;

    @InjectMocks
    private ProjectsRepository projectsRepository;

    @Before
    public void setup() {
        when(dynamoDBMapper.scan(eq(Project.class), any(DynamoDBScanExpression.class)))
            .thenReturn(projectPaginatedScanList);
    }

    @Test
    public void whenProjectListIsEmptyReturnAnEmptyList() {
        when(projectPaginatedScanList.stream()).thenReturn(Stream.empty());
        List<Project> orderedList = projectsRepository.getProjects();
        assertThat(orderedList, is(empty()));
    }

    @Test
    public void whenProjectListIsContainsSingleProjectReturnAnSingleProjectList() {
        Project expectedProject = getProject("projectId", 1);
        when(projectPaginatedScanList.stream()).thenReturn(Stream.of(expectedProject));
        List<Project> orderedList = projectsRepository.getProjects();
        assertThat(orderedList, contains(expectedProject));
    }

    @Test
    public void whenProjectListIsContainsTwoItemsWithOrderValuesThenTheListReturnedShouldBeOrdered() {
        Project projectTwo = getProject("projectId", 2);
        Project projectOne = getProject("projectIdTwo", 1);
        when(projectPaginatedScanList.stream()).thenReturn(Stream.of(projectTwo, projectOne));
        List<Project> orderedList = projectsRepository.getProjects();
        assertThat(orderedList, contains(projectOne, projectTwo));
    }

    @Test
    public void whenSavingAProjectWithOrderNumberZeroAndTheProjectsRetrievedIsEmptyThenJustAddAsFirstInOrder() {
        when(projectPaginatedScanList.stream()).thenReturn(Stream.empty());
        String projectId = "newProject";
        projectsRepository.save(getProject(projectId, 0));
        verify(dynamoDBMapper).save(getProject(projectId, 1));
    }

    @Test
    public void whenSavingAProjectWithOrderNumber5AndTheProjectsRetrievedIsEmptyThenJustAddAsFirstInOrder() {
        when(projectPaginatedScanList.stream()).thenReturn(Stream.empty());
        String projectId = "newProject";
        projectsRepository.save(getProject(projectId, 5));
        verify(dynamoDBMapper).save(getProject(projectId, 1));
    }

    @Test
    public void whenSavingAProjectWithOrderNumberZeroAndTheProjectsRetrievedHasAnItemThenOrderShouldBeAfterThatObject() {
        when(projectPaginatedScanList.stream()).thenReturn(Stream.of(getProject("projectFirst", 1)));
        String projectId = "newProject";
        projectsRepository.save(getProject(projectId, 0));
        verify(dynamoDBMapper).save(getProject(projectId, 2));
    }

    @Test
    public void whenSavingAProjectWithOrderNumberBeyondLastAndTheProjectsRetrievedHasAnItemThenOrderShouldBeAfterThatObject() {
        when(projectPaginatedScanList.stream()).thenReturn(Stream.of(getProject("projectFirst", 1)));
        String projectId = "newProject";
        projectsRepository.save(getProject(projectId, 15));
        verify(dynamoDBMapper).save(getProject(projectId, 2));
    }

    @Test
    public void whenSavingANewProjectWithOrderNumberThatAlreadyExistsThenItShouldReplaceOrderOfPreviousWherePreviousProjectAndAllProjectsAfterWillMoveOneOrderToTheRight() {
        when(projectPaginatedScanList.stream()).thenReturn(Stream.of(getProject("projectFirst", 1),
                                                                     getProject("projectTwo", 2),
                                                                     getProject("projectThree", 3)));
        String projectId = "newProject";
        projectsRepository.save(getProject(projectId, 2));
        verify(dynamoDBMapper).save(getProject(projectId, 2));
        verify(dynamoDBMapper).save(getProject("projectTwo", 3));
        verify(dynamoDBMapper).save(getProject("projectThree", 4));
    }

    @Test
    public void whenSavingAOldProjectWithTheSameOrderNumberAsBeforeThenItShouldJustOverwriteThePreviousEntry() {
        when(projectPaginatedScanList.stream()).thenReturn(Stream.of(getProject("projectFirst", 1),
                                                                     getProject("projectTwo", 2),
                                                                     getProject("projectThree", 3)));
        String projectId = "projectTwo";
        projectsRepository.save( getProject(projectId, 2));
        verify(dynamoDBMapper).save(getProject(projectId, 2));
        verify(dynamoDBMapper, never()).save(getProject("projectThree", 4));
    }

    @Test
    public void whenSavingAOldProjectWithOrderNumberBeyondTheSizeOfListThenItShouldBeSetToTheEndOfTheList() {
        when(projectPaginatedScanList.stream()).thenReturn(Stream.of(getProject("projectFirst", 1),
                                                                     getProject("projectTwo", 2),
                                                                     getProject("projectThree", 3)));
        String projectId = "projectFirst";
        projectsRepository.save(getProject(projectId, 15));
        verify(dynamoDBMapper).save(getProject(projectId, 3));
        verify(dynamoDBMapper).save(getProject("projectTwo", 1));
        verify(dynamoDBMapper).save(getProject("projectThree", 2));
    }

    @Test
    public void whenSavingAOldProjectWithNoOrderSpecifiedThenItShouldJustOverwriteThePreviousEntry() {
        when(projectPaginatedScanList.stream()).thenReturn(Stream.of(getProject("projectFirst", 1),
                                                                     getProject("projectTwo", 2),
                                                                     getProject("projectThree", 3)));
        String projectId = "projectTwo";
        projectsRepository.save(getProject(projectId, 0));
        verify(dynamoDBMapper).save(getProject(projectId, 2));
        verify(dynamoDBMapper, never()).save(getProject("projectThree", 4));
    }

    @Test
    public void whenSavingAOldProjectWithANewOrderNumberThatIsHigherThanBeforeThenItShouldShiftEverythingToTheLeftOfItIncludingTheProjectInTheSameOrderNumberOnePositionToTheLeft() {
        when(projectPaginatedScanList.stream()).thenReturn(Stream.of(getProject("projectFirst", 1),
                                                                     getProject("projectTwo", 2),
                                                                     getProject("projectThree", 3),
                                                                     getProject("projectFour", 4),
                                                                     getProject("projectFive", 5),
                                                                     getProject("projectSix", 6)));
        String projectId = "projectTwo";
        int newPosition = 5;
        projectsRepository.save(getProject(projectId, newPosition));

        verify(dynamoDBMapper).save(getProject(projectId, newPosition));

        verify(dynamoDBMapper).save(getProject("projectThree", 2));
        verify(dynamoDBMapper).save(getProject("projectFour", 3));
        verify(dynamoDBMapper).save(getProject("projectFive", 4));

        verify(dynamoDBMapper, never()).save(getProject("projectSix", 5));
        verify(dynamoDBMapper, never()).save(getProject("projectSix", 7));
    }

    @Test
    public void whenSavingAOldProjectWithANewOrderNumberThatIsLowerThanBeforeThenItShouldShiftEverythingToTheRightOfItIncludingTheProjectInTheSameOrderNumberOnePositionToTheRight() {
        when(projectPaginatedScanList.stream()).thenReturn(Stream.of(getProject("projectFirst", 1),
                                                                     getProject("projectTwo", 2),
                                                                     getProject("projectThree", 3),
                                                                     getProject("projectFour", 4),
                                                                     getProject("projectFive", 5),
                                                                     getProject("projectSix", 6)));
        String projectId = "projectFive";
        int newPosition = 2;
        projectsRepository.save(getProject(projectId, newPosition));

        verify(dynamoDBMapper).save(getProject(projectId, newPosition));

        verify(dynamoDBMapper).save(getProject("projectTwo", 3));
        verify(dynamoDBMapper).save(getProject("projectThree", 4));
        verify(dynamoDBMapper).save(getProject("projectFour", 5));

        verify(dynamoDBMapper, never()).save(getProject("projectSix", 5));
        verify(dynamoDBMapper, never()).save(getProject("projectSix", 7));
    }

    private Project getProject(String projectId, int order) {
        return new Project(projectId, null, null, null, null, null, null, null, order);
    }
}
