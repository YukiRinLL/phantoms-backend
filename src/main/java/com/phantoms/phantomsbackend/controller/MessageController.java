package com.phantoms.phantomsbackend.controller;

import com.phantoms.phantomsbackend.pojo.dto.MessageWithUserDTO;
import com.phantoms.phantomsbackend.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@Tag(name = "Message Controller", description = "Provides message management endpoints")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @GetMapping
    @Operation(summary = "Get all messages with user details", description = "Retrieves a list of all messages with user details.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Messages retrieved successfully",
                            content = @Content(schema = @Schema(implementation = MessageWithUserDTO.class), mediaType = "application/json"))
            })
    public ResponseEntity<List<MessageWithUserDTO>> getAllMessagesWithUserDetails() {
        return ResponseEntity.ok(messageService.getAllMessagesWithUserDetails());
    }
}