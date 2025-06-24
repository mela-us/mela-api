package com.hcmus.mela.shared.storage;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "azure.storage")
public class AzureStorageProperties {
    private String accountName;
    private String accountKey;
    private int expireTime;
    private Container container;

    @Setter
    @Getter
    public static class Container {
        private Users users;
        private Conversations conversations;
        private UserUpload userUpload;
        private AdminUpload adminUpload;
        private CommonUpload commonUpload;
    }

    @Setter
    @Getter
    public static class Users {
        private String name; // "users"
        private String images; // "images"
    }

    @Setter
    @Getter
    public static class Conversations {
        private String name; // "conversations"
        private String files; // "files"
    }

    @Setter
    @Getter
    public static class UserUpload {
        private String name; // "user-upload"
    }

    @Setter
    @Getter
    public static class AdminUpload {
        private String name; // "admin-upload"
    }

    @Setter
    @Getter
    public static class CommonUpload {
        private String name; // "mela"
    }
}
