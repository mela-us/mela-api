package com.hcmus.mela.lecture.service;

import com.hcmus.mela.lecture.exception.LectureException;
import com.hcmus.mela.lecture.model.Lecture;
import com.hcmus.mela.lecture.repository.LectureRepository;
import com.hcmus.mela.level.service.LevelStatusService;
import com.hcmus.mela.shared.type.ContentStatus;
import com.hcmus.mela.topic.service.TopicStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LectureStatusServiceImpl implements LectureStatusService {

    private final LectureRepository lectureRepository;
    private final TopicStatusService topicStatusService;
    private final LevelStatusService levelStatusService;

    @Override
    public void denyLecture(UUID lectureId, String reason) {
        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new LectureException("Lecture not found"));
        if (lecture.getStatus() == ContentStatus.VERIFIED || lecture.getStatus() == ContentStatus.DELETED) {
            throw new LectureException("Lecture cannot be denied");
        }
        lecture.setRejectedReason(reason);
        lecture.setStatus(ContentStatus.DENIED);
        lectureRepository.save(lecture);
    }

    @Override
    public void approveLecture(UUID lectureId) {
        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new LectureException("Lecture not found"));
        if (lecture.getStatus() == ContentStatus.DELETED) {
            throw new LectureException("Lecture cannot be approved");
        }
        if (!topicStatusService.isTopicInStatus(lecture.getTopicId(), ContentStatus.VERIFIED)) {
            throw new LectureException("Topic of lecture must be verified before approving lecture");
        }
        if (!levelStatusService.isLevelInStatus(lecture.getLevelId(), ContentStatus.VERIFIED)) {
            throw new LectureException("Level of lecture must be verified before approving lecture");
        }
        lecture.setRejectedReason(null);
        lecture.setStatus(ContentStatus.VERIFIED);
        lectureRepository.save(lecture);
    }

    @Override
    public boolean isLectureAssignableToExercise(UUID userId, UUID lectureId) {
        if (lectureId == null || userId == null) {
            return false;
        }
        Lecture lecture = lectureRepository.findById(lectureId).orElse(null);
        if (lecture == null) {
            return false;
        }
        if (lecture.getStatus() == ContentStatus.VERIFIED) {
            return true;
        }
        return lecture.getStatus() != ContentStatus.DELETED && lecture.getCreatedBy().equals(userId);
    }

    @Override
    public boolean isLectureInStatus(UUID lectureId, ContentStatus status) {
        if (lectureId == null || status == null) {
            return false;
        }
        Lecture lecture = lectureRepository.findById(lectureId).orElse(null);
        return lecture != null && lecture.getStatus() == status;
    }
}
