package com.saite.voice_notifical.service;

/**
 * 语音通知服务
 *
 * @author liyaqi
 * @date 2024/6/7
 */
public interface VoiceNotifyService {
    /**
     * 发送提醒
     * @author liyaqi
     * @date 2024/6/7
     * @param phoneNumber 号码
     * @param deliveryStation 发货站点
     * @param destinationStation 收货站点
     */
    void sendVoiceNotify(String phoneNumber, String deliveryStation, String destinationStation);

}
