package com.romcharm.domain;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@DynamoDBTable(tableName = "romcharm-families")
public class Family {

    @NotNull
    @NotBlank
    @DynamoDBHashKey(attributeName = "email")
    private String email;

    @NotNull
    @NotBlank
    @DynamoDBAttribute(attributeName = "firstName")
    private String firstName;

    @NotNull
    @NotBlank
    @DynamoDBAttribute(attributeName = "lastName")
    private String lastName;

    @NotNull
    @DynamoDBAttribute(attributeName = "areAttending")
    private Boolean areAttending;

    @NotNull
    @DynamoDBAttribute(attributeName = "numberAttending")
    private Integer numberAttending;
}
