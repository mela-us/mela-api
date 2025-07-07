package com.hcmus.mela.shared.utils;

import java.util.Locale;
import java.util.UUID;

public final class ProjectConstants {

    public static final String DEFAULT_ENCODING = "UTF-8";

    public static final String CONTENT_DATABASE_NAME = "mela";

    public static final Locale US_LOCALE = new Locale.Builder().setLanguage("en").setRegion("US").build();

    public static final Double EXERCISE_PASS_SCORE = 80.0;

    public static final UUID ADMIN_ID = UUID.fromString("00000000-0000-0000-0000-000000000000");

    private ProjectConstants() {

        throw new UnsupportedOperationException();
    }

}
