package AbsoluteValue.RESTful.user.service;

import AbsoluteValue.RESTful.user.vo.User;

public interface UserService {

    /**
     * 회원 가입 Service
     * @param user
     * @return int
     */
    int signUpUser(User user);

    /**
     * 회원 정보 조회 Service
     * @param id
     * @return int
     */
    User profileUser(String id);
}
