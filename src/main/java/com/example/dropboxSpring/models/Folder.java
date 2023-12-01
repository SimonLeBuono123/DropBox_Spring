package com.example.dropboxSpring.models;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class Folder {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    @Column(nullable = false)
    private String name;


    @ManyToOne(optional = false)
    private User user;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<File> files;

    public Folder(String name, User user, List<File> files) {
        this.name = name;
        this.user = user;
        this.files = new ArrayList<>();
    }
}
