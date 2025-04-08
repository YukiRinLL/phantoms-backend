package com.phantoms.phantomsbackend.controller;

import com.phantoms.phantomsbackend.pojo.dto.UserDTO;
import com.phantoms.phantomsbackend.pojo.dto.UserWithAvatarDTO;
import com.phantoms.phantomsbackend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User Controller", description = "Provides user management endpoints")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    @Operation(summary = "Create a new user", description = "Creates a new user with the provided details.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User created successfully",
                            content = @Content(schema = @Schema(implementation = UserDTO.class)))
            })
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(userService.createUser(userDTO));
    }

    @GetMapping
    @Operation(summary = "Get user by ID", description = "Retrieves a user by their unique ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User retrieved successfully",
                            content = @Content(schema = @Schema(implementation = UserDTO.class)))
            })
    public ResponseEntity<UserDTO> getUserById(@RequestBody IdRequest idRequest) {
        return ResponseEntity.ok(userService.getUserById(idRequest.getId()));
    }

    @GetMapping("/all")
    @Operation(summary = "Get all users", description = "Retrieves a list of all users.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Users retrieved successfully",
                            content = @Content(schema = @Schema(implementation = UserDTO.class), mediaType = "application/json"))
            })
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

//    @PutMapping
//    @Operation(summary = "Update user", description = "Updates an existing user with the provided details.",
//            responses = {
//                    @ApiResponse(responseCode = "200", description = "User updated successfully",
//                            content = @Content(schema = @Schema(implementation = UserDTO.class)))
//            })
//    public ResponseEntity<UserDTO> updateUser(@RequestBody UpdateUserRequest updateUserRequest) {
//        return ResponseEntity.ok(userService.updateUser(updateUserRequest.getId(), updateUserRequest.getUserDTO()));
//    }

//    @DeleteMapping
//    @Operation(summary = "Delete user by ID", description = "Deletes a user by their unique ID.",
//            responses = {
//                    @ApiResponse(responseCode = "204", description = "User deleted successfully")
//            })
//    public ResponseEntity<Void> deleteUser(@RequestBody IdRequest idRequest) {
//        userService.deleteUser(idRequest.getId());
//        return ResponseEntity.noContent().build();
//    }

//    @DeleteMapping("/batch")
//    @Operation(summary = "Delete multiple users", description = "Deletes multiple users by their IDs.",
//            responses = {
//                    @ApiResponse(responseCode = "204", description = "Users deleted successfully")
//            })
//    public ResponseEntity<Void> deleteUsers(@RequestBody List<UUID> ids) {
//        userService.deleteUsers(ids);
//        return ResponseEntity.noContent().build();
//    }

    @GetMapping("/with-avatar/{userId}")
    @Operation(summary = "Get user with avatar by userId", description = "Retrieves a user with avatar by their unique userId.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User retrieved successfully",
                            content = @Content(schema = @Schema(implementation = UserWithAvatarDTO.class)))
            })
    public ResponseEntity<UserWithAvatarDTO> getUserWithAvatarByUserId(@PathVariable UUID userId) {
        return ResponseEntity.ok(userService.getUserWithAvatarByUserId(userId));
    }

    @GetMapping("/with-avatar/all")
    @Operation(summary = "Get all users with avatar", description = "Retrieves a list of all users with avatar.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Users retrieved successfully",
                            content = @Content(schema = @Schema(implementation = UserWithAvatarDTO.class), mediaType = "application/json"))
            })
    public ResponseEntity<List<UserWithAvatarDTO>> getAllUsersWithAvatar() {
        return ResponseEntity.ok(userService.getAllUsersWithAvatar());
    }

    @Data
    public static class IdRequest {
        private UUID id;
    }

    @Data
    public static class UpdateUserRequest {
        private UUID id;
        private UserDTO userDTO;
    }
}