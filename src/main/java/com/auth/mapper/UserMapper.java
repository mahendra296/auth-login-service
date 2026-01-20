package com.auth.mapper;

import com.auth.dto.UserDto;
import com.auth.dto.UserResponse;
import com.auth.model.User;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "authAccounts", ignore = true)
    User toEntity(UserDto userDto);

    UserDto toDto(User user);

    UserResponse toResponse(User user);

    List<UserResponse> toResponseList(List<User> users);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "authAccounts", ignore = true)
    void updateEntityFromDto(UserDto userDto, @MappingTarget User user);
}
