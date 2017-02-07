package com.romcharm.domain.mypage;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

/**
 * @author Romesh Selvan
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GalleryLink {
    @NotEmpty
    @NotNull
    @ApiModelProperty(required = true, notes = "URL link for the picture")
    private String url;
}
