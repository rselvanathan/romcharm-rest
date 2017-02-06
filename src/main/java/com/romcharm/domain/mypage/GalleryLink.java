package com.romcharm.domain.mypage;

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
    private String url;
}
