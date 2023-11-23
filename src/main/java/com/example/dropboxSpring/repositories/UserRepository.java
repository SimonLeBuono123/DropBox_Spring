package com.example.dropboxSpring.repositories;

import com.example.dropboxSpring.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
}
