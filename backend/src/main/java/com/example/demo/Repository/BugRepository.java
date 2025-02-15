package com.example.demo.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Sort;

import java.util.List;

import com.example.demo.Model.Bug;
public interface BugRepository extends JpaRepository<Bug, Long> {
        
    @Query("SELECT b FROM Bug b WHERE "
            + "(:severity IS NULL OR b.severity = :severity) AND "
            + "(:status IS NULL OR b.status = :status) AND "
            + "(:creator IS NULL OR b.creator = :creator)")
    List<Bug> findByFilters(
            @Param("severity") String severity,
            @Param("status") String status,
            @Param("creator") String creator,
            Sort sort);
}
