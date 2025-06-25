package com.hcmus.mela.lecture.service;

import com.hcmus.mela.lecture.dto.dto.LectureDto;
import com.hcmus.mela.lecture.dto.dto.LectureOfSectionDto;
import com.hcmus.mela.lecture.dto.dto.SectionDto;
import com.hcmus.mela.lecture.dto.request.CreateLectureRequest;
import com.hcmus.mela.lecture.dto.request.UpdateLectureRequest;
import com.hcmus.mela.lecture.dto.response.CreateLectureResponse;
import com.hcmus.mela.lecture.dto.response.GetLectureInfoResponse;
import com.hcmus.mela.lecture.dto.response.GetLectureSectionsResponse;
import com.hcmus.mela.lecture.exception.LectureException;
import com.hcmus.mela.lecture.mapper.LectureMapper;
import com.hcmus.mela.lecture.mapper.LectureSectionMapper;
import com.hcmus.mela.lecture.model.Lecture;
import com.hcmus.mela.lecture.repository.LectureRepository;
import com.hcmus.mela.lecture.strategy.LectureFilterStrategy;
import com.hcmus.mela.level.exception.LevelException;
import com.hcmus.mela.level.service.LevelService;
import com.hcmus.mela.shared.type.ContentStatus;
import com.hcmus.mela.shared.utils.GeneralMessageAccessor;
import com.hcmus.mela.topic.service.TopicService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LectureServiceImpl implements LectureService {

    private final GeneralMessageAccessor generalMessageAccessor;

    private final LectureRepository lectureRepository;

    private final TopicService topicService;

    private final LevelService levelService;

    @Override
    public CreateLectureResponse getCreateLectureResponse(UUID userId, CreateLectureRequest request) {
        Lecture lecture = LectureMapper.INSTANCE.createLectureRequestToLecture(request);
        lecture.setLectureId(UUID.randomUUID());
        lecture.setStatus(ContentStatus.PENDING);
        lecture.setCreatedBy(userId);
        Lecture savedLecture = lectureRepository.save(lecture);

        LectureDto lectureDto = LectureMapper.INSTANCE.lectureToLectureDto(savedLecture);

        return new CreateLectureResponse(
                "Create lecture successfully",
                lectureDto
        );
    }

    @Override
    public void updateLecture(LectureFilterStrategy strategy, UUID userId, UUID lectureId, UpdateLectureRequest request) {
        strategy.updateLecture(userId, lectureId, request);
    }

    @Override
    public void denyLecture(UUID lectureId, String reason) {
        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow(() -> new LectureException("Lecture not found"));
        if (lecture.getStatus() == ContentStatus.VERIFIED || lecture.getStatus() == ContentStatus.DELETED) {
            throw new LevelException("Lecture cannot be denied");
        }
        lecture.setRejectedReason(reason);
        lecture.setStatus(ContentStatus.DENIED);
        lectureRepository.save(lecture);
    }

    @Override
    public void approveLecture(UUID lectureId) {
        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow(() -> new LectureException("Lecture not found"));
        if (lecture.getStatus() == ContentStatus.DELETED) {
            throw new LevelException("Lecture cannot be approved");
        }
        if (!topicService.isTopicVerified(lecture.getTopicId())) {
            throw new LevelException("Topic of lecture must be verified before approving lecture");
        }
        if (!levelService.isLevelVerified(lecture.getLevelId())) {
            throw new LevelException("Level of lecture must be verified before approving lecture");
        }
        lecture.setRejectedReason(null);
        lecture.setStatus(ContentStatus.VERIFIED);
        lectureRepository.save(lecture);
    }

    @Override
    public boolean isLectureAssignableToExercise(UUID lectureId, UUID userId) {
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
    public boolean isLectureDeleted(UUID lectureId) {
        return checkLectureStatus(lectureId, ContentStatus.DELETED);
    }

    @Override
    public boolean isLectureVerified(UUID lectureId) {
        return checkLectureStatus(lectureId, ContentStatus.VERIFIED);
    }

    @Override
    public boolean checkLectureStatus(UUID lectureId, ContentStatus status) {
        if (lectureId == null || status == null) {
            return false;
        }
        Lecture lecture = lectureRepository.findById(lectureId).orElse(null);
        return lecture != null && lecture.getStatus() == status;
    }

    @Override
    public GetLectureInfoResponse getLectureInfoResponse(UUID lectureId) {
        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow(() -> new LectureException("Lecture not found"));
        LectureDto lectureDto = LectureMapper.INSTANCE.lectureToLectureDto(lecture);
        return new GetLectureInfoResponse("Get lecture info successfully", lectureDto);
    }

    @Override
    public LectureDto getLectureById(UUID lectureId) {
        Lecture lecture = lectureRepository.findByLectureId(lectureId);
        if (lecture == null) {
            return null;
        }
        return LectureMapper.INSTANCE.lectureToLectureDto(lecture);
    }

    @Override
    public Integer getLectureOrdinalNumber(UUID lectureId) {
        Lecture lecture = lectureRepository.findByLectureId(lectureId);

        if (lecture == null) {
            return -1;
        }

        return lecture.getOrdinalNumber();
    }

    @Override
    public GetLectureSectionsResponse getLectureSections(UUID lectureId) {
        Lecture lecture = lectureRepository.findByLectureId(lectureId);
        if (lecture == null) {
            return new GetLectureSectionsResponse(
                    generalMessageAccessor.getMessage(null, "get_sections_success"),
                    0,
                    null,
                    Collections.emptyList()
            );
        }
        LectureOfSectionDto lectureInfo = LectureMapper.INSTANCE.lectureToLectureOfSectionDto(lecture);
        if (lecture.getSections() == null) {
            return new GetLectureSectionsResponse(
                    generalMessageAccessor.getMessage(null, "get_sections_success"),
                    0,
                    lectureInfo,
                    Collections.emptyList()
            );
        }
        List<SectionDto> sectionDtoList = lecture.getSections().stream()
                .map(LectureSectionMapper.INSTANCE::sectionToSectionDto)
                .sorted(Comparator.comparingInt(SectionDto::getOrdinalNumber))
                .toList();

        return new GetLectureSectionsResponse(
                generalMessageAccessor.getMessage(null, "get_sections_success"),
                sectionDtoList.size(),
                lectureInfo,
                sectionDtoList
        );
    }

    @Override
    public LectureDto getLectureByTopicIdAndLevelIdAndOrdinalNumber(UUID topicId, UUID levelId, Integer ordinalNumber) {
        Lecture lecture = lectureRepository.findByTopicIdAndLevelIdAndOrdinalNumber(topicId, levelId, ordinalNumber);

        if (lecture == null) {
            return null;
        }

        return LectureMapper.INSTANCE.lectureToLectureDto(lecture);
    }
}
