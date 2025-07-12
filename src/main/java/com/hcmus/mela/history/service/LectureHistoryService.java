package com.hcmus.mela.history.service;

import com.hcmus.mela.history.dto.dto.LectureHistoryDto;
import com.hcmus.mela.history.dto.request.SaveSectionRequest;
import com.hcmus.mela.history.dto.response.SaveSectionResponse;
import com.hcmus.mela.history.model.LectureHistory;

import java.util.List;
import java.util.UUID;

public interface LectureHistoryService {

    SaveSectionResponse saveSection(UUID userId, SaveSectionRequest request);

    List<LectureHistoryDto> getLectureHistoryByUserAndLevel(UUID userId, UUID levelId);

    List<LectureHistory> getBestProgressHistoriesGroupedByLecture(UUID userId);

    void deleteAllLectureHistoryByUserId(UUID userId);

    Integer countAccessedNumberByLectureId(UUID lectureId);
}