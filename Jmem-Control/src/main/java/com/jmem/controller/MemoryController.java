package com.jmem.controller;

import com.jmem.core.MemoryService;
import com.jmem.embedder.Embedder;
import com.jmem.model.Memory;
import com.jmem.model.MemoryScope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/memory")
public class MemoryController {

    private final MemoryService memoryService;
    private final Embedder embedder;

    public MemoryController(MemoryService memoryService, Embedder embedder) {
        this.memoryService = memoryService;
        this.embedder = embedder;
    }

    @GetMapping
    public String listMemories(
            @RequestParam(required = false) MemoryScope scope,
            @RequestParam(required = false) String scopeId,
            @RequestParam(defaultValue = "100") int limit,
            Model model) {

        List<Memory> memories;
        if (scope != null) {
            memories = memoryService.getAll(scope, scopeId);
        } else {
            memories = memoryService.getAll(null, null);
        }

        if (memories.size() > limit) {
            memories = memories.subList(0, limit);
        }

        model.addAttribute("memories", memories);
        model.addAttribute("scopes", MemoryScope.values());
        model.addAttribute("selectedScope", scope);
        model.addAttribute("scopeId", scopeId);
        return "memory/list";
    }

    @GetMapping("/search")
    public String search(
            @RequestParam String query,
            @RequestParam(required = false) MemoryScope scope,
            @RequestParam(defaultValue = "50") int limit,
            Model model) {

        List<Memory> results = memoryService.search(query, scope, limit);
        model.addAttribute("memories", results);
        model.addAttribute("query", query);
        model.addAttribute("scopes", MemoryScope.values());
        model.addAttribute("selectedScope", scope);
        return "memory/search-results";
    }

    @GetMapping("/new")
    public String newMemoryForm(Model model) {
        model.addAttribute("memory", Memory.builder().build());
        model.addAttribute("scopes", MemoryScope.values());
        return "memory/form";
    }

    @PostMapping
    public String createMemory(
            @RequestParam String scope,
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String sessionId,
            @RequestParam(required = false) String agentId,
            @RequestParam String data) {

        Memory memory = Memory.builder()
                .id(UUID.randomUUID().toString())
                .scope(MemoryScope.valueOf(scope))
                .userId(userId)
                .sessionId(sessionId)
                .agentId(agentId)
                .data(data)
                .createdAt(java.time.Instant.now())
                .build();

        memoryService.add(memory);
        return "redirect:/memory";
    }

    @GetMapping("/{id}")
    public String viewMemory(@PathVariable String id, Model model) {
        List<Memory> all = memoryService.getAll(null, null);
        Memory memory = all.stream()
                .filter(m -> m.getId().equals(id))
                .findFirst()
                .orElse(null);
        model.addAttribute("memory", memory);
        return "memory/view";
    }

    @GetMapping("/{id}/edit")
    public String editMemoryForm(@PathVariable String id, Model model) {
        List<Memory> all = memoryService.getAll(null, null);
        Memory memory = all.stream()
                .filter(m -> m.getId().equals(id))
                .findFirst()
                .orElse(null);
        model.addAttribute("memory", memory);
        model.addAttribute("scopes", MemoryScope.values());
        return "memory/form";
    }

    @PostMapping("/{id}")
    public String updateMemory(
            @PathVariable String id,
            @RequestParam String scope,
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String sessionId,
            @RequestParam(required = false) String agentId,
            @RequestParam String data) {

        Memory memory = Memory.builder()
                .id(id)
                .scope(MemoryScope.valueOf(scope))
                .userId(userId)
                .sessionId(sessionId)
                .agentId(agentId)
                .data(data)
                .updatedAt(java.time.Instant.now())
                .build();

        memoryService.update(memory);
        return "redirect:/memory/" + id;
    }

    @PostMapping("/{id}/delete")
    public String deleteMemory(@PathVariable String id) {
        memoryService.delete(id);
        return "redirect:/memory";
    }

    @PostMapping("/reset")
    public String resetScope(
            @RequestParam MemoryScope scope,
            @RequestParam(required = false) String scopeId) {
        memoryService.reset(scope, scopeId);
        return "redirect:/memory";
    }
}
