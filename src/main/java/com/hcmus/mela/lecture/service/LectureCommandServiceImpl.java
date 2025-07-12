package com.hcmus.mela.lecture.service;

import com.hcmus.mela.lecture.dto.dto.LectureDto;
import com.hcmus.mela.lecture.dto.request.CreateLectureRequest;
import com.hcmus.mela.lecture.dto.request.UpdateLectureRequest;
import com.hcmus.mela.lecture.dto.response.CreateLectureResponse;
import com.hcmus.mela.lecture.mapper.LectureMapper;
import com.hcmus.mela.lecture.model.Lecture;
import com.hcmus.mela.lecture.strategy.LectureFilterStrategy;
import com.hcmus.mela.shared.type.ContentStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class LectureCommandServiceImpl implements LectureCommandService {

    @Override
    public CreateLectureResponse createLecture(LectureFilterStrategy strategy, UUID userId, CreateLectureRequest request) {
        Lecture lecture = LectureMapper.INSTANCE.createLectureRequestToLecture(request);
        LectureDto lectureDto = strategy.createLecture(userId, lecture);
        return new CreateLectureResponse("Create lecture successfully", lectureDto);
    }

    @Override
    public void updateLecture(LectureFilterStrategy strategy, UUID userId, UUID lectureId, UpdateLectureRequest request) {
        strategy.updateLecture(userId, lectureId, request);
    }

    @Override
    public void deleteLecture(LectureFilterStrategy strategy, UUID userId, UUID lectureId) {
        strategy.deleteLecture(userId, lectureId);
    }
}
