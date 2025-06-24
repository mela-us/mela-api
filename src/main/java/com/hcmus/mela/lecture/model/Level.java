package com.hcmus.mela.lecture.model;

import com.hcmus.mela.shared.type.ContentStatus;
import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "levels")
public class Level {

    @Id
    private UUID levelId;

    @Field("name")
    private String name;

    @Field("image_url")
    private String imageUrl;

    @Field(name = "status")
    private ContentStatus status;

    @Field(name = "created_by")
    private UUID createdBy;

    @Field(name= "rejected_reason")
    private String rejectedReason;

    @PrePersist
    public void prePersist() {
        if (this.status == null) {
            this.status = ContentStatus.PENDING;
        }
        if (this.createdBy == null) {
            this.createdBy = UUID.fromString("00000000-0000-0000-0000-000000000000");
        }
    }
}
