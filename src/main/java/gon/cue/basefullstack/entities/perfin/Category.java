package gon.cue.basefullstack.entities.perfin;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "Categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}
