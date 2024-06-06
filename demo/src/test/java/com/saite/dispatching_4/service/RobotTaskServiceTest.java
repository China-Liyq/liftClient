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
    void simulateRobotTaskV1() {

        LocalDate date = LocalDate.of(2024, 5, 27);
        for (int i = 0; i <= 7; i++) {
            robotTaskService.simulateRobotTask(date.minusDays(i), i + 1);
        }
    }

    @Test
    void simulateRobotTaskV2() {
        LocalDate date = LocalDate.of(2024, 6, 3);
        for (int i = 0; i <= 8; i++) {
            LocalDate minusDays = date.minusDays(i);
            log.info("日期：{}", minusDays);
            robotTaskService.simulateRobotTask(minusDays, i + 1);
        }

    }

    @Test
    void simulateRobotTask2() {
        //模拟2023-xx-x - 2024-xx-xx
        LocalDate startDate = LocalDate.of(2023, 10, 11);
        LocalDate date = LocalDate.of(2024, 5, 19);
        log.info("起始日期：{}", date);
        int i = 1;
        while (!date.isBefore(startDate)) {
            log.info("序号：[{}]日期：{}",i, date);
            robotTaskService.simulateRobotTask(date, i);

            if (i % 14 == 0) {
                log.info("--序号：[{}]日期：{}",i, date);
                i = 0;
            }
            date = date.minusDays(1);
            i++;
        }
        log.info("结束日期：{}", date);
    }
}