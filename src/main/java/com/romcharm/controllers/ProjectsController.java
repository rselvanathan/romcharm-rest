package com.romcharm.controllers;

import com.romcharm.defaults.APIErrorCode;
import com.romcharm.domain.mypage.Project;
import com.romcharm.exceptions.NotFoundException;
import com.romcharm.repositories.ProjectsRepository;
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
    public Project getProject(@PathVariable(value = "projectId") String projectId) {
        Project project = projectsRepository.getProject(projectId);
        if(project == null) {
            throw new NotFoundException(APIErrorCode.PROJECT_NOT_FOUND);
        }
        return project;
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public Project save(@RequestBody @Valid Project project) {
        return projectsRepository.save(project);
    }

    @RequestMapping(value = "", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public List<Project> getProjects() {
        return projectsRepository.getProjects();
    }
}
