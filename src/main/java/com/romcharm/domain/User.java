package com.romcharm.domain;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamoDBTable(tableName = "romcharm-userRoles")
public class User {

    @NotNull
    @NotEmpty
    @DynamoDBHashKey(attributeName = "username")
    private String username;

    @NotNull
    @NotEmpty
    @DynamoDBAttribute(attributeName = "password")
    private String password;

    @NotNull
    @NotEmpty
    @DynamoDBAttribute(attributeName = "role")
    private String role;
}
