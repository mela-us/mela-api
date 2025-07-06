package com.hcmus.mela.test.mapper;


import com.hcmus.mela.test.dto.QuestionDto;
import com.hcmus.mela.test.model.Question;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface QuestionMapper {

    QuestionMapper INSTANCE = Mappers.getMapper(QuestionMapper.class);

    QuestionDto questionToQuestionDto(Question question);

    @Named("questionsToQuestionDtoList")
    List<QuestionDto> questionsToQuestionDtoList(List<Question> questions);
}
