package org.talon.vo;

import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Created by Zelong
 * On 2022/12/3
 **/

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CommentInfo {

    @NotBlank(message = "Incomplete fields for CommentInfo")
    private String vlogerId;

    @NotBlank(message = "Incomplete fields for CommentInfo")
    private String fatherCommentId;

    @NotBlank(message = "Incomplete fields for CommentInfo")
    private String vlogId;

    @NotBlank(message = "Invalid user! Please login again!")
    @NotBlank
    private String commentUserId;

    @NotBlank(message = "Comments can not be empty!")
    @Length(max = 100, message = "Comments can not exceed the limit of 50 words!")
    private String content;

    private Integer likeCounts;

}
