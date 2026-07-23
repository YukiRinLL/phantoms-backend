package com.phantoms.phantomsbackend.repository.primary;

import com.phantoms.phantomsbackend.pojo.entity.primary.RisingStonesAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RisingStonesAccountRepository extends JpaRepository<RisingStonesAccount, String> {

    List<RisingStonesAccount> findByEnabledTrue();

    List<RisingStonesAccount> findAllByOrderByCreatedAtDesc();

    Optional<RisingStonesAccount> findByDefaultForApiTrue();

    boolean existsByDefaultForApiTrue();

    void deleteByAccountId(String accountId);
}
