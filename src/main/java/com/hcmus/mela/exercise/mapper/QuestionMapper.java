package com.hcmus.mela.exercise.mapper;

import com.hcmus.mela.exercise.dto.dto.ExerciseDto;
import com.hcmus.mela.exercise.dto.dto.QuestionDto;
import com.hcmus.mela.exercise.dto.request.CreateQuestionRequest;
import com.hcmus.mela.exercise.dto.request.UpdateQuestionRequest;
import com.hcmus.mela.exercise.model.Question;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = OptionMapper.class)
public interface QuestionMapper {

    QuestionMapper INSTANCE = Mappers.getMapper(QuestionMapper.class);

    QuestionDto questionToQuestionDto(Question question);

    @Named("questionsToQuestionDtos")
    List<QuestionDto> questionsToQuestionDtos(List<Question> questions);

    @Mapping(source = "options", target = "options", qualifiedByName = "createOptionRequestsToOptions")
    Question createQuestionRequestToQuestion(CreateQuestionRequest createQuestionRequest);

    @Named("createQuestionRequestsToQuestions")
    List<Question> createQuestionRequestsToQuestions(List<CreateQuestionRequest> createQuestionRequests);

    Question updateQuestionRequestToQuestion(UpdateQuestionRequest updateQuestionRequest);
}
