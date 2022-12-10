package org.talon.enums;

/**
 * @Desc: 消息类型
 */
public enum MessageEnum {
    FOLLOW_YOU(1, "follow"),
    LIKE_VLOG(2, "like_vlog"),
    COMMENT_VLOG(3, "comment"),
    REPLY_YOU(4, "reply_comment"),
    LIKE_COMMENT(5, "like_comment");

    public final Integer type;
    public final String value;

    MessageEnum(Integer type, String value) {
        this.type = type;
        this.value = value;
    }
}
