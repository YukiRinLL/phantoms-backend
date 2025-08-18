package com.phantoms.phantomsbackend.repository.primary.onebot;

import com.phantoms.phantomsbackend.pojo.entity.primary.onebot.UserMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserMessageRepository extends JpaRepository<UserMessage, Long> {
}