package com.saite.dispatching_4.dao;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.ObjectUtil;
import com.saite.dispatching_4.common.base.BaseDao;
import com.saite.dispatching_4.common.bean.request.RobotTaskRequest;
import com.saite.dispatching_4.common.entity.RobotTask;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 机器人任务操作层
 * @author liyaqi
 * @date 2024/6/4
 */
@Repository
public class RobotTaskDao extends BaseDao<RobotTask, Integer> {

    public List<RobotTask> listBy(RobotTaskRequest request) {
        StringBuilder sql = new StringBuilder(128);
        Map<String, Object> args = new HashMap<>(4);
        sql.append("select * from robot_task where 1 = 1");
        LocalDateTime beginTime = request.getBeginTime();
        if (ObjectUtil.isNotNull(beginTime)) {
            sql.append(" and create_time >= :beginTime");
            args.put("beginTime", beginTime);
        }
        LocalDateTime endTime = request.getEndTime();
        if (ObjectUtil.isNotNull(endTime)) {
            sql.append(" and create_time <= :endTime");
            args.put("endTime" ,endTime);
        }
        List<Integer> statusList = request.getStatusList();
        if (CollUtil.isNotEmpty(statusList)) {
            sql.append(" and status in :statusList");
            args.put("statusList", statusList);
        }

        List<Integer> typeList = request.getTypeList();
        if (CollUtil.isNotEmpty(typeList)) {
            sql.append(" and type in :typeList");
            args.put("typeList", typeList);
        }
        List<Integer> notInTypeList = request.getNotInTypeList();
        if (CollUtil.isNotEmpty(notInTypeList)) {
            sql.append(" and type not in :notInTypeList");
            args.put("notInTypeList", notInTypeList);
        }
        return findBySql(sql.toString(), args);
    }

}
