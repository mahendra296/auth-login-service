package com.auth.repository;

import com.auth.enums.AuthProvider;
import com.auth.model.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query(
            "SELECT u FROM User u LEFT JOIN FETCH u.authAccounts a WHERE u.email = :email AND (a IS NULL OR a.provider = :provider)")
    Optional<User> findByEmailWithAuthProvider(@Param("email") String email, @Param("provider") AuthProvider provider);
}
