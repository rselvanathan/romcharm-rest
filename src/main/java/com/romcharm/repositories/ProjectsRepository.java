package com.romcharm.repositories;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList;
import com.romcharm.domain.mypage.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
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
     * Save a project.
     *
     * The logic is as follows :
     *
     * - If the current project list is empty then add the project as the first in order
     * - If the project already exists then do the following :
     *      - If the project order has not been set, save it at the same position
     *      - If the project is being moved further down, then shift projects between its current position and new position,
     *        to the left. If the order number is greater than the number of projects then just add it to the back, while
     *        still shifting the projects across.
     *      - If the project is being moved up in the list, then shift projects between its current position and new position,
     *        to the right
     *      - If none of the above conditions are met then just do nothing and save it in the same position
     *- If the project is new do the following :
     *      - If the project order has not been set or it is beyond the current project list size then add it to the back
     *      - If it is to replace another project in the order, then simply shift all the projects from the position specified
     *        one to the right
     * @param project The project to be saved
     * @return The project that has been saved
     */
    public Project save(Project project) {
        List<Project> currentOrderedProjects = getProjects();
        Optional<Project> alreadyExistingProject = currentOrderedProjects.stream()
                                                                         .filter(p -> p.getProjectId().equals(project.getProjectId()))
                                                                         .findFirst();
        // Save the project as the first if the list is empty
        if(currentOrderedProjects.isEmpty()) {
            project.setOrder(1);
        }
        // If the project already exists
        else if(alreadyExistingProject.isPresent()) {
            // Project has not order defined, then set it to the same as before
            if(project.getOrder() <= 0) {
                project.setOrder(alreadyExistingProject.get().getOrder());
            }
            // Project moved further down the list - then move object that are between its old and new position including the
            // object to be replaced one position to the left. If the project has a order defined which is higher than the total
            // list size then just place it at the end of the list instead, while shifting rest one position to the left.
            else if(alreadyExistingProject.get().getOrder() < project.getOrder()) {
                if(project.getOrder() > currentOrderedProjects.size()) {
                    project.setOrder(currentOrderedProjects.size());
                }
                saveNewOrder(currentOrderedProjects, alreadyExistingProject.get().getOrder(), project.getOrder(), false);
            // If Project move up towards the beginning of the list, then move objects that are between its old and position, including
            // the object to be replaced one position to the right.
            } else if(alreadyExistingProject.get().getOrder() > project.getOrder()){
                saveNewOrder(currentOrderedProjects, project.getOrder() - 1, alreadyExistingProject.get().getOrder(), true);
            }
        // If the project is new and has no order set or the order set is higher than the project list total, just add it to the end.
        } else if(project.getOrder() <= 0 || currentOrderedProjects.size() < project.getOrder()) {
            int lastOrder = currentOrderedProjects.get(currentOrderedProjects.size() - 1).getOrder();
            project.setOrder(lastOrder + 1);
        // Otherwise if the project is new move all the projects between the position defined and the end of the list one position to the right,
        // increasing the size of the list by one.
        } else {
            saveNewOrder(currentOrderedProjects, project.getOrder() - 1, currentOrderedProjects.size(), true);
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

    /**
     * Save every item from a sublist with it's new order number
     * @param projectList The project list to get the sublist from
     * @param sublistInclusiveStart The start of the sublist - the object in this position will also be affected
     * @param sublistExclusiveEnd The end of the sublist - excluding the object in this position/array end
     * @param increment whether to increment or decrement
     */
    private void saveNewOrder(List<Project> projectList, int sublistInclusiveStart, int sublistExclusiveEnd, boolean increment) {
        projectList.subList(sublistInclusiveStart, sublistExclusiveEnd)
                  .forEach(currProject -> {
                      if(increment) {
                          currProject.setOrder(currProject.getOrder() + 1);
                      } else {
                          currProject.setOrder(currProject.getOrder() - 1);
                      }
                      dynamoDBMapper.save(currProject);
                  });
    }
}
