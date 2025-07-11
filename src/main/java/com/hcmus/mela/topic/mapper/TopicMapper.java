package com.hcmus.mela.topic.mapper;

import com.hcmus.mela.lecture.dto.dto.LecturesByTopicDto;
import com.hcmus.mela.lecture.dto.dto.TopicCount;
import com.hcmus.mela.topic.dto.dto.TopicDto;
import com.hcmus.mela.topic.dto.request.CreateTopicRequest;
import com.hcmus.mela.topic.model.Topic;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TopicMapper {

    TopicMapper INSTANCE = Mappers.getMapper(TopicMapper.class);

    TopicDto topicToTopicDto(Topic topic);

    @Mapping(source = "name", target = "topicName")
    LecturesByTopicDto topicDtoToLecturesByTopicDto(TopicDto topicDto);

    Topic createTopicRequestToTopic(CreateTopicRequest createTopicRequest);
}
