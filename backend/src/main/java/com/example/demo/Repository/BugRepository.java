package com.example.demo.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Sort;

import java.util.List;

import com.example.demo.Model.Bug;
public interface BugRepository extends JpaRepository<Bug, Long> {
}
