package com.AbsoluteValue.RESTful.common.auth.custom;

import com.AbsoluteValue.RESTful.user.entity.User;
import com.AbsoluteValue.RESTful.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        return userRepository.findUserById(id)
                .map(this::createUserDetails)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    private UserDetails createUserDetails(User user) {
        return org.springframework.security.core.userdetails.User.withUsername(user.getId())
                .password(user.getPassword())
                .roles("USER")
                .build();
    }
}
