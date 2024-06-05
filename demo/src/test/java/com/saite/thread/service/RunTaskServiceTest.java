package com.saite.thread.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 测试
 *
 * @author liyaqi
 * @date 2024/5/31
 */
@SpringBootTest
class RunTaskServiceTest {
    @Autowired
    private RunTaskService runTaskService;

    @Test
    void runTask() {
        runTaskService.runTask();
    }
}