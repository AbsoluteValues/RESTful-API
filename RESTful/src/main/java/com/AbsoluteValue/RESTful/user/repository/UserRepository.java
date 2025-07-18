package com.AbsoluteValue.RESTful.user.repository;

import com.AbsoluteValue.RESTful.user.entity.User;
import com.AbsoluteValue.RESTful.user.repository.custom.UserRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String>, UserRepositoryCustom {


}
