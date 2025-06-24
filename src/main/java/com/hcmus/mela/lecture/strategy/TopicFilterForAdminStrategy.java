package com.hcmus.mela.lecture.strategy;

import com.hcmus.mela.lecture.dto.dto.TopicDto;
import com.hcmus.mela.lecture.mapper.TopicMapper;
import com.hcmus.mela.lecture.model.Topic;
import com.hcmus.mela.lecture.repository.TopicRepository;
import com.hcmus.mela.shared.type.ContentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component("TOPIC_ADMIN")
@RequiredArgsConstructor
public class TopicFilterForAdminStrategy implements TopicFilterStrategy {

    private final TopicRepository topicRepository;

    @Override
    public List<TopicDto> getTopics(UUID userId) {
        List<Topic> topics = topicRepository.findAll();
        if (topics.isEmpty()) {
            return List.of();
        }
        return topics.stream()
                .map(TopicMapper.INSTANCE::topicToTopicDto)
                .toList();
    }
}