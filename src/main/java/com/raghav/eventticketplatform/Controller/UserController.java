package com.raghav.eventticketplatform.Controller;

import com.raghav.eventticketplatform.DTO.LoginDTO;
import com.raghav.eventticketplatform.DTO.RegisteringDTO;
import com.raghav.eventticketplatform.Entity.Users;
import com.raghav.eventticketplatform.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class UserController {

    @Autowired
    private UserService userService;
    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
    @PostMapping("/register")
    public ResponseEntity<Users> register(@RequestBody RegisteringDTO dto) {
        dto.setPassword(encoder.encode(dto.getPassword()));
        return new ResponseEntity<>(userService.register(dto), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginDTO dto) {
        return new ResponseEntity<>(userService.verify(dto), HttpStatus.OK);
    }
}
