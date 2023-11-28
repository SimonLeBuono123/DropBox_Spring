package com.example.dropboxSpring.repositories;

import com.example.dropboxSpring.models.Folder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface FolderRepository extends JpaRepository<Folder, UUID> {
    @Query(value = """
            SELECT fo.id, fo.name, fo.user_id FROM folder_files LEFT JOIN files f ON f.id = folder_files.files_id
            LEFT JOIN folder fo ON fo.id = folder_files.folder_id
            WHERE files = ?;
            """, nativeQuery = true)
    Optional<Folder> findFolderByFileId(UUID fileId);
}
