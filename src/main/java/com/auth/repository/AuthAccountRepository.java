package com.auth.repository;

import com.auth.enums.AuthProvider;
import com.auth.model.AuthAccount;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthAccountRepository extends JpaRepository<AuthAccount, Long> {

    List<AuthAccount> findByUserId(Long userId);

    Optional<AuthAccount> findByProviderAndProviderAccountId(AuthProvider provider, String providerAccountId);

    boolean existsByProviderAndProviderAccountId(AuthProvider provider, String providerAccountId);
}
