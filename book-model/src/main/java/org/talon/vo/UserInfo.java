package org.talon.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Id;
import java.util.Date;

/**
 * Created by Zelong
 * On 2022/10/30
 **/

@ToString
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfo {
    @Id
    private String id;

    private String mobile;

    private String nickname;

    @Column(name = "imooc_num")
    private String imoocNum;

    private String face;

    private Integer sex;

    private Date birthday;

    private String country;

    private String province;

    private String city;

    private String district;

    private String description;

    @Column(name = "bg_img")
    private String bgImg;

    @Column(name = "can_imooc_num_be_updated")
    private Integer canImoocNumBeUpdated;

    @Column(name = "created_time")
    private Date createdTime;

    @Column(name = "updated_time")
    private Date updatedTime;

    private String userToken;

    private Long myFollowsCounts;
    private Long myFansCounts;
    private Long myLikedVlogCounts;
    private Long totalLikeMeCounts;
}
