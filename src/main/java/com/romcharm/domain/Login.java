package com.romcharm.domain;

import io.swagger.annotations.ApiModelProperty;
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
public class Login {
    @NotNull
    @NotEmpty
    @ApiModelProperty(required = true, notes = "User username")
    private String username;
    @NotNull
    @NotEmpty
    @ApiModelProperty(required = true, notes = "User password")
    private String password;
}
