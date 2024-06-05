package com.saite.thread.service;

import java.util.concurrent.Future;

/**
 * 异步任务
 * @author liyaqi
 * @date 2024/5/31
 */

public interface AsyncInvokeService {

    Future<Boolean> exec1(String name);
    Future<Boolean> exec2(String name);
}
