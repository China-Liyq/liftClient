package com.saite.dispatching_4.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.saite.dispatching_4.common.SimulateConstant;
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
    public void simulateRobotTask(LocalDate date, int gap) {
        //当前日期
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = LocalDateTimeUtil.endOfDay(startOfDay);
        RobotTaskRequest request = new RobotTaskRequest()
                .setBeginTime(startOfDay)
                .setEndTime(endOfDay)
                .setNotInTypeList(ListUtil.of(6, 7));
        List<RobotTask> currentRobotTaskList = robotTaskDao.listBy(request);
        int existCount = currentRobotTaskList.size();
        log.info("日期[{}]数据数量：[{}]", date, existCount);
        if (existCount >= SimulateConstant.TASK_COUNT_MIN_PER_DAY) {
            return;
        }

        //每天最低数量,随机60-90
        int minSize = getRandomTaskCount();

        List<RobotTask> needSavaList = new ArrayList<>(minSize);

        LocalDate startDay;
        int i = 1;
        request.setStatusList(ListUtil.of(6,8));
        int randomInt = gap * 30;

        while (existCount < minSize) {
            int minusDays = i + randomInt + 365;
            startDay = date.minusDays(minusDays);
            LocalDateTime oldStartOfDay = startDay.atStartOfDay();
            LocalDateTime oldEndOfDay = LocalDateTimeUtil.endOfDay(oldStartOfDay);
            request.setBeginTime(oldStartOfDay).setEndTime(oldEndOfDay);

            List<RobotTask> recentlyRobotTaskList = robotTaskDao.listBy(request);
            if (CollUtil.isEmpty(recentlyRobotTaskList)) {
                existCount = needSavaList.size() + currentRobotTaskList.size();
                i++;
                continue;
            }
            log.info("----查询数据数量：{}-[{}]", startDay, recentlyRobotTaskList.size());

            String dateStr = LocalDateTimeUtil.format(date, DatePattern.PURE_DATE_PATTERN);

            for (RobotTask robotTask : recentlyRobotTaskList) {
                LocalDateTime createTime = robotTask.getCreateTime();
                LocalDateTime endTime = robotTask.getEndTime();
                String name = robotTask.getName();
                boolean checkName = name.contains("关机点") || name.contains("充电桩") || name.contains("供应中心（住）");
                if (ObjectUtil.isNull(createTime) || ObjectUtil.isNull(endTime) || checkName) {
                    continue;
                }
                createTime = createTime.plusDays(minusDays);
                endTime = endTime.plusDays(minusDays);
                if (createTime.isBefore(startOfDay) || endTime.isAfter(endOfDay)) {
                    continue;
                }

                String robotSerial = robotTask.getRobotSerial();

                List<RobotTask> filterBySn = currentRobotTaskList.stream()
                        .filter(t -> ObjectUtil.equal(t.getRobotSerial(),robotSerial))
                        .collect(Collectors.toList());
                boolean robotTaskCheck = robotTaskCheck(createTime, endTime, filterBySn, needSavaList);
                if (!robotTaskCheck) {
                    continue;
                }

                //时间处理
                createTime = getRandomTime(createTime);
                endTime = getRandomTime(endTime);
                if (!createTime.isBefore(endTime)) {
                    continue;
                }
//                String[] split = code.split(StrUtil.DASHED);
//                String sn = split[split.length - 1];
//                int beginIndex = 4;
//                int endIndex = Math.min(sn.length(), 13);
//
//                String newSn;
//                String simStr = SimulateConstant.SIM_STR;
//                if (sn.contains(simStr)) {
//                    newSn = dateStr + simStr + sn.replace(simStr, "").substring(beginIndex, endIndex);
//                } else {
//                    newSn =  dateStr + simStr + sn.substring(beginIndex, endIndex);
//                }
//                code = code.replace(sn, newSn) + getRandomNumber(4);
                String code = robotTask.getCode();
                String newCode = robotTaskCodeHandler(code, dateStr);

                robotTask
                        .setId(null)
                        .setCode(newCode)
                        .setCreateTime(createTime)
                        .setExecTime(createTime)
                        .setEndTime(endTime)
//                        .setDay(String.valueOf(createTime.getDayOfMonth()))
                        .setDay(getTwoDigit(createTime.getDayOfMonth()))
//                        .setMonth(String.valueOf(createTime.getMonthValue()))
                        .setMonth(getTwoDigit(createTime.getMonthValue()))
                        .setYear(String.valueOf(createTime.getYear()))
                        .setStatus(8);
                needSavaList.add(robotTask);
            }
            existCount = needSavaList.size() + currentRobotTaskList.size();
            i++;
        }
        log.info("日期[{}]新增数据：{}", date, needSavaList.size());
        if (CollUtil.isEmpty(needSavaList)) {
            return;
        }
        log.info("开始保存");
        this.batchSave(needSavaList);
        log.info("保存结束");
    }

    private String robotTaskCodeHandler(String code,  String dateStr) {
        String[] split = code.split(StrUtil.DASHED);
        String sn = split[split.length - 1];
        int beginIndex = 4;
        int endIndex = Math.min(sn.length(), 13);

        String newSn;
        String simStr = SimulateConstant.SIM_STR;
        if (sn.contains(simStr)) {
            newSn = dateStr + simStr + sn.replace(simStr, "").substring(beginIndex, endIndex);
        } else {
            newSn =  dateStr + simStr + sn.substring(beginIndex, endIndex);
        }
        return code.replace(sn, newSn) + getRandomNumber(4);

    }

    private String getTwoDigit(int num) {
        return String.format("%02d", num);
    }

    private LocalDateTime getRandomTime(LocalDateTime time) {
        int robotCount = getRobotCount();
        if (robotCount >= 0) {
            return time.plusSeconds(robotCount);
        }
        return time.minusSeconds(Math.abs(robotCount));
    }

    private int getRandomTaskCount() {
        return RandomUtil.randomInt(0, 30) + 60;
    }


    private int getRobotCount() {
        return RandomUtil.randomInt(0, 21) - 10;
    }

    private String getRandomNumber(Integer size) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < size; i++) {
            s.append(9);
        }
        int randomInt = RandomUtil.randomInt(Integer.parseInt(s.toString()));
        String format = "%0" + size + "d";
        return String.format(format, randomInt);
    }

    public static void main(String[] args) {
        String s = "2024052008250009";
        String s2 = "202405280709074613250";
        String s3 = "20200810155759317";
        System.out.println(s3.length());

        System.out.println(s3.substring(4, 17));

//        System.out.println(s.substring(4));
//        int size = 4;
//        for (int i = 0; i < 1000; i++) {
//            String randomNumber = getRandomNumber(4);
//            if (randomNumber.length() < size) {
//                System.out.println("异常数据：" + randomNumber);
//                break;
//            }
//        }
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
