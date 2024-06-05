package com.saite.thread.service.impl;

import com.saite.thread.service.AsyncInvokeService;
import com.saite.thread.service.RunTaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * TODO
 *
 * @author liyaqi
 * @date 2024/5/31
 */
@Slf4j
@Service
public class RunTaskServiceImpl implements RunTaskService {

    @Autowired
    private AsyncInvokeService asyncInvokeService;
    @Override
    public void runTask() {
        Future<Boolean> future1 = asyncInvokeService.exec1("张三");
        Future<Boolean> future2 = asyncInvokeService.exec2("15618881888");

        List<Future<Boolean>> futureList = new ArrayList<>();
        futureList.add(future1);
        futureList.add(future2);
        List<Boolean> list = new ArrayList<>();
        //查询任务执行的结果
        log.info("执行开始22");
        for (int i = 0; i < futureList.size(); i++) {
            Future<Boolean> future = futureList.get(i);
            //CPU高速轮询：每个future都并发轮循，判断完成状态然后获取结果，这一行，是本实现方案的精髓所在。即有10个future在高速轮询，完成一个future的获取结果，就关闭一个轮询
            while (true) {
                //获取future成功完成状态，如果想要限制每个任务的超时时间，取消本行的状态判断+future.get(1000*1, TimeUnit.MILLISECONDS)+catch超时异常使用即可。
                if (future.isDone() && !future.isCancelled()) {
                    //获取结果
                    Boolean result;
                    try {
                        result = future.get();
                    } catch (InterruptedException | ExecutionException e) {
                        throw new RuntimeException(e);
                    }
                    String text = "任务i=" + i + "获取完成!" + new Date();
                    log.info("{}", text);
                    list.add(result);
                    //当前future获取结果完毕，跳出while
                    break;
                } else {
                    try {
                        //每次轮询休息1毫秒（CPU纳秒级），避免CPU高速轮循耗空CPU---》新手别忘记这个
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        log.info("结果：{}",list);
        log.info("执行成功");
    }
}
