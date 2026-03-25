package com.runhub.running.mapper;

import com.runhub.running.dto.ActivityDto;
import com.runhub.running.dto.ActivitySplitDto;
import com.runhub.running.model.ActivitySplit;
import com.runhub.running.model.RunningActivity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ActivityMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.displayUsername", target = "username")
    ActivityDto toDto(RunningActivity activity);

    ActivitySplitDto toSplitDto(ActivitySplit split);
}
