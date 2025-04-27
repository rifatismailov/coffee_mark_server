package org.example.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.example.cafe.Cafe;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    @Email
    private String email;

    @Enumerated(EnumType.STRING)
    private Role role; // CLIENT або BARISTA

    @NotBlank
    private String image;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String public_key;

    // Якщо бариста — список кафе, які він створив
    @OneToMany(mappedBy = "barista", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Cafe> cafes = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserCafe> selectedCafes = new ArrayList<>();

}




