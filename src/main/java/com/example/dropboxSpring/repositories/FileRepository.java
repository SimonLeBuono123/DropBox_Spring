package com.example.dropboxSpring.repositories;


import com.example.dropboxSpring.models.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Interface for handling all queries of entity model
 * File
 */
@Repository
public interface FileRepository extends JpaRepository<File, UUID> {
    @Query(value = """
            SELECT f.id, f.data, f.name, f.type FROM folder_files LEFT JOIN files f ON f.id = folder_files.files_id
            LEFT JOIN folder fo ON fo.id = folder_files.folder_id
            WHERE fo.id = ?;
            """, nativeQuery = true)
        List<File> findByFolderId(String folderId);
}
