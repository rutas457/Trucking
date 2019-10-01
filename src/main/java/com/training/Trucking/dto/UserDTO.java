package com.training.Trucking.dto;

import com.training.Trucking.validation.UniqueEmail;
import com.training.Trucking.validation.ValidName;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {

    @NotNull(message = "{not.null}")
    @Email(message = "{email.valid}")
    @Size(min = 5, max = 30, message = "{length.email}")
    @UniqueEmail
    private String email;

    @NotNull(message = "{not.null}")
    @ValidName
    @Size(min = 2, max = 30, message = "{length.name}")
    private String name;

    @NotNull(message = "{not.null}")
    @Size(min = 2, max = 30, message = "{length.surname}")
    @ValidName
    private String surname;

    @NotNull(message = "{not.null}")
    @Size(min = 5, max = 20, message = "{length.password}")
    private String password;
}
