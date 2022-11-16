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
public class FansInfo {
    private String fanId;
    private String nickname;
    private String face;
    private Boolean friend = false;
}
