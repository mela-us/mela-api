package com.hcmus.mela.exercise.mapper;

import com.hcmus.mela.exercise.dto.dto.QuestionDto;
import com.hcmus.mela.exercise.dto.request.CreateOptionRequest;
import com.hcmus.mela.exercise.model.Option;
import com.hcmus.mela.exercise.model.Question;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OptionMapper {

    OptionMapper INSTANCE = Mappers.getMapper(OptionMapper.class);

    Option createOptionRequestToOption(CreateOptionRequest createOptionRequest);

    @Named("createOptionRequestsToOptions")
    List<Option> createOptionRequestsToOptions(List<CreateOptionRequest> createOptionRequests);
}
