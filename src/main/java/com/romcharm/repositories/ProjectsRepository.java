package com.romcharm.repositories;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList;
import com.romcharm.domain.mypage.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Romesh Selvan
 */
@Component
public class ProjectsRepository {

    private final DynamoDBMapper dynamoDBMapper;

    @Autowired
    public ProjectsRepository(DynamoDBMapper mapper) {
        dynamoDBMapper = mapper;
    }

    public Project getProject(String projectId) {
        return dynamoDBMapper.load(Project.class, projectId);
    }

    /**
     * Save a project. If the project list is empty then add the project as the first in the order. If the project being
     * saved has no order set or the order set is higher than the current project list size then add the project to the
     * end of the ordered list. If the project is to replace another project in the ordering, then move all the projects on
     * the right of it one place further and place this new project in its new place.
     *
     * @param project The project to be saved
     * @return The project that has been saved
     */
    public Project save(Project project) {
        List<Project> currentOrderedProjects = getProjects();
        if(currentOrderedProjects.isEmpty()) {
            project.setOrder(1);
        } else if(project.getOrder() == 0 || currentOrderedProjects.size() < project.getOrder()) {
            int lastOrder = currentOrderedProjects.get(currentOrderedProjects.size() - 1).getOrder();
            project.setOrder(lastOrder + 1);
        } else {
            currentOrderedProjects.subList(project.getOrder()-1, currentOrderedProjects.size())
                                  .forEach(currProject ->  {
                                      currProject.setOrder(currProject.getOrder()+1);
                                      dynamoDBMapper.save(currProject);
                                  });
        }
        dynamoDBMapper.save(project);
        return project;
    }

    /**
     * Get a list of projects sorted in order
     *
     * @return An ordered list of projects
     */
    public List<Project> getProjects() {
        DynamoDBScanExpression expression = new DynamoDBScanExpression();
        PaginatedScanList<Project> list = dynamoDBMapper.scan(Project.class, expression);
        // Forcing entire list to be loaded by iterating over the list. This is because AWS uses Lazy loading when returning
        // a scan table result.
        return list.stream().sorted(Comparator.comparingInt(Project::getOrder)).collect(Collectors.toList());
    }
}
