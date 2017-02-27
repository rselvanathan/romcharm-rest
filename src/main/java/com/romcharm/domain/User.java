package com.romcharm.domain;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@DynamoDBTable(tableName = "romcharm-userRoles")
public class User {

    @NotNull
    @NotEmpty
    @DynamoDBHashKey(attributeName = "username")
    @ApiModelProperty(required = true, notes = "User's username")
    private String username;

    @NotNull
    @NotEmpty
    @DynamoDBAttribute(attributeName = "password")
    @ApiModelProperty(required = true, notes = "User's password")
    private String password;

    @NotNull
    @NotEmpty
    @DynamoDBAttribute(attributeName = "role")
    @ApiModelProperty(required = true, notes = "The Role of the user", dataType = "string",
        allowableValues = "ROLE_ROMCHARM_APP, ROLE_MYPAGE_APP, ROLE_ADMIN")
    private String role;
}
