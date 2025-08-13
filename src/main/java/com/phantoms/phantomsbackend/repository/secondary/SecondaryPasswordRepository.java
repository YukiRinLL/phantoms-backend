package com.phantoms.phantomsbackend.repository.secondary;

import com.phantoms.phantomsbackend.pojo.entity.secondary.Password;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SecondaryPasswordRepository extends JpaRepository<Password, String> {
}