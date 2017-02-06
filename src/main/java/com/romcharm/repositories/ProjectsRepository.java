package com.romcharm.repositories;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList;
import com.romcharm.domain.mypage.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

    public Project save(Project project) {
        dynamoDBMapper.save(project);
        return project;
    }

    public List<Project> getProjects() {
        DynamoDBScanExpression expression = new DynamoDBScanExpression();
        PaginatedScanList<Project> list = dynamoDBMapper.scan(Project.class, expression);
        // Forcing entire list to be loaded by iterating over the list. This is because AWS uses Lazy loading when returning
        // a scan table result.
        return list.stream().collect(Collectors.toList());
    }
}
