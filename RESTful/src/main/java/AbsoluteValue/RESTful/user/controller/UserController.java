package AbsoluteValue.RESTful.user.controller;

import AbsoluteValue.RESTful.user.service.UserService;
import AbsoluteValue.RESTful.user.vo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserController {

    private UserService userService;

    public UserController() {}
    @Autowired
    public UserController(UserService userService) { this.userService = userService; }

    @PostMapping("/user")
    public ResponseEntity<String> signUpUser(@RequestBody User user) {
        int result = userService.signUpUser(user);
        if (result > 0) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/user/profile/{id}")
    public ResponseEntity<String> profileUser(@PathVariable String id) {
        User userInfo = userService.profileUser(id);
        if (userInfo != null) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }
}
