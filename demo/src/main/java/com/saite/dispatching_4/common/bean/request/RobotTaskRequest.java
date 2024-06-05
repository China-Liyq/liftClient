package com.saite.dispatching_4.common.bean.request;

import cn.hutool.core.date.DatePattern;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 机器人任务请求
 *
 * @author liyaqi
 * @date 2024/6/4
 */
@Data
@Accessors(chain = true)
public class RobotTaskRequest {


    /**开始时间*/
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime beginTime;
    /**结束时间*/
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime endTime;

    private List<Integer> typeList;

    private List<Integer> notInTypeList;

    private List<Integer> statusList;
}
