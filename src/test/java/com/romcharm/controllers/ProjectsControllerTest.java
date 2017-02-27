package com.romcharm.controllers;

import com.romcharm.domain.mypage.Project;
import com.romcharm.exceptions.NotFoundException;
import com.romcharm.repositories.Repository;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

/**
 * @author Romesh Selvan
 */
@RunWith(MockitoJUnitRunner.class)
public class ProjectsControllerTest {

    private static final String PROJECT_NAME = "projectName";

    private static final Project EXPECTED_PROJECT = new Project(PROJECT_NAME, null, null, null, null, null, null, null, 0);

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Mock
    private Repository<Project> projectsRepository;

    @InjectMocks
    private ProjectsController projectsController;

    @Test
    public void whenGettingAProjectThatDoesNotExistExpectNotFoundException() {
        exception.expect(NotFoundException.class);
        projectsController.getProject(PROJECT_NAME);
    }

    @Test
    public void whenGettingAProjectAndExistsReturnIt() {
        when(projectsRepository.findOne(PROJECT_NAME)).thenReturn(EXPECTED_PROJECT);
        Project result = projectsController.getProject(PROJECT_NAME);
        assertThat(result, is(EXPECTED_PROJECT));
    }

    @Test
    public void whenSavingAProjectThenCallSaveWithTheObjectPassedIn() {
        when(projectsRepository.save(EXPECTED_PROJECT)).thenReturn(EXPECTED_PROJECT);
        Project result = projectsController.save(EXPECTED_PROJECT);
        assertThat(result, is(EXPECTED_PROJECT));
    }

    @Test
    public void whenGettingAllProjectsExpectAListOfProjects() {
        Project secondProject = new Project("anotherProject", null, null, null, null, null, null, null, 0);

        List<Project> projects = Arrays.asList(EXPECTED_PROJECT, secondProject);
        when(projectsRepository.getProjects()).thenReturn(projects);
        List<Project> result = projectsController.getProjects();
        assertThat(result, is(projects));
    }
}
