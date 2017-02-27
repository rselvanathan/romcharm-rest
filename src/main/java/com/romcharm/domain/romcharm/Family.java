package com.romcharm.domain.romcharm;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@DynamoDBTable(tableName = "romcharm-families")
public class Family {

    @NotNull
    @NotBlank
    @DynamoDBHashKey(attributeName = "email")
    @ApiModelProperty(required = true, notes = "The main e-mail of the family representative")
    private String email;

    @NotNull
    @NotBlank
    @DynamoDBAttribute(attributeName = "firstName")
    @ApiModelProperty(required = true, notes = "The first name  of the family representative")
    private String firstName;

    @NotNull
    @NotBlank
    @DynamoDBAttribute(attributeName = "lastName")
    @ApiModelProperty(required = true, notes = "The last name  of the family representative")
    private String lastName;

    @NotNull
    @DynamoDBAttribute(attributeName = "areAttending")
    @ApiModelProperty(required = true, notes = "Whether the family is attending")
    private Boolean areAttending;

    @NotNull
    @DynamoDBAttribute(attributeName = "numberAttending")
    @ApiModelProperty(required = true, notes = "How many of the family members are attending")
    private Integer numberAttending;
}
