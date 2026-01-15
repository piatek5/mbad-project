package com.example.mbad.project.repository;

import com.example.mbad.project.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    @Query("SELECT u FROM User u WHERE " +
            "(lower(u.username) LIKE lower(concat('%', :username, '%')) OR :username IS NULL) AND " +
            "(lower(u.email) LIKE lower(concat('%', :email, '%')) OR :email IS NULL) AND " +
            "(u.phone LIKE concat('%', :phone, '%') OR :phone IS NULL)")
    List<User> searchUsers(@Param("username") String username,
                           @Param("email") String email,
                           @Param("phone") String phone);
}
