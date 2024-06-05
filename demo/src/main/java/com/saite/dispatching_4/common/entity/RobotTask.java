package com.saite.dispatching_4.common.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 机器人任务
 */
@Data
@Accessors(chain = true)
@Entity
@Table(name = "robot_task", indexes = {
        @Index(name = "idx_end_time", columnList = "end_time"),
        @Index(name = "idx_type_status", columnList = "type,status"),
        @Index(name = "idx_create_time", columnList = "create_time"),
        @Index(name = "idx_project_id", columnList = "project_id")
})
public class RobotTask implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

//    @ApiModelProperty(value = "主键", required = false, allowEmptyValue = true)
    @Id
    @GenericGenerator(name = "autoId", strategy = "native")
    @GeneratedValue(generator = "autoId")
    private Integer id;

//    @ApiModelProperty(value = "创建时间", required = false, allowEmptyValue = true)
//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "create_time", columnDefinition = "datetime not null comment '时间'")
    private LocalDateTime createTime;

    @JsonIgnore
    @Column(length = 10)
    private String year;

    @JsonIgnore
    @Column(length = 10)
    private String month;

    @JsonIgnore
    @Column(length = 10)
    private String day;

//    @ApiModelProperty(value = "执行时间", required = true, allowEmptyValue = false)
//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
//    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "exec_time", columnDefinition = "datetime not null comment '时间'")
    private LocalDateTime execTime;

//    @ApiModelProperty(value = "执行订单的机器人", required = false, allowEmptyValue = true)
    @Column(name = "robot_id")
    private Long robotId;

//    @ApiModelProperty(value = "执行订单的机器人序列号", required = false, allowEmptyValue = true)
    @Column(name = "robot_serial")
    private String robotSerial;

    /**
     * 下单人
     */
//    @ApiModelProperty(value = "下单人名称", required = false, allowEmptyValue = true)
    @Column(name = "operator")
    private String operator;

    /**
     * 任务周期
     */
//    @ApiModelProperty(value = "任务周期", required = false, allowEmptyValue = true)
    @Column(name = "task_cycle")
    private String taskCycle;

    /**
     * 车辆任务计划类型
     * 1:立即执行
     * 0:周期作业
     */
//    @ApiModelProperty(value = "车辆任务计划类型，eg:1:立即执行 0:周期作业", required = true, allowEmptyValue = false)
    @Column(name = "plan_type")
    private Integer planType;

    /**
     * 任务结束时间
     */
//    @ApiModelProperty(value = "任务结束时间", required = false, allowEmptyValue = true)
//    @JsonIgnore
//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "end_time" ,columnDefinition = "datetime not null comment '时间'")
    private LocalDateTime endTime;

    /****  注  ： 添加任务类型需到 RobotTaskTypeEnum 枚举添加 *****/
//    @ApiModelProperty(value = "任务类型，eg:1-配送任务 2-清扫任务 3-巡检任务 4-召回任务 5-呼叫任务 6-回待机点 " +
//            "7-充电任务 8-回收任务 9-远程问诊 10-消毒任务 11-分体机器人运送智能柜 12-核源机器人检测任务 " +
//            "13-定时回收任务 15-回收订单；21-样本检测任务,22医废回收任务（机器人上报任务）23医废回收任务（调度下发）,24 气溶胶检测任务（机器人上报），25气溶胶检测任务（调度下发）",
//            required = false, allowEmptyValue = true)
    @Column(name = "type")
    private Integer type;

    /**
     * 未执行-1;排队中-2;已分配-3;正在发货-4;正在送货-5;部分完成-6;已取消-7;已完成-8;到达收货点-9
     * 四柜门机器人 定制：5到达发货点，6离开发货点，8完成订单
     */
//    @ApiModelProperty(value = "任务状态，eg：未执行-1;排队中-2;已分配-3;正在配送-4;到达发货点-5;部分完成-6;" +
//            "已取消-7;已完成-8;到达收货点-9,异常终止-10, 超时-11", required = false, allowEmptyValue = true)
    @Column(name = "status")
    private Integer status;


//    @ApiModelProperty(value = "项目编号", required = true, allowEmptyValue = false)
    @Column(name = "project_id")
    private Long projectId;

//    @ApiModelProperty(value = "下单站点的站点名称", required = false, allowEmptyValue = false)
    @Column(name = "place_station")
    private String placeStation;
//    @ApiModelProperty(value = "下单站点的站点编号", required = true, allowEmptyValue = false)
    @Column(name = "place_station_id")
    private Integer placeStationId;

//    @ApiModelProperty(value = "任务编码", required = true, allowEmptyValue = false)
    @Column(unique = true)
    private String code;

//    @ApiModelProperty(value = "任务名称", required = true, allowEmptyValue = false)
    @Column(name = "name")
    private String name;



    /**
     * 远程问诊时是否取货
     */
//    @ApiModelProperty(value = "远程问诊时是否取货的标志位，远程问诊订单必选", required = true, allowEmptyValue = false)
    @Column(name = "fetch_product")
    private Boolean fetchProduct = false;


    @Column(name = "deleted", columnDefinition = "tinyint not null default 0 comment '是否已删除：0-否 1-是'")
    private int deleted = 0;


    //医废2.0项目新增字段
//    @ApiModelProperty(value = "下单方式，1定时任务，2箱体呼叫，3人工下单,4调度下单,5app下单,6触发装置下单,7HIS系统退药呼叫,8医药箱搬运任务")
    @Column(name = "ordering_method",nullable = false,columnDefinition="int default 0")
    private Integer orderingMethod = 0;

    /*合并污物回收父订单*/
    @Column(name = "merge_parent_task_id")
    private Integer mergeParentTaskId;
    /*已经合并的污物回收订单数*/
    @Column(name = "merged_task")
    private Integer mergedTask;
    /*定时多站点回收任务id*/
    @Column(name = "parent_task_id")
    private Long parentTaskId;
    /**
     * 机器人类型
     */
    @Column(name = "robot_type")
    private Integer robotType;
    //    @ApiModelProperty(value = "是否是预约任务1:是 0:否", required = true, allowEmptyValue = false)
    @Column(name = "scheduled_task")
    private Byte scheduledTask = 0;
    @Column(name = "third_platform_code", columnDefinition = "varchar(64) default '' comment '三方平台的订单,一个订单对应多个robotTask'")
    private String thirdPlatformCode;

    @Column(name = "third_platform_finish_confirm", columnDefinition = "tinyint default 0 comment '三方平台定义的完成流程确定'")
    private Integer thirdPlatformFinishConfirm;

    @Column(name = "sample_task_id", columnDefinition = "int comment '样本呼叫的id'")
    private Integer sampleTaskId;
    @Column(name = "is_disinfect", columnDefinition = "tinyint not null default 0 comment '订单完成后是否消毒(轮椅特有)'")
    private int isDisinfect;
    /**
     * 是否一次性任务？
     */
    @Column(name = "once")
    private Boolean once = true;
    @Column(name = "order_source", columnDefinition = "tinyint not null default 1 comment '订单来源：1-调度系统，2-机器人 '")
    private int orderSource;
    //    @ApiModelProperty(value = "四柜门任务医嘱信息，医嘱摆单号，格式：单号;单号;", required = false,allowEmptyValue = true)
    @Column(name = "remarks", columnDefinition = "varchar(200) default '' comment '备注'")
    private String remarks;

    @Column(name = "sign_status", columnDefinition = "tinyint not null default 0 comment '是否被签收,调度确认签收1,签收屏幕为2'")
    private int signStatus;







}
