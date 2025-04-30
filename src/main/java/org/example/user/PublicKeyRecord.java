package org.example.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "public_key_record")
public class PublicKeyRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String uuid;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String publicKey;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    private LocalDateTime createdAt;
}

