package com.phantoms.phantomsbackend.controller;

import com.phantoms.phantomsbackend.service.HousingNotifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/housing/notify")
public class HousingNotifyController {

    @Autowired
    private HousingNotifyService housingNotifyService;

    @GetMapping("/targets")
    public ResponseEntity<List<HousingNotifyService.TargetSummary>> getAllTargets() {
        return ResponseEntity.ok(housingNotifyService.getTargetSummaries());
    }

    @GetMapping("/targets/{id}")
    public ResponseEntity<HousingNotifyService.TargetSummary> getTarget(@PathVariable Long id) {
        HousingNotifyService.TargetSummary summary = housingNotifyService.getTargetSummaries().stream()
                .filter(t -> t.getId().equals(id))
                .findFirst()
                .orElse(null);
        if (summary != null) {
            return ResponseEntity.ok(summary);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/targets")
    public ResponseEntity<HousingNotifyService.TargetSummary> createTarget(@RequestBody Map<String, Object> body) {
        String name = (String) body.get("name");
        String description = (String) body.get("description");
        List<String> servers = (List<String>) body.get("servers");
        List<Integer> areas = (List<Integer>) body.get("areas");
        List<String> groups = (List<String>) body.get("groups");
        List<Integer> sizes = (List<Integer>) body.get("sizes");

        if (name == null || name.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        if (servers == null || servers.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        if (groups == null || groups.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        if (areas == null || areas.isEmpty()) {
            areas = List.of(0, 1, 2, 3, 4);
        }
        if (sizes == null || sizes.isEmpty()) {
            sizes = List.of(1, 2); // 默认 M 和 L
        }

        housingNotifyService.createTarget(name, description, servers, areas, groups, sizes);
        
        HousingNotifyService.TargetSummary created = housingNotifyService.getTargetSummaries().stream()
                .filter(t -> t.getName().equals(name))
                .findFirst()
                .orElse(null);
        
        return ResponseEntity.ok(created);
    }

    @PutMapping("/targets/{id}")
    public ResponseEntity<HousingNotifyService.TargetSummary> updateTarget(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body) {
        String name = (String) body.get("name");
        String description = (String) body.get("description");
        List<String> servers = (List<String>) body.get("servers");
        List<Integer> areas = (List<Integer>) body.get("areas");
        List<String> groups = (List<String>) body.get("groups");
        List<Integer> sizes = (List<Integer>) body.get("sizes");

        if (servers == null || servers.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        if (groups == null || groups.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        if (areas == null || areas.isEmpty()) {
            areas = List.of(0, 1, 2, 3, 4);
        }
        if (sizes == null || sizes.isEmpty()) {
            sizes = List.of(1, 2); // 默认 M 和 L
        }

        housingNotifyService.updateTarget(id, name, description, servers, areas, groups, sizes);
        
        HousingNotifyService.TargetSummary updated = housingNotifyService.getTargetSummaries().stream()
                .filter(t -> t.getId().equals(id))
                .findFirst()
                .orElse(null);
        
        if (updated != null) {
            return ResponseEntity.ok(updated);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/targets/{id}")
    public ResponseEntity<Void> deleteTarget(@PathVariable Long id) {
        housingNotifyService.deleteTarget(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/targets/{id}/toggle")
    public ResponseEntity<HousingNotifyService.TargetSummary> toggleTarget(
            @PathVariable Long id,
            @RequestBody Map<String, Boolean> body) {
        Boolean enabled = body.get("enabled");
        if (enabled == null) {
            return ResponseEntity.badRequest().build();
        }

        housingNotifyService.toggleTarget(id, enabled);
        
        HousingNotifyService.TargetSummary updated = housingNotifyService.getTargetSummaries().stream()
                .filter(t -> t.getId().equals(id))
                .findFirst()
                .orElse(null);
        
        if (updated != null) {
            return ResponseEntity.ok(updated);
        }
        return ResponseEntity.notFound().build();
    }
}
