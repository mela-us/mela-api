package com.hcmus.mela.exercise.model;

import com.hcmus.mela.shared.type.ContentStatus;
import com.hcmus.mela.shared.utils.ProjectConstants;
import jakarta.persistence.PrePersist;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "exercises")
public class Exercise {

    @Id
    @Field(name = "_id")
    private UUID exerciseId;

    @Field(name = "lecture_id")
    private UUID lectureId;

    @Field(name = "name")
    private String exerciseName;

    @Field(name = "ordinal_number")
    private Integer ordinalNumber;

    @Field(name = "questions")
    private List<Question> questions;

    @Field(name = "status")
    private ContentStatus status;

    @Field(name = "created_by")
    private UUID createdBy;

    @Field(name = "rejected_reason")
    private String rejectedReason;

    @PrePersist
    public void prePersist() {
        if (this.status == null) {
            this.status = ContentStatus.PENDING;
        }
        if (this.status == ContentStatus.DENIED && (this.rejectedReason == null || this.rejectedReason.isEmpty())) {
            this.rejectedReason = "Liên hệ với quản trị viên để biết thêm chi tiết.";
        }
    }
}
