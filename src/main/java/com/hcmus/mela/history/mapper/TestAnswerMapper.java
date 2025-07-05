package com.hcmus.mela.history.mapper;

import com.hcmus.mela.history.dto.dto.AnswerResultDto;
import com.hcmus.mela.history.dto.dto.TestAnswerDto;
import com.hcmus.mela.history.model.TestAnswer;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TestAnswerMapper {
    TestAnswerMapper INSTANCE = Mappers.getMapper(TestAnswerMapper.class);

    TestAnswer convertToTestAnswer(TestAnswerDto testAnswerDto);

    AnswerResultDto convertToAnswerResultDto(TestAnswer testAnswer);
}
