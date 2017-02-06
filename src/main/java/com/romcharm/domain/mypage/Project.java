package com.romcharm.domain.mypage;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.romcharm.defaults.ProjectButtonTypes;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author Romesh Selvan
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@DynamoDBTable(tableName = "mypage-projects")
public class Project {

    @DynamoDBHashKey(attributeName = "projectId")
    @NotNull
    @NotEmpty
    private String projectId;

    @DynamoDBAttribute(attributeName = "projectTitle")
    @NotNull
    @NotEmpty
    private String projectTitle;

    @DynamoDBAttribute(attributeName = "titleImageLink")
    private String titleImageLink;

    @DynamoDBAttribute(attributeName = "buttonTypes")
    private List<ProjectButtonTypes> buttonTypes;

    @DynamoDBAttribute(attributeName = "githubLink")
    private String githubLink;

    @DynamoDBAttribute(attributeName = "videoLink")
    private String videoLink;

    @DynamoDBAttribute(attributeName = "galleryLinks")
    @Valid
    private List<GalleryLink> galleryLinks;
}
