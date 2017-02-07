package com.romcharm.domain.mypage;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.romcharm.defaults.ProjectButtonTypes;
import com.romcharm.util.dynamoDBConverters.GalleryLinkListConverter;
import com.romcharm.util.dynamoDBConverters.ProjectButtonTypeConverter;
import io.swagger.annotations.ApiModelProperty;
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
    @ApiModelProperty(required = true, notes = "The unique project Id")
    private String projectId;

    @DynamoDBAttribute(attributeName = "projectTitle")
    @NotNull
    @NotEmpty
    @ApiModelProperty(required = true, notes = "Title of the project to display")
    private String projectTitle;

    @DynamoDBAttribute(attributeName = "titleImageLink")
    @ApiModelProperty(notes = "Title image link")
    private String titleImageLink;

    @DynamoDBAttribute(attributeName = "buttonTypes")
    @DynamoDBTypeConverted(converter = ProjectButtonTypeConverter.class)
    @ApiModelProperty(notes = "List of project buttons displayed", allowableValues = "GITHUB, GALLERY, VIDEO")
    private List<ProjectButtonTypes> buttonTypes;

    @DynamoDBAttribute(attributeName = "githubLink")
    @ApiModelProperty(notes = "Link to the project github page")
    private String githubLink;

    @DynamoDBAttribute(attributeName = "videoLink")
    @ApiModelProperty(notes = "Link to the project video")
    private String videoLink;

    @DynamoDBAttribute(attributeName = "galleryLinks")
    @DynamoDBTypeConverted(converter = GalleryLinkListConverter.class)
    @Valid
    @ApiModelProperty(notes = "A list of links for the pictures")
    private List<GalleryLink> galleryLinks;
}
