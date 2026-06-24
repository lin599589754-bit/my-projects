package com.freshfood.backend.service;

import com.freshfood.backend.common.NotFoundException;
import com.freshfood.backend.entity.User;
import com.freshfood.backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserService {

    private static final Byte NORMAL_STATUS = (byte) 1;
    private static final Byte UNKNOWN_GENDER = (byte) 0;

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public User login(String openid, String nickName, String avatarUrl) {
        LocalDateTime now = LocalDateTime.now();

        User user = userRepository.findByOpenid(openid)
                .orElseGet(User::new);

        if (user.getId() == null) {
            user.setOpenid(openid);
            user.setGender(UNKNOWN_GENDER);
            user.setStatus(NORMAL_STATUS);
            user.setCreateTime(now);
        }

        user.setNickName(nickName);
        user.setAvatarUrl(avatarUrl);
        user.setLastLoginTime(now);
        user.setUpdateTime(now);

        return userRepository.save(user);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("用户不存在"));
    }

    public User getUserByOpenid(String openid) {
        return userRepository.findByOpenid(openid).orElse(null);
    }

    public List<User> listUsers() {
        return userRepository.findAll();
    }
}
