package com.romcharm.repositories;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList;
import com.romcharm.domain.mypage.Project;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Romesh Selvan
 */
class ProjectsRepository implements Repository<Project> {

    private final DynamoDBMapper dynamoDBMapper;

    ProjectsRepository(DynamoDBMapper mapper) {
        dynamoDBMapper = mapper;
    }

    /**
     * Get a list of projects sorted in order
     *
     * @return An ordered list of projects
     */
    @Override
    public List<Project> getProjects() {
        DynamoDBScanExpression expression = new DynamoDBScanExpression();
        PaginatedScanList<Project> list = getDynamoDBMapper().scan(getClassType(), expression);
        // Forcing entire list to be loaded by iterating over the list. This is because AWS uses Lazy loading when returning
        // a scan table result.
        return list.stream().sorted(Comparator.comparingInt(Project::getOrder)).collect(Collectors.toList());
    }

    @Override
    public Class<Project> getClassType() {
        return Project.class;
    }

    @Override
    public DynamoDBMapper getDynamoDBMapper() {
        return dynamoDBMapper;
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
     *        to the left. If the order number is greater than the number of projects then just place it at the back of the list,
     *        while shifting all the project, from the existing position to the last position, one position to the left
     *      - If the project is being moved up in the list, then shift projects between its current position and new position,
     *        to the right
     *      - If none of the above conditions are met then just do nothing and save it in the same position
     *- If the project is new do the following :
     *      - If the project order has not been set or it is beyond the current project list size then add it to the back
     *      - If it is to replace another project in the order, then simply shift all the projects from the position specified
     *        to the right
     *
     * @param project The project to be saved
     * @return The project that has been saved
     */
    @Override
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
            processExistingProject(project, alreadyExistingProject.get(), currentOrderedProjects);
        // If the project is new and has no order set or the order set is higher than the project list total, just add it to the end.
        } else if(isProjectOrderValid(project, currentOrderedProjects)) {
            int lastOrder = currentOrderedProjects.get(currentOrderedProjects.size() - 1).getOrder();
            project.setOrder(lastOrder + 1);
        // Otherwise if the project is new move all the projects between the position defined and the end of the list
        // one position to the right, increasing the size of the list by one.
        } else {
            saveNewOrder(currentOrderedProjects, project.getOrder() - 1, currentOrderedProjects.size(), true);
        }
        getDynamoDBMapper().save(project);
        return project;
    }

    private void processExistingProject(Project projectToSave,
                                        Project alreadyExistingProject,
                                        List<Project> currentOrderedProjects) {
        // Project has not order defined, then set it to the same as before
        if(projectToSave.getOrder() <= 0) {
            projectToSave.setOrder(alreadyExistingProject.getOrder());
        }
        // Project moved further down the list - then move object that are between its old and new position including the
        // object to be replaced one position to the left. If the project has a order defined which is higher than the total
        // list size then just place it at the end of the list instead, while shifting rest one position to the left.
        else if(alreadyExistingProject.getOrder() < projectToSave.getOrder()) {
            if(projectToSave.getOrder() > currentOrderedProjects.size()) {
                projectToSave.setOrder(currentOrderedProjects.size());
            }
            saveNewOrder(currentOrderedProjects, alreadyExistingProject.getOrder(), projectToSave.getOrder(), false);
            // If Project move up towards the beginning of the list, then move objects that are between its old and position, including
            // the object to be replaced one position to the right.
        } else if(alreadyExistingProject.getOrder() > projectToSave.getOrder()){
            saveNewOrder(currentOrderedProjects, projectToSave.getOrder() - 1, alreadyExistingProject.getOrder(), true);
        }
    }

    private boolean isProjectOrderValid(Project project, List<Project> currentOrderedProjects) {
        return project.getOrder() <= 0 || currentOrderedProjects.size() < project.getOrder();
    }

    /**
     * Save every item from a sublist with it's new order number
     * @param projectList The project list to get the sublist from
     * @param sublistInclusiveStart The start of the sublist - the object in this position will also be affected
     * @param sublistExclusiveEnd The end of the sublist - excluding the object in this position/array end
     * @param increment whether to increment or decrement
     */
    private void saveNewOrder(List<Project> projectList,
                              int sublistInclusiveStart,
                              int sublistExclusiveEnd,
                              boolean increment) {
        projectList.subList(sublistInclusiveStart, sublistExclusiveEnd)
                  .forEach(currProject -> {
                      if(increment) {
                          currProject.setOrder(currProject.getOrder() + 1);
                      } else {
                          currProject.setOrder(currProject.getOrder() - 1);
                      }
                      getDynamoDBMapper().save(currProject);
                  });
    }
}
