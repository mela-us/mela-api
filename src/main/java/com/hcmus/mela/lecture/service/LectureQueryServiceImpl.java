package com.hcmus.mela.lecture.service;

import com.hcmus.mela.history.service.ExerciseHistoryService;
import com.hcmus.mela.lecture.dto.dto.*;
import com.hcmus.mela.lecture.dto.response.*;
import com.hcmus.mela.lecture.mapper.LectureMapper;
import com.hcmus.mela.lecture.mapper.LectureSectionMapper;
import com.hcmus.mela.lecture.model.Lecture;
import com.hcmus.mela.lecture.repository.LectureRepository;
import com.hcmus.mela.lecture.strategy.LectureFilterStrategy;
import com.hcmus.mela.shared.async.AsyncCustomService;
import com.hcmus.mela.shared.type.ContentStatus;
import com.hcmus.mela.topic.mapper.TopicMapper;
import com.hcmus.mela.topic.service.TopicInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class LectureQueryServiceImpl implements LectureQueryService {

    private final TopicInfoService topicInfoService;
    private final LectureRepository lectureRepository;
    private final ExerciseHistoryService exerciseHistoryService;
    private final AsyncCustomService asyncService;

    @Override
    public GetAllLecturesResponse getAllLectures(LectureFilterStrategy strategy, UUID userId) {
        List<LectureDto> lectures = strategy.getLectures(userId);
        if (lectures.isEmpty()) {
            return new GetAllLecturesResponse("No lectures found in the system", Collections.emptyList());
        }
        return new GetAllLecturesResponse("Get lectures success", lectures);
    }

    @Override
    public GetLectureInfoResponse getLectureInfoByLectureId(LectureFilterStrategy strategy, UUID userId, UUID lectureId) {
        LectureDto lectureDto = strategy.getLectureById(userId, lectureId);
        return new GetLectureInfoResponse("Get lecture info successfully", lectureDto);
    }

    @Override
    public GetLecturesByLevelResponse getLecturesByLevelId(UUID userId, UUID levelId) {
        CompletableFuture<Map<UUID, Integer>> passExerciseTotalsMapFuture = asyncService.runAsync(
                () -> exerciseHistoryService.getPassedExerciseCountOfUser(userId),
                Collections.emptyMap());
        CompletableFuture<List<LecturesByTopicDto>> lecturesByTopicDtoListFuture = asyncService.runAsync(
                () -> topicInfoService.findAllTopicsInStatus(ContentStatus.VERIFIED)
                        .stream()
                        .map(TopicMapper.INSTANCE::topicDtoToLecturesByTopicDto)
                        .toList(),
                Collections.emptyList());

        CompletableFuture.allOf(passExerciseTotalsMapFuture, lecturesByTopicDtoListFuture).join();

        Map<UUID, Integer> passMap = passExerciseTotalsMapFuture.join();
        List<LecturesByTopicDto> lecturesByTopicDtoList = lecturesByTopicDtoListFuture.join();

        for (LecturesByTopicDto lecturesByTopicDto : lecturesByTopicDtoList) {
            List<Lecture> lectures = lectureRepository.findLecturesByTopicAndLevel(lecturesByTopicDto.getTopicId(), levelId);
            if (lectures == null || lectures.isEmpty()) {
                lecturesByTopicDto.setLectures(Collections.emptyList());
                continue;
            }
            List<LectureStatDetailDto> lectureStatDetailDtoList = convertLecturesToLectureStatList(passMap, lectures);
            lecturesByTopicDto.setLectures(lectureStatDetailDtoList);
        }
        lecturesByTopicDtoList = lecturesByTopicDtoList
                .stream()
                .filter(dto -> !dto.getLectures().isEmpty())
                .toList();

        return new GetLecturesByLevelResponse(
                "Get lectures successfully",
                lecturesByTopicDtoList.size(),
                lecturesByTopicDtoList
        );
    }

    @Override
    public GetLecturesWithStatsResponse getLecturesByKeyword(UUID userId, String keyword) {
        CompletableFuture<List<Lecture>> lecturesFuture = asyncService.runAsync(
                () -> lectureRepository.findLecturesByKeyword(keyword),
                Collections.emptyList());
        CompletableFuture<Map<UUID, Integer>> passExerciseTotalsMapFuture = asyncService.runAsync(
                () -> exerciseHistoryService.getPassedExerciseCountOfUser(userId),
                Collections.emptyMap());

        CompletableFuture.allOf(passExerciseTotalsMapFuture, lecturesFuture).join();

        Map<UUID, Integer> passMap = passExerciseTotalsMapFuture.join();
        List<Lecture> lectures = lecturesFuture.join();

        if (lectures == null || lectures.isEmpty()) {
            return new GetLecturesWithStatsResponse(
                    "Search lectures successfully",
                    0,
                    Collections.emptyList()
            );
        }
        return getLectureStatListResponse(passMap, lectures);
    }

    @Override
    public GetLectureSectionsResponse getLectureSectionsByLectureId(UUID lectureId) {
        Lecture lecture = lectureRepository.findByLectureIdAndStatus(lectureId, ContentStatus.VERIFIED).orElse(null);
        if (lecture == null) {
            return new GetLectureSectionsResponse(
                    "No verified lecture found with the given id",
                    0,
                    null,
                    Collections.emptyList()
            );
        }
        LectureOfSectionDto lectureInfo = LectureMapper.INSTANCE.lectureToLectureOfSectionDto(lecture);
        if (lecture.getSections() == null || lecture.getSections().isEmpty()) {
            return new GetLectureSectionsResponse(
                    "Sections not found for the lecture",
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
                "Get lecture sections successfully",
                sectionDtoList.size(),
                lectureInfo,
                sectionDtoList
        );
    }

    private GetLecturesWithStatsResponse getLectureStatListResponse(Map<UUID, Integer> passExerciseTotalsMap, List<Lecture> lectures) {
        List<LectureStatDetailDto> lectureStatDetailDtoList = convertLecturesToLectureStatList(
                passExerciseTotalsMap,
                lectures);
        return new GetLecturesWithStatsResponse(
                "Get lectures success",
                lectureStatDetailDtoList.size(),
                lectureStatDetailDtoList);
    }

    private List<LectureStatDetailDto> convertLecturesToLectureStatList(Map<UUID, Integer> passExerciseTotalsMap, List<Lecture> lectures) {
        List<LectureStatDetailDto> lectureStatDetailDtoList = new ArrayList<>();
        for (Lecture lecture : lectures) {
            LectureStatDetailDto lectureStatDetailDto = LectureMapper.INSTANCE.lectureToLectureStatDetailDto(lecture);
            lectureStatDetailDto.setTotalPassExercises(passExerciseTotalsMap.getOrDefault(lecture.getLectureId(), 0));
            lectureStatDetailDtoList.add(lectureStatDetailDto);
        }
        return lectureStatDetailDtoList;
    }
}