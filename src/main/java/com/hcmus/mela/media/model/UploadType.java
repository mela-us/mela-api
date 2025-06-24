package com.hcmus.mela.media.model;

import lombok.Getter;

@Getter
public enum UploadType {
    EXERCISE("exercise/"),
    LECTURE("lecture/"),
    TOPIC("topic/"),
    LEVEL("level/"),
    COMMON("common/"),
    USER_AVATAR("user/avatar/");

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
