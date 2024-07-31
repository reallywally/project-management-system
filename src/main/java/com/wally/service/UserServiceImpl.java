package com.wally.service;

import com.wally.config.JwtProvider;
import com.wally.model.User;
import com.wally.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;

    @Override
    public User findUserProfileByJwt(String jwt) throws Exception {
        //  여기서 Bearer를 제거하게 아닐텐데
        jwt = jwt.replace("Bearer ", "");
        String email = JwtProvider.getEmailFromToken(jwt);

        return findUserByEmail(email);
    }

    @Override
    public User findUserByEmail(String email) throws Exception {
        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new Exception("User not found");
        }

        return user;
    }

    @Override
    public User findUserById(Long id) throws Exception {
        Optional<User> optionalUser = userRepository.findById(id);

        if(optionalUser.isEmpty()){
            throw new Exception("User not found");
        }

        return optionalUser.get();
    }

    @Override
    public User updateUsersProjectsSize(User user, int size) throws Exception {
        user.setProjectSize(user.getProjectSize()+size);

        return userRepository.save(user);
    }
}
