package com.hcmus.mela.topic.model;

import com.hcmus.mela.shared.type.ContentStatus;
import com.hcmus.mela.shared.utils.ProjectConstants;
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
@Document(collection = "topics")
public class Topic {

    @Id
    private UUID topicId;

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
            this.createdBy = ProjectConstants.ADMIN_ID;
        }
    }
}
