package org.talon.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
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
public class IndexVlog {
    private String vlogId;

    @NotBlank
    private String vlogerId;

    private String vlogerFace;

    private String blogerName;

    @NotBlank
    @URL
    private String url;

    @URL
    private String cover;

    private String content;

    private Integer width;

    private Integer height;

    private Integer likeCounts;

    private Integer commentsCounts;

    private Integer isPrivate;

    private Boolean isPlay = false;

    private Boolean doIFollowVloger = false;

    private Boolean doILikeThisVlog = false;

}
