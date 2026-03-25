package com.runhub.programs.mapper;

import com.runhub.programs.dto.ProgramDto;
import com.runhub.programs.dto.ProgramSessionDto;
import com.runhub.programs.model.Program;
import com.runhub.programs.model.ProgramSession;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProgramMapper {

    @Mapping(target = "sessionsCount", ignore = true)
    @Mapping(source = "community.id", target = "communityId")
    @Mapping(source = "createdBy.displayUsername", target = "createdByUsername")
    ProgramDto toDto(Program program);

    ProgramSessionDto toSessionDto(ProgramSession session);
}
