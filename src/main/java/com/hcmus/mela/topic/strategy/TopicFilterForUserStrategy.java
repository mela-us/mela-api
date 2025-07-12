package com.hcmus.mela.topic.strategy;

import com.hcmus.mela.topic.dto.dto.TopicDto;
import com.hcmus.mela.topic.dto.request.UpdateTopicRequest;
import com.hcmus.mela.topic.mapper.TopicMapper;
import com.hcmus.mela.topic.model.Topic;
import com.hcmus.mela.topic.repository.TopicRepository;
import com.hcmus.mela.shared.type.ContentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component("TOPIC_USER")
@RequiredArgsConstructor
public class TopicFilterForUserStrategy implements TopicFilterStrategy {

    private final TopicRepository topicRepository;

    @Override
    public List<TopicDto> getTopics(UUID userId) {
        List<Topic> topics = topicRepository.findAllByStatus(ContentStatus.VERIFIED);
        if (topics.isEmpty()) {
            return List.of();
        }
        return topics.stream()
                .map(TopicMapper.INSTANCE::topicToTopicDto)
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