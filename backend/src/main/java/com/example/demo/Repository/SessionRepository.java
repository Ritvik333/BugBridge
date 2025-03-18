package com.example.demo.Repository;
import com.example.demo.Model.Session;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SessionRepository extends JpaRepository<Session, Long> {

    Session findByBugId(Long bugId);
    boolean existsByOwnerIdAndBugId(Long ownerId, Long bugId);
    Session findByOwnerIdAndBugId(Long ownerId, Long bugId);

}
