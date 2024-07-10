package com.wally.service;

import com.wally.model.User;

public interface UserService {
    User findUserProfileByJwt(String jwt) throws Exception;

    User findUserByEmail(String email) throws Exception;

    User findUserById(Long id) throws Exception;

    User updateUsersProjectsSize(User user, int size) throws Exception;
}
