package com.hcmus.mela.history.service;

import com.hcmus.mela.history.dto.dto.LectureHistoryDto;
import com.hcmus.mela.history.dto.request.SaveSectionRequest;
import com.hcmus.mela.history.dto.response.SaveSectionResponse;
import com.hcmus.mela.history.exception.HistoryException;
import com.hcmus.mela.history.mapper.LectureHistoryMapper;
import com.hcmus.mela.history.model.LectureCompletedSection;
import com.hcmus.mela.history.model.LectureHistory;
import com.hcmus.mela.history.repository.LectureHistoryRepository;
import com.hcmus.mela.lecture.dto.dto.LectureDto;
import com.hcmus.mela.lecture.service.LectureInfoService;
import com.hcmus.mela.shared.type.ContentStatus;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@AllArgsConstructor
@Slf4j
@Service
public class LectureHistoryServiceImpl implements LectureHistoryService {

    private final LectureHistoryRepository lectureHistoryRepository;
    private final LectureInfoService lectureInfoService;

    @Override
    public SaveSectionResponse saveSection(UUID userId, SaveSectionRequest saveSectionRequest) {
        LectureHistory lectureHistory = lectureHistoryRepository.findByLectureIdAndUserId(saveSectionRequest.getLectureId(), userId);
        LectureDto lectureInfo = lectureInfoService.findLectureByLectureIdAndStatus(
                saveSectionRequest.getLectureId(), ContentStatus.VERIFIED);
        if (lectureInfo == null) {
            throw new HistoryException("Lecture not found for id " + saveSectionRequest.getLectureId());
        }

        boolean isUpdated = (lectureHistory != null && lectureHistory.getCompletedSections() != null);
        boolean isCompleted = false;
        if (!isUpdated) {
            lectureHistory = new LectureHistory();
            lectureHistory.setStartedAt(saveSectionRequest.getCompletedAt());
        } else {
            isCompleted = lectureHistory.getProgress().equals(100);
        }

        List<LectureCompletedSection> completedSections = updateCompletedSections(lectureHistory, saveSectionRequest);

        Integer progress = calculateProgress(completedSections, lectureInfo);

        if (progress.equals(100) && !isCompleted) {
            lectureHistory.setCompletedAt(saveSectionRequest.getCompletedAt());
        }

        lectureHistory.setId(isUpdated ? lectureHistory.getId() : UUID.randomUUID());
        lectureHistory.setUserId(userId);
        lectureHistory.setLectureId(saveSectionRequest.getLectureId());
        lectureHistory.setTopicId(lectureInfo.getTopicId());
        lectureHistory.setLevelId(lectureInfo.getLevelId());
        lectureHistory.setProgress(progress);
        lectureHistory.setCompletedSections(completedSections);

        if (isUpdated) {
            lectureHistoryRepository.updateFirstById(lectureHistory.getId(), lectureHistory);
        } else {
            lectureHistoryRepository.save(lectureHistory);
        }
        log.info("Lecture section saved successfully for user {}, lecture {}, progress {}",
                userId, saveSectionRequest.getLectureId(), progress);
        return new SaveSectionResponse("Lecture section saved successfully for user: " + userId);
    }

    private List<LectureCompletedSection> updateCompletedSections(LectureHistory lectureHistory, SaveSectionRequest request) {
        List<LectureCompletedSection> completedSections = Optional
                .ofNullable(lectureHistory.getCompletedSections())
                .orElse(new ArrayList<>());

        LectureCompletedSection lectureCompletedSection = completedSections
                .stream()
                .filter(section -> section.getOrdinalNumber().equals(request.getOrdinalNumber()))
                .findFirst().orElse(null);

        if (lectureCompletedSection == null) {
            lectureCompletedSection = new LectureCompletedSection();
            lectureCompletedSection.setOrdinalNumber(request.getOrdinalNumber());
            completedSections.add(lectureCompletedSection);
        }

        lectureCompletedSection.setCompletedAt(request.getCompletedAt());
        completedSections.sort(Comparator.comparing(LectureCompletedSection::getCompletedAt).reversed());
        return completedSections;
    }

    private Integer calculateProgress(List<LectureCompletedSection> completedSections, LectureDto lectureInfo) {
        int completedSectionsCount = completedSections.size();
        int sectionsCount = lectureInfo.getSections().size();
        return (int) (completedSectionsCount * 1.0 / sectionsCount * 100);
    }

    @Override
    public List<LectureHistoryDto> getLectureHistoryByUserAndLevel(UUID userId, UUID levelId) {
        List<LectureHistory> lectureHistories = lectureHistoryRepository.findAllByUserIdAndLevelId(userId, levelId);
        if (lectureHistories == null || lectureHistories.isEmpty()) {
            return new ArrayList<>();
        }
        return lectureHistories.stream().map(LectureHistoryMapper.INSTANCE::lectureHistoryToLectureHistoryDto).toList();
    }

    @Override
    public List<LectureHistory> getBestProgressHistoriesGroupedByLecture(UUID userId) {
        List<LectureHistory> lectureHistories = lectureHistoryRepository.findBestProgressHistoriesGroupedByLecture(userId);
        if (lectureHistories == null || lectureHistories.isEmpty()) {
            return new ArrayList<>();
        }
        return lectureHistories;
    }
}