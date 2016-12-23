package com.romcharm.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "UserRoles")
public class UserRole {

    @Id
    @NotNull
    @NotEmpty
    private String userAccessName;

    @NotNull
    @NotEmpty
    private String role;
}
