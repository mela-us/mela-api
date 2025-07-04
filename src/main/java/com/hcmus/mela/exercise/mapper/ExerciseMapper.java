package com.hcmus.mela.exercise.mapper;

import com.hcmus.mela.exercise.dto.dto.ExerciseDetailDto;
import com.hcmus.mela.exercise.dto.dto.ExerciseDto;
import com.hcmus.mela.exercise.dto.dto.ExerciseStatDetailDto;
import com.hcmus.mela.exercise.dto.request.CreateExerciseRequest;
import com.hcmus.mela.exercise.model.Exercise;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = QuestionMapper.class)
public interface ExerciseMapper {

    ExerciseMapper INSTANCE = Mappers.getMapper(ExerciseMapper.class);

    @Mapping(source = "questions", target = "questions", qualifiedByName = "questionsToQuestionDtos")
    ExerciseDto exerciseToExerciseDto(Exercise exercise);

    @Mapping(source = "questions", target = "questions", qualifiedByName = "createQuestionRequestsToQuestions")
    Exercise createExerciseRequestToExercise(CreateExerciseRequest createExerciseRequest);

    ExerciseDetailDto exerciseToExerciseDetailDto(Exercise exercise);

    ExerciseStatDetailDto exerciseToExerciseStatDetailDto(Exercise exercise);
}
