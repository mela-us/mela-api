package com.hcmus.mela.lecture.model;

public enum SectionType {
    PDF, TEXT;

    public static SectionType fromString(String type) {
        for (SectionType sectionType : SectionType.values()) {
            if (sectionType.name().equalsIgnoreCase(type)) {
                return sectionType;
            }
        }
        throw new IllegalArgumentException("Unknown section type: " + type);
    }
}
