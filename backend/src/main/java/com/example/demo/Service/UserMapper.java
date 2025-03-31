package com.example.demo.Service;

import com.example.demo.Model.User;
import com.example.demo.dto.SignUpDto;
import com.example.demo.dto.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto toUserDto(User user);

    @Mapping(target = "password", ignore = true)
    User signUpToUser(SignUpDto signUpDto);

}