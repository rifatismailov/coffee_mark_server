package org.example.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.cafe.Cafe;

@Entity
@Table(name = "user_cafes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserCafe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "cafe_id")
    private Cafe cafe;

    private int coffeeCount = 0; // Початково 0 кав
}

