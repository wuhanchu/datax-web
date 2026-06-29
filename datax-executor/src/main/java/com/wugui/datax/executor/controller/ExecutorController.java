package com.wugui.datax.executor.controller;

import com.wugui.datax.executor.service.jobhandler.ExecutorJobHandler;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/executor")
public class ExecutorController {

    // 查询当前DataX并发限制和可用许可数，只读不写
    @GetMapping("/maxConcurrent")
    public Map<String, Object> getMaxConcurrent() {
        Map<String, Object> result = new HashMap<>();
        result.put("maxConcurrent", ExecutorJobHandler.getMaxPermits());
        result.put("available", ExecutorJobHandler.getAvailablePermits());
        return result;
    }
}
