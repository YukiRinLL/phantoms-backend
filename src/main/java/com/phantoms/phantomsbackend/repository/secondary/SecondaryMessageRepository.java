package com.phantoms.phantomsbackend.repository.secondary;

import com.phantoms.phantomsbackend.pojo.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository("secondaryMessageRepository")
public interface SecondaryMessageRepository extends JpaRepository<Message, UUID> {
}