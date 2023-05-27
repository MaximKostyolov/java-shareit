package ru.practicum.shareit.user.model;

import lombok.*;

import javax.persistence.*;
import java.util.Map;


/**
 * TODO Sprint add-controllers.
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String name;

    public Map<String, Object> toMap() {
        return Map.of("email", email,
                "name", name);
    }

}