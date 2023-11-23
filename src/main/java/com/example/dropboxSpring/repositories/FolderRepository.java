package com.example.dropboxSpring.repositories;

import com.example.dropboxSpring.models.Folder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FolderRepository extends JpaRepository<Folder, UUID> {
}
