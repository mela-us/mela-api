package com.hcmus.mela.lecture.service;

import com.hcmus.mela.history.service.ExerciseHistoryService;
import com.hcmus.mela.lecture.dto.dto.*;
import com.hcmus.mela.lecture.dto.response.*;
import com.hcmus.mela.lecture.mapper.LectureMapper;
import com.hcmus.mela.lecture.mapper.LectureSectionMapper;
import com.hcmus.mela.lecture.model.Lecture;
import com.hcmus.mela.lecture.model.LectureActivity;
import com.hcmus.mela.lecture.repository.LectureCustomRepositoryImpl;
import com.hcmus.mela.lecture.repository.LectureRepository;
import com.hcmus.mela.lecture.strategy.LectureFilterStrategy;
import com.hcmus.mela.shared.async.AsyncCustomService;
import com.hcmus.mela.shared.type.ContentStatus;
import com.hcmus.mela.shared.utils.GeneralMessageAccessor;
import com.hcmus.mela.topic.mapper.TopicMapper;
import com.hcmus.mela.topic.service.TopicQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class LectureQueryServiceImpl implements LectureQueryService {

    private final GeneralMessageAccessor generalMessageAccessor;

    private final TopicQueryService topicQueryService;

    private final LectureRepository lectureRepository;

    private final ExerciseHistoryService exerciseHistoryService;

    private final AsyncCustomService asyncService;

    private final LectureCustomRepositoryImpl lectureCustomRepositoryImpl;

    @Override
    public GetAllLecturesResponse getAllLectures(LectureFilterStrategy strategy, UUID userId) {
        List<LectureDto> lectures = strategy.getLectures(userId);
        if (lectures.isEmpty()) {
            return new GetAllLecturesResponse("No lectures found", Collections.emptyList());
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
                () -> topicQueryService.getVerifiedTopics()
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
                .stream().filter(dto -> !dto.getLectures().isEmpty()).toList();

        return new GetLecturesByLevelResponse(
                generalMessageAccessor.getMessage(null, "get_lectures_success"),
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
                    generalMessageAccessor.getMessage(null, "search_lectures_success"),
                    0,
                    Collections.emptyList()
            );
        }
        return getLectureStatListResponse(passMap, lectures);
    }

    @Override
    public GetLecturesWithStatsResponse getLecturesByRecent(UUID userId, Integer size) {
        CompletableFuture<List<LectureActivity>> exerciseHistoryFuture = asyncService.runAsync(
                () -> lectureRepository.findRecentLectureByUserExerciseHistory(userId, size),
                Collections.emptyList());
        CompletableFuture<List<LectureActivity>> sectionHistoryFuture = asyncService.runAsync(
                () -> lectureRepository.findRecentLectureByUserSectionHistory(userId, size),
                Collections.emptyList());
        CompletableFuture<Map<UUID, Integer>> passMapFuture = asyncService.runAsync(
                () -> exerciseHistoryService.getPassedExerciseCountOfUser(userId),
                Collections.emptyMap());
        CompletableFuture.allOf(exerciseHistoryFuture, sectionHistoryFuture, passMapFuture).join();

        List<LectureActivity> exerciseHistory = Optional.ofNullable(exerciseHistoryFuture.join()).orElse(Collections.emptyList());
        List<LectureActivity> sectionHistory = Optional.ofNullable(sectionHistoryFuture.join()).orElse(Collections.emptyList());

        List<LectureActivity> recentLectures = mergeActivity(exerciseHistory, sectionHistory);
        if (recentLectures.isEmpty()) {
            return new GetLecturesWithStatsResponse(
                    generalMessageAccessor.getMessage(null, "get_recent_lectures_success"),
                    0,
                    Collections.emptyList()
            );
        }
        List<Lecture> lectures = recentLectures.stream()
                .sorted(Comparator.comparing(LectureActivity::getCompletedAt).reversed())
                .limit(size)
                .map(LectureMapper.INSTANCE::lectureActivityToLecture)
                .toList();

        return getLectureStatListResponse(passMapFuture.join(), lectures);
    }

    @Override
    public GetLectureSectionsResponse getLectureSectionsByLectureId(UUID lectureId) {
        Lecture lecture = lectureRepository.findByLectureId(lectureId);
        if (lecture == null || lecture.getStatus() != ContentStatus.VERIFIED) {
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

    private List<LectureActivity> mergeActivity(List<LectureActivity> first, List<LectureActivity> second) {
        List<LectureActivity> recentLecture = new ArrayList<>();

        List<LectureActivity> modifiableFirst = new ArrayList<>(first);
        List<LectureActivity> modifiableSecond = new ArrayList<>(second);
        modifiableFirst.sort(Comparator.comparing(LectureActivity::getLectureId));
        modifiableSecond.sort(Comparator.comparing(LectureActivity::getLectureId));

        int n = modifiableFirst.size() + modifiableSecond.size();
        int firstIndex = 0;
        int secondIndex = 0;
        while (firstIndex + secondIndex <= n && firstIndex < modifiableFirst.size() && secondIndex < modifiableSecond.size()) {
            if (modifiableFirst.get(firstIndex).compareTo(modifiableSecond.get(secondIndex)) < 0) {
                recentLecture.add(modifiableFirst.get(firstIndex));
                firstIndex++;
            } else if (modifiableFirst.get(firstIndex).compareTo(modifiableSecond.get(secondIndex)) > 0) {
                recentLecture.add(modifiableSecond.get(secondIndex));
                secondIndex++;
            } else if (modifiableFirst.get(firstIndex++).getCompletedAt()
                    .isAfter(modifiableSecond.get(secondIndex++).getCompletedAt())) {
                recentLecture.add(modifiableFirst.get(firstIndex - 1));
            } else {
                recentLecture.add(modifiableSecond.get(secondIndex - 1));
            }
        }

        if (firstIndex < modifiableFirst.size()) {
            recentLecture.addAll(modifiableFirst.subList(firstIndex, modifiableFirst.size()));
        }
        if (secondIndex < modifiableSecond.size()) {
            recentLecture.addAll(modifiableSecond.subList(secondIndex, modifiableSecond.size()));
        }
        recentLecture.sort(Comparator.comparing(LectureActivity::getCompletedAt).reversed());
        return recentLecture;
    }

    private GetLecturesWithStatsResponse getLectureStatListResponse(Map<UUID, Integer> passExerciseTotalsMap, List<Lecture> lectures) {
        List<LectureStatDetailDto> lectureStatDetailDtoList = convertLecturesToLectureStatList(
                passExerciseTotalsMap,
                lectures);
        return new GetLecturesWithStatsResponse(
                "Get lectures success!",
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