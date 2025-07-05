package com.hcmus.mela.topic.service;

import com.hcmus.mela.shared.type.ContentStatus;
import com.hcmus.mela.shared.utils.GeneralMessageAccessor;
import com.hcmus.mela.topic.dto.dto.TopicDto;
import com.hcmus.mela.topic.dto.request.CreateTopicRequest;
import com.hcmus.mela.topic.dto.request.UpdateTopicRequest;
import com.hcmus.mela.topic.dto.response.CreateTopicResponse;
import com.hcmus.mela.topic.dto.response.GetTopicsResponse;
import com.hcmus.mela.topic.exception.TopicException;
import com.hcmus.mela.topic.mapper.TopicMapper;
import com.hcmus.mela.topic.model.Topic;
import com.hcmus.mela.topic.repository.TopicRepository;
import com.hcmus.mela.topic.strategy.TopicFilterStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TopicQueryServiceImpl implements TopicQueryService {

    private final TopicRepository topicRepository;

    private final GeneralMessageAccessor generalMessageAccessor;

    public GetTopicsResponse getTopicsResponse(TopicFilterStrategy topicFilterStrategy, UUID userId) {

        List<TopicDto> topics = topicFilterStrategy.getTopics(userId);

        if (topics.isEmpty()) {
            return new GetTopicsResponse(
                    generalMessageAccessor.getMessage(null, "get_topics_empty"),
                    0,
                    Collections.emptyList()
            );
        }

        return new GetTopicsResponse(
                generalMessageAccessor.getMessage(null, "get_topics_success"),
                topics.size(),
                topics
        );
    }

    @Override
    public boolean checkTopicStatus(UUID topicId, ContentStatus status) {
        if (topicId == null || status == null) {
            return false;
        }
        Topic topic = topicRepository.findById(topicId).orElse(null);
        return topic != null && topic.getStatus() == status;
    }

    @Override
    public List<TopicDto> getTopics() {
        List<Topic> topics = topicRepository.findAll();
        return topics.isEmpty()
                ? Collections.emptyList()
                : topics.stream().map(TopicMapper.INSTANCE::topicToTopicDto).toList();
    }
}
