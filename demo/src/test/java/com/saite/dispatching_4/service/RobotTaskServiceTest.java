package com.saite.dispatching_4.service;

import com.saite.dispatching_4.common.entity.RobotTask;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 机器人任务测试
 *
 * @author liyaqi
 * @date 2024/6/4
 */
@Slf4j
@SpringBootTest
class RobotTaskServiceTest {

    @Autowired
    private RobotTaskService robotTaskService;

    @Test
    void save() {
    }

    @Test
    void update() {
    }

    @Test
    void delete() {
    }

    @Test
    void find() {
        RobotTask robotTask = robotTaskService.find(111);
        log.info("查询数据：{}" ,robotTask);
    }

    @Test
    void simulateRobotTask() {
        LocalDate date = LocalDate.of(2024, 5, 20);
        for (int i = 0; i < 10; i++) {
            robotTaskService.simulateRobotTask(date.plusDays(i));
        }

    }
}