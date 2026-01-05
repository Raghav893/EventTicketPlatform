package com.raghav.eventticketplatform.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RegisteringDTO {
    String username;
    String password;
    String Role;
}
