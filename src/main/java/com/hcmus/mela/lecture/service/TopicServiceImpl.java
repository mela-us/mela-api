package com.hcmus.mela.lecture.service;

import com.hcmus.mela.lecture.dto.dto.TopicDto;
import com.hcmus.mela.lecture.dto.request.CreateTopicRequest;
import com.hcmus.mela.lecture.dto.request.UpdateTopicRequest;
import com.hcmus.mela.lecture.dto.response.CreateTopicResponse;
import com.hcmus.mela.lecture.dto.response.GetTopicsResponse;
import com.hcmus.mela.lecture.mapper.TopicMapper;
import com.hcmus.mela.lecture.model.Topic;
import com.hcmus.mela.lecture.repository.TopicRepository;
import com.hcmus.mela.lecture.strategy.TopicFilterStrategy;
import com.hcmus.mela.shared.type.ContentStatus;
import com.hcmus.mela.shared.utils.GeneralMessageAccessor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TopicServiceImpl implements TopicService {

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
    public CreateTopicResponse getCreateTopicResponse(UUID creatorId, CreateTopicRequest createTopicRequest) {
        Topic topic = TopicMapper.INSTANCE.createTopicRequestToTopic(createTopicRequest);
        topic.setTopicId(UUID.randomUUID());
        topic.setCreatedBy(creatorId);
        topic.setStatus(ContentStatus.PENDING);
        Topic savedTopic = topicRepository.save(topic);

        TopicDto topicDto = TopicMapper.INSTANCE.topicToTopicDto(savedTopic);

        return new CreateTopicResponse(
                "Create topic successfully",
                topicDto
        );
    }

    @Override
    public void updateTopic(TopicFilterStrategy strategy, UUID userId, UUID topicId, UpdateTopicRequest request) {
        strategy.updateTopic(userId, topicId, request);
    }

    @Override
    public List<TopicDto> getTopics() {
        List<Topic> topics = topicRepository.findAll();
        return topics.isEmpty()
                ? Collections.emptyList()
                : topics.stream().map(TopicMapper.INSTANCE::topicToTopicDto).toList();
    }

    @Override
    public TopicDto getTopicById(UUID topicId) {
        Topic topic = topicRepository.findById(topicId).orElse(null);

        if (topic == null) {
            return null;
        }

        return TopicMapper.INSTANCE.topicToTopicDto(topic);
    }
}
