package com.example.dropboxSpring.models;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;
@Data
@Entity
@Table(name = "files")
@NoArgsConstructor
public class File {
    @Setter(value = AccessLevel.PRIVATE)
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String name;
    private String type;

    @Column(columnDefinition = "mediumblob")
    @Lob
    private byte[] data;


    public File(String name, String type, byte[] data) {
        this.name = name;
        this.type = type;
        this.data = data;
    }
}
