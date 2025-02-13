package com.example.demo.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.example.demo.Model.Bug;
public interface BugRepository extends JpaRepository<Bug, Long>, JpaSpecificationExecutor<Bug> {}
