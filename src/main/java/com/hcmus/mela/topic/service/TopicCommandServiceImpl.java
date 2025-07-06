package com.hcmus.mela.topic.service;

import com.hcmus.mela.shared.type.ContentStatus;
import com.hcmus.mela.topic.dto.dto.TopicDto;
import com.hcmus.mela.topic.dto.request.CreateTopicRequest;
import com.hcmus.mela.topic.dto.request.UpdateTopicRequest;
import com.hcmus.mela.topic.dto.response.CreateTopicResponse;
import com.hcmus.mela.topic.mapper.TopicMapper;
import com.hcmus.mela.topic.model.Topic;
import com.hcmus.mela.topic.repository.TopicRepository;
import com.hcmus.mela.topic.strategy.TopicFilterStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TopicCommandServiceImpl implements TopicCommandService {

    private final TopicRepository topicRepository;

    @Override
    public CreateTopicResponse createTopic(UUID userId, CreateTopicRequest request) {
        Topic topic = TopicMapper.INSTANCE.createTopicRequestToTopic(request);
        topic.setTopicId(UUID.randomUUID());
        topic.setCreatedBy(userId);
        topic.setStatus(ContentStatus.PENDING);
        Topic savedTopic = topicRepository.save(topic);
        TopicDto topicDto = TopicMapper.INSTANCE.topicToTopicDto(savedTopic);
        return new CreateTopicResponse("Create topic successfully", topicDto);
    }

    @Override
    public void updateTopic(TopicFilterStrategy strategy, UUID userId, UUID topicId, UpdateTopicRequest request) {
        strategy.updateTopic(userId, topicId, request);
    }

    @Override
    public void deleteTopic(TopicFilterStrategy strategy, UUID userId, UUID topicId) {
        strategy.deleteTopic(userId, topicId);
    }
}
