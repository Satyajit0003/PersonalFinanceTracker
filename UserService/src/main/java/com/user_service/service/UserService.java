package com.user_service.service;


import com.user_service.dto.UserDto;
import com.user_service.entity.User;

import java.util.List;

public interface UserService {

    User createUser(UserDto userDto, String role);

    User getUserById(String id);

    List<User> getAllUsers();

    User updateUser(UserDto userDto, String userId);

    void deleteUser(String userId);


}
