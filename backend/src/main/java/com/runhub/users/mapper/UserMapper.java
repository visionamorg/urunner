package com.runhub.users.mapper;

import com.runhub.users.dto.UserDto;
import com.runhub.users.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "displayUsername", target = "username")
    UserDto toDto(User user);
}
