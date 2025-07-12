package com.hcmus.mela.media.model;

import lombok.Getter;

@Getter
public enum UploadType {
    EXERCISE("exercises/"),
    LECTURE("lectures/"),
    TOPIC("topics/"),
    LEVEL("levels/"),
    TEST("tests/"),
    COMMON("common/"),
    USER_AVATAR("users/avatars/");

    private final String path;

    UploadType(String path) {
        this.path = path;
    }

    public static UploadType fromTypeName(String typeName) {
        for (UploadType type : UploadType.values()) {
            if (type.name().equalsIgnoreCase(typeName)) {
                return type;
            }
        }
        return UploadType.COMMON;
    }
}
