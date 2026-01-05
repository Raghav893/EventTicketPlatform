package com.raghav.eventticketplatform.Service;

import com.raghav.eventticketplatform.DTO.LoginDTO;
import com.raghav.eventticketplatform.DTO.RegisteringDTO;
import com.raghav.eventticketplatform.Entity.Roles;
import com.raghav.eventticketplatform.Entity.Users;
import com.raghav.eventticketplatform.Repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserService {

    @Autowired
    private UserRepo repo;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;


    public Users register(RegisteringDTO dto) {

        Users user = new Users();
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());
        user.setRoles(Roles.valueOf(dto.getRole()));
        user.setEnabled(true);
        user.setAuthProvider("JWT");
        user.setCreatedAt(LocalDateTime.now());

        return repo.save(user);
    }

    public String verify(LoginDTO dto) {

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                dto.getUsername(),
                                dto.getPassword()
                        )
                );

        return jwtService.genrateToken(dto.getUsername());
    }
}
