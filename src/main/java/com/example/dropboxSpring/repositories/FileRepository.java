package com.example.dropboxSpring.repositories;


import com.example.dropboxSpring.models.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Interface for handling all queries of entity model
 * File
 */
@Repository
public interface FileRepository extends JpaRepository<File, UUID> {

}
