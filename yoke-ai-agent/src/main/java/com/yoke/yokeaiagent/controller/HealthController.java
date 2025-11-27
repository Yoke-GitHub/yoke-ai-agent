package com.yoke.yokeaiagent.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author H
 * @project_name yoke-ai-agent
 * @filename HealthController
 * @created_date 2025/10/26 17:32
 * @description HealthController 健康检查接口
 */
@RestController
@RequestMapping("/health")
public class HealthController {

    @GetMapping
    public String health() {
        return "OK";
    }
}
