package com.saite.thread.service.impl;

import com.saite.thread.service.AsyncInvokeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.Future;

/**
 * TODO
 *
 * @author liyaqi
 * @date 2024/5/31
 */
@Slf4j
@Service
public class AsyncInvokeServiceImpl implements AsyncInvokeService {
    @Async("taskExecutors")
    public Future<Boolean> exec1(String name) {
        log.info("子线程 name -->" + Thread.currentThread().getName());
        log.info("name -->{}" + name);
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return new AsyncResult<>(true);
    }

    @Async("taskExecutors")
    public Future<Boolean> exec2(String phone) {
        log.info("子线程 name -->" + Thread.currentThread().getName());
        log.info("name -->{}" + phone);
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return new AsyncResult<>(true);
    }
}
