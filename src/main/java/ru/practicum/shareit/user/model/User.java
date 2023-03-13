package ru.practicum.shareit.user.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.Map;

/**
 * TODO Sprint add-controllers.
 */

@Data
@Builder
public class User {

    @Positive
    private Integer id;

    @Email
    @NotNull
    private String email;

    @NotBlank
    @NotNull
    private String name;

    public Map<String, Object> toMap() {
        return Map.of("email", email,
                "name", name);
    }

}
