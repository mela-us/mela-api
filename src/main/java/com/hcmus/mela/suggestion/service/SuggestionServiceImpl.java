package com.hcmus.mela.suggestion.service;

import com.hcmus.mela.history.model.LectureCompletedSection;
import com.hcmus.mela.history.model.LectureHistory;
import com.hcmus.mela.history.service.LectureHistoryService;
import com.hcmus.mela.lecture.dto.dto.LectureDto;
import com.hcmus.mela.lecture.dto.dto.SectionDto;
import com.hcmus.mela.lecture.service.LectureInfoService;
import com.hcmus.mela.level.dto.dto.LevelDto;
import com.hcmus.mela.level.service.LevelInfoService;
import com.hcmus.mela.shared.type.ContentStatus;
import com.hcmus.mela.suggestion.dto.dto.SectionReferenceDto;
import com.hcmus.mela.suggestion.dto.dto.SuggestionDto;
import com.hcmus.mela.suggestion.dto.request.UpdateSuggestionRequest;
import com.hcmus.mela.suggestion.dto.response.GetSuggestionsResponse;
import com.hcmus.mela.suggestion.dto.response.UpdateSuggestionResponse;
import com.hcmus.mela.suggestion.exception.SuggestionNotFoundException;
import com.hcmus.mela.suggestion.mapper.SuggestionMapper;
import com.hcmus.mela.suggestion.model.SectionReference;
import com.hcmus.mela.suggestion.model.Suggestion;
import com.hcmus.mela.suggestion.repository.SuggestionRepository;
import com.hcmus.mela.topic.dto.dto.TopicDto;
import com.hcmus.mela.topic.service.TopicInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class SuggestionServiceImpl implements SuggestionService {

    private final LectureHistoryService lectureHistoryService;
    private final SuggestionRepository suggestionRepository;
    private final TopicInfoService topicInfoService;
    private final LevelInfoService levelInfoService;
    private final LectureInfoService lectureInfoService;

    @Override
    public GetSuggestionsResponse getSuggestions(UUID userId) {
        ZoneId zoneVN = ZoneId.of("Asia/Ho_Chi_Minh");

        ZonedDateTime startOfDayVN = ZonedDateTime.now(zoneVN).toLocalDate().atStartOfDay(zoneVN);
        ZonedDateTime endOfDayVN = startOfDayVN.plusDays(1);

        Date startOfDay = Date.from(startOfDayVN.toInstant());
        Date endOfDay = Date.from(endOfDayVN.toInstant());

        List<Suggestion> suggestions = suggestionRepository.findAllByUserIdAndCreatedAtBetween(userId, startOfDay, endOfDay);

        if (suggestions == null || suggestions.isEmpty()) {
            suggestions = createSuggestion(userId, startOfDay);
        }

        List<SuggestionDto> suggestionDtos = suggestions.stream()
                .map(SuggestionMapper.INSTANCE::suggestionToSuggestionDto)
                .sorted(Comparator.comparing(SuggestionDto::getOrdinalNumber))
                .toList();

        for (SuggestionDto suggestionDto : suggestionDtos) {
            List<SectionReferenceDto> sectionReferenceDtos = new ArrayList<>();
            for (SectionReferenceDto sectionReferenceDto : suggestionDto.getSectionList()) {
                LectureDto lectureDto = lectureInfoService.findLectureByLectureIdAndStatus(sectionReferenceDto.getLectureId(), ContentStatus.VERIFIED);
                if (lectureDto == null) {
                    continue;
                }
                TopicDto topicDto = topicInfoService.findTopicByTopicId(lectureDto.getTopicId());
                LevelDto levelDto = levelInfoService.findLevelByLevelId(lectureDto.getLevelId());

                sectionReferenceDto.setLectureTitle(lectureDto.getName());
                sectionReferenceDto.setTopicTitle(topicDto.getName());
                sectionReferenceDto.setLevelTitle(levelDto.getName());
                String sectionUrl = lectureDto.getSections()
                        .stream()
                        .filter(section -> section.getOrdinalNumber().equals(sectionReferenceDto.getOrdinalNumber()))
                        .findFirst()
                        .map(SectionDto::getUrl)
                        .orElse(null);
                sectionReferenceDto.setSectionUrl(sectionUrl);
                sectionReferenceDtos.add(sectionReferenceDto);
            }
            suggestionDto.setSectionList(sectionReferenceDtos);
        }
        return new GetSuggestionsResponse("Get suggestions successfully", suggestionDtos);
    }

    @Override
    public UpdateSuggestionResponse updateSuggestion(UUID userId, UUID suggestionId, UpdateSuggestionRequest request) {
        Suggestion suggestion = suggestionRepository.findBySuggestionIdAndUserId(suggestionId, userId);
        if (suggestion == null) {
            throw new SuggestionNotFoundException("Suggestion not found for user with id " + suggestionId);
        }

        for (SectionReference sectionReference : suggestion.getSectionList()) {
            if (sectionReference.getLectureId().equals(request.getItemId())
                    && sectionReference.getOrdinalNumber().equals(request.getOrdinalNumber())) {
                sectionReference.setIsDone(request.getIsDone());
                break;
            }
        }

        Suggestion result = suggestionRepository.updateSuggestion(suggestion);
        return new UpdateSuggestionResponse("Update suggestion successfully");
    }

    @Override
    public void deleteSuggestion(UUID userId) {
        suggestionRepository.deleteAllByUserId(userId);
    }

    private List<Suggestion> createSuggestion(UUID userId, Date startOfDay) {
        List<LectureHistory> lectureHistories = lectureHistoryService.getBestProgressHistoriesGroupedByLecture(userId);
        if (lectureHistories.isEmpty()) {
            return List.of();
        }

        int sectionSize = 0;
        List<Suggestion> results = new ArrayList<>();
        List<LectureDto> suggestedLectures = new ArrayList<>();
        List<UUID> studiedLectureIds = new ArrayList<>(lectureHistories.stream()
                .map(LectureHistory::getLectureId)
                .toList());

        for (LectureHistory history : lectureHistories) {
            List<SectionReference> sectionReferences = new ArrayList<>();
            boolean isSuggested = false;

            if (history.getProgress() < 100) {
                LectureDto lecture = lectureInfoService.findLectureByLectureId(history.getLectureId());
                List<Integer> completedSectionNumbers = history.getCompletedSections().stream()
                        .map(LectureCompletedSection::getOrdinalNumber)
                        .toList();

                for (SectionDto section : lecture.getSections()) {
                    if (!completedSectionNumbers.contains(section.getOrdinalNumber())) {
                        sectionReferences.add(new SectionReference(lecture.getLectureId(),
                                section.getOrdinalNumber(),
                                false));
                        isSuggested = true;
                    }
                }

                if (isSuggested) {
                    suggestedLectures.add(lecture);
                }
            } else {
                LectureDto lecture = lectureInfoService.findLectureByTopicIdAndLevelIdAndOrdinalNumber(
                        history.getTopicId(),
                        history.getLevelId(),
                        lectureInfoService.findLectureOrdinalNumberByLectureId(history.getLectureId()) + 1);

                if (lecture != null) {
                    if (!studiedLectureIds.contains(lecture.getLectureId())) {
                        for (SectionDto section : lecture.getSections()) {
                            sectionReferences.add(new SectionReference(
                                    lecture.getLectureId(),
                                    section.getOrdinalNumber(),
                                    false));
                        }
                        suggestedLectures.add(lecture);
                        studiedLectureIds.add(lecture.getLectureId());
                    }
                }
            }

            if (!sectionReferences.isEmpty()) {
                Suggestion suggestion = Suggestion.builder()
                        .suggestionId(UUID.randomUUID())
                        .userId(userId)
                        .ordinalNumber(results.size() + 1)
                        .createdAt(startOfDay)
                        .sectionList(sectionReferences)
                        .build();
                results.add(suggestion);
                sectionSize += sectionReferences.size();
            }

            if (sectionSize >= 3) {
                break;
            }
        }

        if (sectionSize < 3 && !suggestedLectures.isEmpty()) {
            List<UUID> suggestedLectureIds = new ArrayList<>(suggestedLectures.stream()
                    .map(LectureDto::getLectureId)
                    .toList());

            for (int i = 0; i < suggestedLectureIds.size(); i++) {
                LectureDto lecture = suggestedLectures.get(i);
                List<SectionReference> sectionReferences = new ArrayList<>();

                LectureDto nextLecture = lectureInfoService.findLectureByTopicIdAndLevelIdAndOrdinalNumber(
                        lecture.getTopicId(),
                        lecture.getLevelId(),
                        lectureInfoService.findLectureOrdinalNumberByLectureId(lecture.getLectureId()) + 1);

                if (nextLecture != null) {
                    if (!suggestedLectureIds.contains(nextLecture.getLectureId())) {
                        for (SectionDto section : nextLecture.getSections()) {
                            sectionReferences.add(new SectionReference(
                                    nextLecture.getLectureId(),
                                    section.getOrdinalNumber(),
                                    false));
                        }
                        suggestedLectures.add(nextLecture);
                        suggestedLectureIds.add(nextLecture.getLectureId());
                        Suggestion suggestion = Suggestion.builder()
                                .suggestionId(UUID.randomUUID())
                                .userId(userId)
                                .ordinalNumber(results.size() + 1)
                                .createdAt(startOfDay)
                                .sectionList(sectionReferences)
                                .build();
                        results.add(suggestion);
                        sectionSize += sectionReferences.size();
                    }
                }
                if (sectionSize >= 3) {
                    break;
                }
            }
        }
        return suggestionRepository.saveAll(results);
    }
}
