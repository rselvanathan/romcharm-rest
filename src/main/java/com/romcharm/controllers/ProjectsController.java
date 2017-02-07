package com.romcharm.controllers;

import com.romcharm.defaults.APIErrorCode;
import com.romcharm.domain.mypage.Project;
import com.romcharm.exceptions.NotFoundException;
import com.romcharm.repositories.ProjectsRepository;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * @author Romesh Selvan
 */
@RestController
@RequestMapping("/projects")
public class ProjectsController {

    private final ProjectsRepository projectsRepository;

    @Autowired
    public ProjectsController(ProjectsRepository repository) {
        projectsRepository = repository;
    }

    @RequestMapping(value = "/{projectId}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Success", response = Project.class),
        @ApiResponse(code = 404, message = "Project not found"),
    })
    public Project getProject(@PathVariable(value = "projectId") String projectId) {
        Project project = projectsRepository.getProject(projectId);
        if(project == null) {
            throw new NotFoundException(APIErrorCode.PROJECT_NOT_FOUND);
        }
        return project;
    }

    @ApiOperation(value = "Add a project", notes = "An admin only endpoint to add a project")
    @RequestMapping(value = "/add", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = "Created", response = Project.class),
        @ApiResponse(code = 400, message = "Bad Request - Invalid or Bad Data"),
    })
    public Project save(@RequestBody @Valid Project project) {
        return projectsRepository.save(project);
    }

    @RequestMapping(value = "", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Success", response = Project.class, responseContainer = "List"),
    })
    public List<Project> getProjects() {
        return projectsRepository.getProjects();
    }
}
