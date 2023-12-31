package com.example.dropboxSpring.repositories;

import com.example.dropboxSpring.models.Folder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Interface for handling all queries of entity model
 * Folder
 */
public interface FolderRepository extends JpaRepository<Folder, UUID> {
    @Query(value = """
            SELECT fo.id, fo.name, fo.user_id FROM folder_files LEFT JOIN files f ON f.id = folder_files.files_id
            LEFT JOIN folder fo ON fo.id = folder_files.folder_id
            WHERE f.id = ?;
            """, nativeQuery = true)
    Optional<Folder> findFolderByFileId(UUID fileId);


    @Query(value = "SELECT * FROM folder f WHERE user_id = ?", nativeQuery = true)
    List<Folder> findAllFoldersByUserId(UUID user);
}
