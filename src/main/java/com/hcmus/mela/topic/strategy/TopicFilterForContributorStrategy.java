package com.hcmus.mela.topic.strategy;

import com.hcmus.mela.lecture.strategy.LectureFilterForContributorStrategy;
import com.hcmus.mela.shared.type.ContentStatus;
import com.hcmus.mela.topic.dto.dto.TopicDto;
import com.hcmus.mela.topic.dto.request.UpdateTopicRequest;
import com.hcmus.mela.topic.exception.TopicException;
import com.hcmus.mela.topic.mapper.TopicMapper;
import com.hcmus.mela.topic.model.Topic;
import com.hcmus.mela.topic.repository.TopicRepository;
import com.hcmus.mela.user.service.UserInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component("TOPIC_CONTRIBUTOR")
@RequiredArgsConstructor
public class TopicFilterForContributorStrategy implements TopicFilterStrategy {

    private final TopicRepository topicRepository;
    private final UserInfoService userInfoService;
    private final LectureFilterForContributorStrategy lectureFilterStrategy;

    @Override
    public List<TopicDto> getTopics(UUID userId) {
        List<Topic> verifiedTopics = topicRepository.findAllByStatus(ContentStatus.VERIFIED);
        if (verifiedTopics.isEmpty()) {
            return List.of();
        }
        return verifiedTopics.stream()
                .map(topic -> {
                    TopicDto topicDto = TopicMapper.INSTANCE.topicToTopicDto(topic);
                    if (topic.getCreatedBy() != null) {
                        topicDto.setCreator(userInfoService.getUserPreviewDtoByUserId(topic.getCreatedBy()));
                    }
                    return topicDto;
                })
                .toList();
    }

    @Override
    public TopicDto createTopic(UUID userId, Topic topic) {
        return null;
    }

    @Override
    public void updateTopic(UUID userId, UUID topicId, UpdateTopicRequest request) {

    }

    @Override
    public void deleteTopic(UUID userId, UUID topicId) {

    }
}