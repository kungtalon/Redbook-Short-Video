package org.talon.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Created by Zelong
 * On 2022/11/15
 **/

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class VloggerInfo {
    private String vlogerId;
    private String nickname;
    private String face;
    private Boolean followed = true;
}
