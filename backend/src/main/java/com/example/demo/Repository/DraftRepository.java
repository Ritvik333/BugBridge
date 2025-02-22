package com.example.demo.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.Model.Bug;
import com.example.demo.Model.Draft;
import com.example.demo.Model.User;

@Repository
public interface DraftRepository extends JpaRepository<Draft, Long> {
    List<Draft> findByUserAndBug(User user, Bug bug);
    List<Draft> findByUser(User user);
}
