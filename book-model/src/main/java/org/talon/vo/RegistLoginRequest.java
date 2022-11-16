package org.talon.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

/**
 * Created by Zelong
 * On 2022/10/30
 **/

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RegistLoginRequest {

    @NotBlank(message = "Email can not be blank!")
    @Length(min=3)
    private String email;
    @NotBlank(message = "verification code can not be blank!")
    private String verifyCode;

}
