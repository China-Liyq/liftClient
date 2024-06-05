package com.saite.dispatching_4.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.saite.dispatching_4.common.bean.request.RobotTaskRequest;
import com.saite.dispatching_4.common.entity.RobotTask;
import com.saite.dispatching_4.dao.RobotTaskDao;
import com.saite.dispatching_4.service.RobotTaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 机器人任务
 * @author liyaqi
 * @date 2024/6/4
 */
@Slf4j
@Service
public class RobotTaskServiceImpl implements RobotTaskService {
    @Autowired
    private RobotTaskDao robotTaskDao;
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(RobotTask robotTask) {
        robotTaskDao.save(robotTask);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchSave(List<RobotTask> list) {
        robotTaskDao.batchSave(list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(RobotTask robotTask) {
        robotTaskDao.update(robotTask);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {

    }

    @Override
    public RobotTask find(Integer id) {
        return robotTaskDao.find(id);
    }

    @Override
    @Transactional(rollbackFor =Exception.class)
    public void simulateRobotTask(LocalDate date) {
        //当前日期
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = LocalDateTimeUtil.endOfDay(startOfDay);
        RobotTaskRequest request = new RobotTaskRequest()
                .setBeginTime(startOfDay)
                .setEndTime(endOfDay)
                .setNotInTypeList(ListUtil.of(6, 7));
        List<RobotTask> currentRobotTaskList = robotTaskDao.listBy(request);
        log.info("数据数量：[{}]-[{}]", date, currentRobotTaskList.size());

        //查询最近几天前的数据 默认5天前
        int minSize = 75;
        List<RobotTask> needSavaList = new ArrayList<>(minSize + currentRobotTaskList.size());
        int count = currentRobotTaskList.size();

        LocalDate startDay;
        int i = 1;

        while (count < minSize) {
            startDay = date.minusDays(i);
            LocalDateTime oldStartOfDay = startDay.atStartOfDay();
            LocalDateTime oldEndOfDay = LocalDateTimeUtil.endOfDay(startOfDay);
            request.setBeginTime(oldStartOfDay).setEndTime(oldEndOfDay);

            List<RobotTask> recentlyRobotTaskList = robotTaskDao.listBy(request);
            log.info("数据数量：{}-[{}]", startDay, currentRobotTaskList.size());

            String dateStr = LocalDateTimeUtil.format(date, DatePattern.PURE_DATE_PATTERN);

            for (RobotTask robotTask : recentlyRobotTaskList) {
                LocalDateTime createTime = robotTask.getCreateTime();
                LocalDateTime endTime = robotTask.getEndTime();
                String name = robotTask.getName();
                if (ObjectUtil.isNull(createTime) || ObjectUtil.isNull(endTime) || "关机点".equals(name) || "供应中心（住）".equals(name)) {
                    continue;
                }
                createTime = createTime.plusDays(i);
                endTime = endTime.plusDays(i);
                if (createTime.isBefore(startOfDay) || endTime.isAfter(endOfDay)) {
                    continue;
                }
                String robotSerial = robotTask.getRobotSerial();

                List<RobotTask> filterBySn = currentRobotTaskList.stream().filter(t -> t.getRobotSerial().equals(robotSerial)).collect(Collectors.toList());
                boolean robotTaskCheck = robotTaskCheck(createTime, endTime, filterBySn, needSavaList);
                if (!robotTaskCheck) {
                    continue;
                }

                String code = robotTask.getCode();
                String[] split = code.split(StrUtil.DASHED);
                String sn = split[split.length - 1];
                code = code.replace(sn, dateStr + sn.substring(4));


                robotTask
                        .setId(null)
                        .setCode(code)
                        .setCreateTime(createTime)
                        .setExecTime(createTime)
                        .setEndTime(endTime)
                        .setDay(String.valueOf(createTime.getDayOfMonth()))
                        .setMonth(String.valueOf(createTime.getMonthValue()))
                        .setYear(String.valueOf(createTime.getYear()))
                        .setStatus(8);
                needSavaList.add(robotTask);
            }
            count = needSavaList.size() + currentRobotTaskList.size();
            i++;
        }
        log.info("新增数据：{}",needSavaList.size());
        if (CollUtil.isEmpty(needSavaList)) {
            return;
        }
        this.batchSave(needSavaList);
    }

    public static void main(String[] args) {
        String s = "2024052008250009";

        System.out.println(s.substring(4));
    }

    private boolean robotTaskCheck( LocalDateTime createTime, LocalDateTime endTime, List<RobotTask> filterBySn, List<RobotTask> needSavaList) {
        if (CollUtil.isNotEmpty(filterBySn)) {
            for (RobotTask robotTask : filterBySn) {
                LocalDateTime createTime1 = robotTask.getCreateTime();
                LocalDateTime endTime1 = robotTask.getEndTime();
                if (ObjectUtil.isNull(createTime1)|| ObjectUtil.isNull(endTime1)) {
                    continue;
                }
                boolean checkTimeRange = createTime.isAfter(endTime1) || endTime.isBefore(createTime1);
                if (!checkTimeRange) {
                    return false;
                }
            }
        }
        if (CollUtil.isEmpty(needSavaList)) {
            return true;
        }
        for (RobotTask robotTask : needSavaList) {
            LocalDateTime createTime1 = robotTask.getCreateTime();
            LocalDateTime endTime1 = robotTask.getEndTime();
            if (ObjectUtil.isNull(createTime1)|| ObjectUtil.isNull(endTime1)) {
                continue;
            }
            boolean checkTimeRange = createTime.isAfter(endTime1) || endTime.isBefore(createTime1);
            if (!checkTimeRange) {
                return false;
            }
        }
        return true;
    }


}
