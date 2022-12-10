package org.talon.enums;

/**
 * Created by Zelong
 * On 2022/12/10
 **/

public enum CrudOperationEnum {
    QUERY(0, "query"),
    CREATE(1, "create"),
    UPDATE(2, "update"),
    DELETE(3, "delete");

    public final Integer type;
    public final String value;

    CrudOperationEnum(Integer type, String value) {
        this.type = type;
        this.value = value;
    }

    public static CrudOperationEnum lookup(String key) {
        for (CrudOperationEnum enumType : CrudOperationEnum.values()) {
            if (key.contains(enumType.value)) {
                return enumType;
            }
        }
        return null;
    }
}
