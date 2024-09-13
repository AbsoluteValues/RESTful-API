package AbsoluteValue.RESTful.user.service.impl;

import AbsoluteValue.RESTful.user.mapper.UserMapper;
import AbsoluteValue.RESTful.user.service.UserService;
import AbsoluteValue.RESTful.user.vo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private UserMapper userMapper;

    public UserServiceImpl() {}
    @Autowired
    public UserServiceImpl(UserMapper userMapper) { this.userMapper = userMapper; }


    @Override
    public int signUpUser(User user) {
        int result = userMapper.signUpUser(user);
        return result;
    }

    @Override
    public User profileUser(String id) {
        User userInfo = userMapper.profileUser(id);
        return userInfo;
    }
}
