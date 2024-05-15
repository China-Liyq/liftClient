package com.saite.lift.common.bean;

import com.saite.lift.common.util.ConversionUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;

/**
 * @author llw
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SaiteLiftProtocol {
    /**
     * 消息头
     */
    private byte header1;
    private byte header2;
    /**
     * 数据长度
     */
    private byte[] length;
    /**
     * 源地址
     */
    private byte  sourceAddress;
    /**
     * 目的地址
     */
    private byte destinationAddress;

    /**
     * 帧类型
     */
    private byte frameType;

    /**
     * 功能码
     */
    private byte functionCode;

    /**
     * 帧编号
     */
    private byte frameSerial;

    /**
     * 数据内容
     */
    private byte[] data;

    private short groupId;
    private int floor;
    private int doorNumber;
    private int doorId;

    /**
     * CRC校验 2个字节
     */
    private byte[] crc;

    /**
     * 获取CRC校验的数据
     */
    public byte[] getCrcData(){
        int len = Objects.nonNull(data) ? data.length : 0;
        byte[] crcData = new byte[len+9];
        crcData[0] = header1;
        crcData[1] = header2;
        crcData[2] = length[1];
        crcData[3] = length[0];
        crcData[4] = sourceAddress;
        crcData[5] = destinationAddress;
        crcData[6] = frameType;
        crcData[7] = functionCode;
        crcData[8] = getFrameSerial();
        if (Objects.nonNull(data)) {
            System.arraycopy(data, 0, crcData, 9, len);
        }
//        byte[] crc16 = VerifyUtil.getCRC16(crcData, 0);
        return crcData;
    }
    public byte[] getReceviceCrcData(){
        short len = (short) ConversionUtil.bytesToInt(length);
        byte[] crcData = new byte[len+9];
        crcData[0] = header1;
        crcData[1] = header2;
        crcData[2] = length[0];
        crcData[3] = length[1];
        crcData[4] = sourceAddress;
        crcData[5] = destinationAddress;
        crcData[6] = frameType;
        crcData[7] = functionCode;
        crcData[8] = getFrameSerial();
        if (Objects.nonNull(data)) {
            System.arraycopy(data, 0, crcData, 9, len);
        }
//        byte[] crc16 = VerifyUtil.getCRC16(crcData, 0);
        return crcData;
    }

}

