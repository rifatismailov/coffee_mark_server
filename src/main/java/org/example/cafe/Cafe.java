package org.example.cafe;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.user.User;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cafes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Cafe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String address;

    @NotBlank
    private String cafe_image;

    @ManyToOne
    @JoinColumn(name = "barista_id")
    private User barista;

    // Список клієнтів, які вибрали це кафе
    @ManyToMany(mappedBy = "selectedCafes")
    private List<User> clients = new ArrayList<>();
}

