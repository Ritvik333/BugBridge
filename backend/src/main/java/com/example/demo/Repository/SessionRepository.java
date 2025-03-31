package com.example.demo.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.Model.Session;


public interface SessionRepository extends JpaRepository<Session, Long> {

    Session findByBugId(Long bugId);
    boolean existsByOwnerIdAndBugId(Long ownerId, Long bugId);
    Session findByOwnerIdAndBugId(Long ownerId, Long bugId);
}
