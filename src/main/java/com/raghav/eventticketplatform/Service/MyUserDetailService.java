package com.raghav.eventticketplatform.Service;

import com.raghav.eventticketplatform.Entity.UserPrinciple;
import com.raghav.eventticketplatform.Entity.Users;
import com.raghav.eventticketplatform.Repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailService implements UserDetailsService {
    @Autowired
    UserRepo repo;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users users = repo.findByUsername(username);
        if (users == null){
            throw new UsernameNotFoundException("User Not Found");

        }
        return new UserPrinciple(users);
    }
}
