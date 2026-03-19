package com.jmem.controller;

import com.jmem.core.MemoryService;
import com.jmem.model.MemoryScope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final MemoryService memoryService;

    public DashboardController(MemoryService memoryService) {
        this.memoryService = memoryService;
    }

    @GetMapping("/")
    public String dashboard(Model model) {
        long userCount = memoryService.getAll(MemoryScope.USER, null).size();
        long sessionCount = memoryService.getAll(MemoryScope.SESSION, null).size();
        long agentCount = memoryService.getAll(MemoryScope.AGENT, null).size();

        model.addAttribute("userMemoryCount", userCount);
        model.addAttribute("sessionMemoryCount", sessionCount);
        model.addAttribute("agentMemoryCount", agentCount);
        model.addAttribute("totalMemoryCount", userCount + sessionCount + agentCount);

        return "dashboard";
    }

    @GetMapping("/stats")
    public String stats(Model model) {
        return "stats";
    }
}
