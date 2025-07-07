package com.hcmus.mela.user.model;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.PrePersist;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User {

    @Id
    @Field(name = "_id")
    private UUID userId; // Mongo stores _id as a String; you can use UUID.toString()

    @Field("username")
    private String username;

    @Field("password")
    private String password;

    @Field("full_name")
    private String fullname;

    @Field("image_url")
    private String imageUrl;

    @Field("created_at")
    private Date createdAt;

    @Field("updated_at")
    private Date updatedAt;

    @Field("birthday")
    private Date birthday;

    @Field("level_id")
    private UUID levelId;

    @Field("user_role")
    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    @PrePersist
    public void prePersist() {
        if (this.userId == null) {
            this.userId = UUID.randomUUID();
        }
        Instant nowInstant = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")).toInstant();
        Date now = Date.from(nowInstant);
        if (this.createdAt == null) {
            this.createdAt = now;
        }
        this.updatedAt = now;
        if (this.userRole == null) {
            this.userRole = UserRole.USER;
        }
    }
}
