package com.saite.dispatching_4.dao;

import com.saite.dispatching_4.common.entity.RobotTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public interface RobotTaskRepository extends JpaRepository<RobotTask, Integer>, JpaSpecificationExecutor {

    /**
     * 查询排队中的消毒任务
     *
     * @param type
     * @param robotId
     * @return
     */
    @Query(value = "select count(id) from robot_task where status = 2 and type = :type and project_id = :robotId", nativeQuery = true)
    Integer findTaskCount(Integer type, Long robotId);

    /**
     * 项目中执行任务次数
     */
    @Query(value = "select project_id, count(id), SUBSTRING(end_time, 1, 13) as time,robot_id " +
            " from robot_task where end_time between str_to_date(:start,'%Y-%m-%d %T') and str_to_date(:end,'%Y-%m-%d %T') " +
            " and (status = 6 or status = 8) and (type = 1 or type = 8) " +
            " and merge_parent_task_id is null and parent_task_id is null " +
            " group by project_id,time,robot_id ", nativeQuery = true)
    List<Object[]> taskTimesAfterTime(String start, String end);

    @Query(value = "select project_id, count(id), SUBSTRING(end_time, 1, 13) as time,robot_id " +
            " from robot_task where end_time between str_to_date(:start,'%Y-%m-%d %T') and str_to_date(:end,'%Y-%m-%d %T') " +
            " and project_id = :projectId " +
            " and (status = 6 or status = 8) and (type = 1 or type = 8) " +
            " and merge_parent_task_id is null and parent_task_id is null " +
            " group by time,robot_id ", nativeQuery = true)
    List<Object[]> taskTimesAfterTime(Long projectId, String start, String end);

    /**
     * 根据车辆id查询当天任务
     *
     * @param robotId
     */
    @Query(value = "select * from robot_task where robot_id=? to_days(create_time between concat(curdate(),'00:00:00') and concat(curdate(),'23:59:59'))", nativeQuery = true)
    List<RobotTask> getTaskByToday(Long robotId);

    @Query(value = "select * from robot_task where robot_id = ? and status not in (6,7,8,9,10,11) order by exec_time desc limit 1", nativeQuery = true)
    RobotTask findCurrentTaskByRobotId(Long robotId);

    @Query(value = "select * from robot_task where project_id = :projectId" +
            " and create_time between :startDate and :endDate" +
            " and status not in (6,7,8,9,10,11) order by create_time desc limit 10", nativeQuery = true)
    List<RobotTask> findByProjectIdAndCreateTimeBetween(Long projectId, @Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Query(value = "select count(*) from robot_task where project_id = :projectId" +
            " and type != 7 and create_time between :startDate and :endDate", nativeQuery = true)
    Integer countByProjectIdAndCreateTimeBetween(Long projectId, @Param("startDate") Date startDate, @Param("endDate") Date endDate);

    /**
     * 车辆id与计划类型查询任务
     *
     * @param robotId
     * @param planTypeId
     */
    List<RobotTask> findByRobotIdAndPlanType(Long robotId, Integer planTypeId);

    /**
     * 某项目的订单总次数
     */
    @Query(value = "select count(id) from robot_task " +
            " where project_id = :projectId " +
            " and (type = 1 or type = 8 or type = 12) and (status = 6 or status = 8)", nativeQuery = true)
    Integer allTimesForProject(long projectId);

    /**
     * 机器人报表-某机器人执行任务的次数及药品件数
     */
    @Query(value = "select t.robot_id,t.project_id,sum(rpe.num) from robot_task as t join robot_task_entry as re on re.robot_task_id=t.id join robot_task_product_entry as rpe on rpe.task_entry_id=re.id where t.create_time > :start group by t.id", nativeQuery = true)
    List<Object[]> getTaskTimes(String start);

    /**
     * 查询正在配送的任务
     */
    @Query(value = "select * from robot_task where id in (SELECT max(id) FROM `robot_task` where project_id=:projectId and status in (2,3,4) and type=1 group by robot_id) order by create_time desc", nativeQuery = true)
    List<RobotTask> deliveringList(long projectId);

    @Query(value = "select distinct rt.* from robot_task as rt left join robot_task_entry as rte on rt.id = rte.robot_task_id where rt.project_id=:projectId and rt.status in (2,3,4) and rt.type=1 and rt.create_time > date_sub(now(), interval 60 minute) and (rte.station_id = :stationId or rt.operator =:userId) order by rt.create_time desc;", nativeQuery = true)
    List<RobotTask> deliveringListByStation(long projectId, Integer stationId, String userId);


    /**
     * 查询正在执行的远程问诊订单
     */
    @Query(value = "select * from robot_task where id in (SELECT max(id) FROM `robot_task` where project_id=:projectId and status in (2,3,4) and type=9 group by robot_id)", nativeQuery = true)
    List<RobotTask> remoteTreatmentdoing(long projectId);

    /**
     * 根据状态查询任务
     */
    @Query(value = "select * from robot_task where status=:status", nativeQuery = true)
    List<RobotTask> findAllByOrderStatus(Integer status);

    @Query(value = "select * from robot_task where code=:code", nativeQuery = true)
    RobotTask findByCode(String code);

    /*
     * 功能描述 : 查询昨天配送的站点及配送次数
     * @author lin liangwei
     * @date 2020/9/26
     * @params  * @param projectId
     * @return java.util.List<java.lang.Object[]>
     */
    @Query(value = "select date_format(t.create_time,'%Y-%m-%d') as recordTime,re.station_id,count(t.id) as mcount,s.name,t.project_id " +
            "from robot_task as t join robot_task_entry as re on re.robot_task_id=t.id join station as s on re.station_id=s.id  " +
            "where t.create_time>=date_add(curdate(), interval - 1 day) \n" +
            "and t.create_time<curdate()  group by recordTime, t.project_id, re.station_id, s.name ", nativeQuery = true)
    List<Object[]> getYesterdayDeliverStation();

    @Query(value = "select t.operator from robot_task_entry as rt join robot_task as t on rt.robot_task_id=t.id where rt.station_id=:stationId and t.status in (6,8) and" +
            " t.project_id=:projectId and t.create_time>=date_add(curdate(), interval - 1 day) \n" +
            "and t.create_time<curdate()  group by t.project_id,rt.station_id,t.operator" +
            " order by count(t.operator) desc limit 1", nativeQuery = true)
    String getStationMostUser(long projectId, Integer stationId);

    @Query(value = "select pr.name from robot_task_entry as t  join robot_task_product_entry as re on re.task_entry_id = t.id  left join product as pr on re.product_id = pr.id \n" +
            "where t.project_id= :projectId and t.create_time>=date_add(curdate(), interval - 1 day) \n" +
            "and t.create_time<curdate()  and t.station_id = :stationId\n" +
            "group by re.product_id order by sum(num) desc limit 1", nativeQuery = true)
    String getStationMostProduct(long projectId, Integer stationId);

    @Query(value = "select sum(re.num) as num from robot_task_entry as t  join robot_task_product_entry as re on re.task_entry_id = t.id  \n" +
            "where t.project_id= :projectId and t.create_time>=date_add(curdate(), interval - 1 day) \n" +
            "and t.create_time<curdate()  and t.station_id = :stationId\n" +
            "group by re.product_id order by num desc limit 1", nativeQuery = true)
    Integer getStationProductCount(long projectId, Integer stationId);


    @Query(value = "select date_format(t.create_time,'%Y-%m-%d') as recordTime,t.robot_id,count(t.id) as mcount, " +
            "r.serial_number,t.project_id,r.type " +
            "from robot_task as t left join delivery_robot as r on t.robot_id = r.id " +
            "where t.create_time>=date_add(curdate(), interval - 1 day) " +
            "and t.create_time<curdate() and t.status in (6,8) " +
            "group by t.project_id,t.robot_id,recordTime,r.type,r.serial_number", nativeQuery = true)
    List<Object[]> getYesterdayRobotDeliverData();


    @Query(value = "select taskId from robot_task t where t.robot_id =:robotId " +
            "and t.create_time>=date_add(curdate(), interval - 1 day) " +
            "and t.create_time<curdate() and t.status in (6,8) ", nativeQuery = true)
    List<Integer> getYesterdayTaskIdByRobotId(Long robotId);


    @Query(value = "select sum(rpe.num) as num from robot_task as t \n" +
            "join robot_task_entry as re on re.robot_task_id=t.id \n" +
            "join robot_task_product_entry as rpe on rpe.task_entry_id=re.id   \n" +
            "where t.project_id= :projectId and t.create_time>=date_add(curdate(), interval - 1 day) \n" +
            "and t.create_time<curdate()  and t.robot_id = :robotId\n" +
            "group by rpe.product_id order by num desc limit 1", nativeQuery = true)
    Integer getRobotProductCount(long projectId, Long robotId);


    @Query(value = "select t.station_id\n" +
            "from robot_task_entry as t \n" +
            "join robot_task_product_entry as re on re.task_entry_id = t.id and t.station_type = 2 \n" +
            "where  t.create_time>=date_add(curdate(), interval - 1 day)  \n" +
            "and t.create_time<curdate() and re.product_id = :productId\n" +
            "group by t.station_id order by num desc limit 1", nativeQuery = true)
    Integer getProductMostStation(long productId);

    @Query(value = "select sum(re.num)\n" +
            "from robot_task_entry as t \n" +
            "join robot_task_product_entry as re on re.task_entry_id = t.id \n" +
            "where  t.create_time>=date_add(curdate(), interval - 1 day) \n" +
            "and t.create_time<curdate()  and re.product_id = :productId", nativeQuery = true)
    Integer getProductCount(long productId);

    @Query(value = "select sum(re.num)\n" +
            "from robot_task_entry as t \n" +
            "join robot_task_product_entry as re on re.task_entry_id = t.id \n" +
            "where  t.create_time>=date_add(curdate(), interval - 1 day) \n" +
            "and t.create_time<curdate()  and t.station_id = :stationId", nativeQuery = true)
    Integer getStationProductCount(Integer stationId);

    /**
     * 查询重复的呼叫任务
     *
     * @param dest
     * @return
     */
    @Query(value = "SELECT rt.* FROM robot.robot_task as rt\n" +
            "left join robot.robot_task_entry as rte on rt.id = rte.robot_task_id \n" +
            "where rt.type = 5 and rte.station_id =:dest and  rte.station_type = 31 and rte.status in (1,2) " +
            "and rt.create_time > date_sub(now(), interval :timeout minute) order by create_time desc limit 1", nativeQuery = true)
    RobotTask findRepeatCallTask(Integer dest, int timeout);

    @Query(value = "SELECT rt.* FROM robot.robot_task as rt\n" +
            "left join robot.robot_task_entry as rte on rt.id = rte.robot_task_id \n" +
            "where rt.type = 55 and rte.station_id =:stationId and  rte.station_type = 1 and rte.status in (1,2) " +
            "and rt.create_time > date_sub(now(), interval :timeout minute) order by create_time desc limit 1", nativeQuery = true)
    RobotTask findRepeatMedicalBoxTask(Integer stationId,int timeout);

    /**
     * 功能描述 : 查询重复的任务
     *
     * @param origins 起点
     * @param destins 目标点
     * @param type
     * @return com.saite.robot.entity.RobotTask
     * @author lin liangwei
     * @date 2020/11/16
     */
    @Query(value = "SELECT rt.* \n" +
            "FROM robot.robot_task AS rt \n" +
            "LEFT JOIN robot.robot_task_entry AS rte ON rt.id = rte.robot_task_id  \n" +
            "WHERE rt.type = :type \n" +
            "AND rt.status in (2,3,4,5,9) AND rt.create_time > date_sub(now(), interval :timeout minute)\n" +
            "AND EXISTS(SELECT 1 FROM robot.robot_task_entry rte1 \n" +
            "WHERE rte1.station_id in(:origins) and rte1.station_type = 1\n" +
            "AND rte1.robot_task_id = rte.robot_task_id)\n" +
            "AND EXISTS(SELECT 1 FROM robot.robot_task_entry rte2 \n" +
            "WHERE rte2.station_id in(:destins) and rte2.station_type = 2\n" +
            "AND rte2.robot_task_id = rte.robot_task_id)\n" +
            "ORDER BY create_time DESC limit 1", nativeQuery = true)
    RobotTask findRepeatTask(String origins, String destins, Integer type, int timeout);

    /**
     * 功能描述 : 取消机器人未完成的任务
     *
     * @return void
     * @author lin liangwei
     * @date 2020/11/25
     * @params * @param robotId 机器人编号
     */
    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value = "update robot_task set status = 7 where robot_id =:robotId and status in(1,2,3,4,5) and create_time > date_sub(now(), interval 60 minute)", nativeQuery = true)
    void cancelUnFinishTaskByRobotId(Long robotId);

    @Query(value = "SELECT * FROM `robot_task` where robot_id=:robotId and type = 5 order by id desc limit 1", nativeQuery = true)
    RobotTask findCallingOrderByRobot(long robotId);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value = "update robot_task set status = :status,end_time=:date where id=:id", nativeQuery = true)
    void finished(Date date, int id, int status);

    /**
     * 查找定时污物回收的子订单
     *
     * @param parentTaskId 父订单id
     * @return
     */
    List<RobotTask> findByParentTaskId(long parentTaskId);

    /**
     * 查找普通污物回收的子订单
     *
     * @param parentTaskId 父订单id
     * @return
     */
    List<RobotTask> findByMergeParentTaskId(int parentTaskId);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value = "update robot_task set status=10,end_time=now() " +
            " where TIMESTAMPDIFF(HOUR, create_time, now()) > 12 " +
            " and type != 28 and status not in (6,7,8,9) " +
            " and merge_parent_task_id is null and parent_task_id is null", nativeQuery = true)
    int updateWrongOrder();

    /*
     * 根据项目查找正在执行的指定类型任务
     */
    @Query(value = "SELECT rt.* FROM delivery_robot as dr left join robot_task as rt on dr.task = rt.id where rt.status between 3 and 7 and rt.type = :type and rt.project_id = :projectId", nativeQuery = true)
    List<RobotTask> findExecutingTask(Long projectId, Integer type);

    @Query(value = "select ifnull(sum(spary),0) as totalSpray,ifnull(sum(spray_capacity),0) as sprayCapacity,ifnull(sum(uv_light),0) as totalUVLight,count(id) as disinfectStationCount " +
            "from robot_task_entry where robot_task_id in :taskIds and status = 4 ", nativeQuery = true)
    Map<String, Object> getStatisticsForRobotType3(List<Integer> taskIds);
    
    @Query(value = "select * from robot_task where type=14 and robot_id=? order by create_time desc limit 1", nativeQuery = true)
    RobotTask findLatestTask(long robotId,int type);

    List<RobotTask> findByThirdPlatformCodeIn(Collection<String> codes);

    List<RobotTask> findByThirdPlatformCode(String thirdPlatformCode);


    RobotTask findFirstByRobotIdOrderByCreateTimeDesc(Long robotId);

    @Query(value = "select count(*) from robot.robot_task where robot_task.third_platform_code = :thirdPlatformCode", nativeQuery = true)
    int countByThirdPlatformCode(String thirdPlatformCode);

    /**
     * 查询重复的呼叫任务
     *
     * @param dest
     * @return
     */
    @Query(value = "SELECT rt.* FROM robot.robot_task as rt\n" +
            "left join robot.robot_task_entry as rte on rt.id = rte.robot_task_id \n" +
            "where rt.type = 23 and rte.station_id =:dest and  rte.station_type = 1 and rt.status in (1,2) and rt.create_time > date_sub(now(), interval 60 minute) order by create_time desc limit 1", nativeQuery = true)
    RobotTask findRepeatMedicalWasteCallTask(Integer dest);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value = "update robot_task set remarks =:remarksStr where id =:taskId", nativeQuery = true)
    void saveRemarks(Integer taskId, String remarksStr);

    @Query(value = "select  rt.* from robot_task as rt left join delivery_robot as dr on rt.id = dr.task where dr.project_id=:projectId ", nativeQuery = true)
    List<RobotTask> findAllRobotExecutingByProjectId(Long projectId);


    @Query(value = "select * from robot_task where id =:id ", nativeQuery = true)
    RobotTask findByRobotTaskId(Integer id);

    @Query(value = "select * from robot_task where project_id =:projectId and type =:type and status in (:statusList)", nativeQuery = true)
    List<RobotTask> findTaskByTypeAndStatus(Long projectId, int type, String statusList);

    /**
     * 查询重复的触发装置呼叫任务
     *
     * @param origin
     * @return
     */
    @Query(value = "SELECT rt.* FROM robot.robot_task as rt\n" +
            "left join robot.robot_task_entry as rte on rt.id = rte.robot_task_id \n" +
            "where (rt.type = 31 or rt.type = 32) and rte.station_id =:origin  and rte.status in (1,2) and rt.create_time > date_sub(now(), interval 60 minute) order by create_time desc limit 1", nativeQuery = true)
    RobotTask findRepeatITDCallTask(Integer origin);

    @Query(value = "select * from robot_task where remarks =:orderId and project_id=:projectId order by create_time desc limit 1", nativeQuery = true)
    RobotTask findByRemarksAndProjectId(String orderId,Long projectId);

    @Query(value = "select * from robot_task where robot_id =:robotId and  status in (3,4,5,6)  order by create_time desc limit 1", nativeQuery = true)
    RobotTask findRobotExecutingByRobotId(Long robotId);

    @Query(value = "select * from robot_task where robot_id =:robotId  and project_id=:projectId and status = 11 order by create_time desc limit 1", nativeQuery = true)
    RobotTask findWaitingForBindingTask(Long robotId, Long projectId);

    @Query(value = "select * from robot_task where remarks =:deviceId  and project_id=:projectId and type = :orderType  order by create_time desc limit 1", nativeQuery = true)
    RobotTask findQueueTaskByRemarks(Long projectId, int orderType, String deviceId);

    @Query(value = "select rt.* from robot_task as rt left join robot_task_queue as rtq on rt.id = rtq.task_id where rt.third_platform_code =:code  and rt.project_id=:projectId and rtq.execute_flag = 0 ", nativeQuery = true)
    List<RobotTask>  getUnExecTaskByProjectAndType(long projectId, Integer code);
}
