package com.hcmus.mela.topic.service;

import com.hcmus.mela.shared.type.ContentStatus;
import com.hcmus.mela.shared.utils.ProjectConstants;
import com.hcmus.mela.topic.dto.dto.TopicDto;
import com.hcmus.mela.topic.mapper.TopicMapper;
import com.hcmus.mela.topic.model.Topic;
import com.hcmus.mela.topic.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TopicInfoServiceImpl implements TopicInfoService {

    private final TopicRepository topicRepository;

    @Override
    public List<TopicDto> findAllTopics() {
        List<Topic> topics = topicRepository.findAll();
        return topics.isEmpty()
                ? Collections.emptyList()
                : topics.stream().map(TopicMapper.INSTANCE::topicToTopicDto).toList();
    }

    @Override
    public List<TopicDto> findAllTopicsInStatus(ContentStatus status) {
        List<Topic> topics = topicRepository.findAllByStatus(status);
        return topics.isEmpty()
                ? Collections.emptyList()
                : topics.stream().map(TopicMapper.INSTANCE::topicToTopicDto).toList();
    }

    @Override
    public TopicDto findTopicByTopicId(UUID topicId) {
        Topic topic = topicRepository.findById(topicId).orElse(null);
        return topic == null ? null : TopicMapper.INSTANCE.topicToTopicDto(topic);
    }

    @Override
    public TopicDto findTopicByTopicIdAndStatus(UUID topicId, ContentStatus status) {
        Topic topic = topicRepository.findByTopicIdAndStatus(topicId, status).orElse(null);
        return topic == null ? null : TopicMapper.INSTANCE.topicToTopicDto(topic);
    }

    @Override
    public void changeTopicOwnerToAdmin(UUID previousUserId) {
        topicRepository.updateAllByCreatedBy(previousUserId, ProjectConstants.ADMIN_ID);
    }
}
