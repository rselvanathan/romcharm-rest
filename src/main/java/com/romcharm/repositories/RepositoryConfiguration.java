package com.romcharm.repositories;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.romcharm.domain.User;
import com.romcharm.domain.mypage.Project;
import com.romcharm.domain.romcharm.Family;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Romesh Selvan
 */
@Configuration
public class RepositoryConfiguration {

    private final DynamoDBMapper dynamoDBMapper;

    @Autowired
    public RepositoryConfiguration(@SuppressWarnings("SpringJavaAutowiringInspection") DynamoDBMapper mapper) {
        dynamoDBMapper = mapper;
    }

    @Bean
    public Repository<Project> projectRepository() {return new ProjectsRepository(dynamoDBMapper);}

    @Bean
    public Repository<Family> familyRepository() {return new FamiliesRepository(dynamoDBMapper);}

    @Bean
    public Repository<User> userRepository() {return new UserRepository(dynamoDBMapper);}
}
