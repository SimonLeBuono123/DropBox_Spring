package com.example.dropboxSpring.repositories;


import com.example.dropboxSpring.models.File;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FileRepository extends JpaRepository<File, UUID> {
}
