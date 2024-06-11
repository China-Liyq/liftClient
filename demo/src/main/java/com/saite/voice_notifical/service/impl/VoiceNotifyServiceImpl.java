package com.saite.voice_notifical.service.impl;

import com.aliyun.dyvmsapi20170525.Client;
import com.aliyun.dyvmsapi20170525.models.SingleCallByVoiceRequest;
import com.aliyun.dyvmsapi20170525.models.SingleCallByVoiceResponse;
import com.aliyun.teaopenapi.models.Config;
import com.aliyun.teautil.models.RuntimeOptions;
import com.saite.voice_notifical.service.VoiceNotifyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 通知服务
 *
 * @author liyaqi
 * @date 2024/6/7
 */
@Slf4j
@Service
public class VoiceNotifyServiceImpl implements VoiceNotifyService {
    @Override
    public void sendVoiceNotify(String phoneNumber, String deliveryStation, String destinationStation) {

        SingleCallByVoiceRequest singleCallByVoiceRequest = new SingleCallByVoiceRequest();
        RuntimeOptions runtimeOptions = new RuntimeOptions();

        Client client = getClient();
        try {
            SingleCallByVoiceResponse resp = client.singleCallByVoiceWithOptions(singleCallByVoiceRequest, runtimeOptions);
            log.info("响应数据：" + resp);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Client getClient() {

        try {
            Config config = new Config();
            config.setAccessKeyId("yourAccessKeyId").setAccessKeySecret("yourAccessKeySecret").setEndpoint("dysmsapi.aliyuncs.com");
            return new Client(config);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
