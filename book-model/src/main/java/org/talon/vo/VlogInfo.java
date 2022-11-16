package org.talon.vo;

import lombok.*;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotBlank;


/**
 * Created by Zelong
 * On 2022/11/13
 **/

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class VlogInfo {
    private String id;

    @NotBlank
    private String vlogerId;

    @NotBlank
    @URL
    private String url;

    @URL
    private String cover;

    private String title;

    private Integer width;

    private Integer height;

    private Integer likeCounts;

    private Integer commentsCounts;

}
