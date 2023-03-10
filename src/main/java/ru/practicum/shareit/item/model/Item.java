package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.Map;

/**
 * TODO Sprint add-controllers.
 */

@Data
@Builder
public class Item {

    @Positive
    private Integer id;

    @NotNull
    @NotBlank
    private String name;

    @NotNull
    @NotBlank
    private String description;

    @NotNull
    private Boolean available;

    private User owner;

    public Map<String, Object> toMap() {
        return Map.of("name", name,
                "description", description,
                "available", available,
                "owner", owner);
    }

}
