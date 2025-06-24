package com.hcmus.mela.topic.service;

import com.hcmus.mela.topic.dto.dto.TopicDto;
import com.hcmus.mela.topic.dto.request.CreateTopicRequest;
import com.hcmus.mela.topic.dto.request.UpdateTopicRequest;
import com.hcmus.mela.topic.dto.response.CreateTopicResponse;
import com.hcmus.mela.topic.dto.response.GetTopicsResponse;
import com.hcmus.mela.topic.mapper.TopicMapper;
import com.hcmus.mela.topic.model.Topic;
import com.hcmus.mela.topic.repository.TopicRepository;
import com.hcmus.mela.topic.strategy.TopicFilterStrategy;
import com.hcmus.mela.shared.exception.BadRequestException;
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
    public void denyTopic(UUID topicId, String reason) {
        Topic topic = topicRepository.findById(topicId).orElseThrow(() -> new BadRequestException("Topic not found"));
        if (topic.getStatus() == ContentStatus.VERIFIED || topic.getStatus() == ContentStatus.DELETED) {
            throw new BadRequestException("Topic cannot be denied");
        }
        topic.setRejectedReason(reason);
        topic.setStatus(ContentStatus.DENIED);
        topicRepository.save(topic);
    }

    @Override
    public void approveTopic(UUID topicId) {
        Topic topic = topicRepository.findById(topicId).orElseThrow(() -> new BadRequestException("Topic not found"));
        if (topic.getStatus() == ContentStatus.DELETED) {
            throw new BadRequestException("Topic cannot be approved");
        }
        topic.setRejectedReason(null);
        topic.setStatus(ContentStatus.VERIFIED);
        topicRepository.save(topic);
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
