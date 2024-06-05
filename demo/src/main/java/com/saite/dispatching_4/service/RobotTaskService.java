package com.saite.dispatching_4.service;

import com.saite.dispatching_4.common.entity.RobotTask;

import java.time.LocalDate;
import java.util.List;

/**
 * TODO
 *
 * @author liyaqi
 * @date 2024/6/4
 */
public interface RobotTaskService {
    void save(RobotTask robotTask);

    void batchSave(List<RobotTask> list);
    void update(RobotTask robotTask);
    void delete(Long id);
    RobotTask find(Integer id);

    /**
     * 模拟机器人模拟业务
     * @author liyaqi
     * @date 2024/6/4
     * @param date 日期
     */
    void simulateRobotTask(LocalDate date);

}
