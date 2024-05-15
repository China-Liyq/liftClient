package com.saite.lift.common.util;


import com.saite.lift.common.bean.SaiteLiftProtocol;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * @author llw
 * 处理netty中各个protocol的封装
 */

@Slf4j
public class ProtocolGenerator {

    public static SaiteLiftProtocol getSaiteProtocolBySend(byte frameType, byte functionCode, byte frameNo, byte[] data){
        int length = Objects.nonNull(data) ? data.length : 0;
        SaiteLiftProtocol protocol = new SaiteLiftProtocol();
        //帧头
        byte header1 = (byte) 0x55;
        protocol.setHeader1(header1);
        byte header2 = (byte) 0xaa;
        protocol.setHeader2(header2);
        //源地址
        byte sourceAddress = 8;
        protocol.setSourceAddress(sourceAddress);
        //目标地址
        byte destinationAddress = 9;
        protocol.setDestinationAddress(destinationAddress);
        //帧类型
        protocol.setFrameType(frameType);
        //功能码
        protocol.setFunctionCode(functionCode);
        //帧编号
        protocol.setFrameSerial(frameNo);
        //设置数据
        protocol.setData(data);
        //数据段长度
        byte[] lengthByte = ConversionUtil.intToTwoByteArray(length);
        protocol.setLength(lengthByte);
        //CRC校验
        byte[] crcData = protocol.getCrcData();
        byte[] crc16 = VerifyUtil.getCRC16(crcData, 0);
        protocol.setCrc(crc16);
        return protocol;
    }
    public static SaiteLiftProtocol getSaiteProtocolByReply(SaiteLiftProtocol sourceProtocol, byte[] data){
        int length = data.length;
        if(length > 0 ) {
            SaiteLiftProtocol protocol = new SaiteLiftProtocol();
            //帧头
            protocol.setHeader1(sourceProtocol.getHeader1());
            protocol.setHeader2(sourceProtocol.getHeader2());
            //源地址
            byte sourceAddress = 1;
            protocol.setSourceAddress(sourceAddress);
            //目标地址
            protocol.setDestinationAddress(sourceProtocol.getSourceAddress());
            //帧类型
            byte frameType = 0x04;
            protocol.setFrameType(frameType);
            //功能码
            protocol.setFunctionCode(sourceProtocol.getFunctionCode());
            //帧编号
            protocol.setFrameSerial(sourceProtocol.getFrameSerial());
            //设置数据
            protocol.setData(data);
            //数据段长度
            byte[] lengthByte = ConversionUtil.intToTwoByteArray(length);
            protocol.setLength(lengthByte);

            //CRC校验
            byte[] crcData = protocol.getCrcData();
            byte[] crc16 = VerifyUtil.getCRC16(crcData, 0);
            protocol.setCrc(crc16);
            return protocol;
        }
        return null;
    }

    public static SaiteLiftProtocol getSaiteProtocolByReply(Byte frameType, SaiteLiftProtocol sourceProtocol, byte[] data){
        int length = Objects.nonNull(data) ? data.length : 0;
        SaiteLiftProtocol protocol = new SaiteLiftProtocol();
        //帧头
        protocol.setHeader1(sourceProtocol.getHeader1());
        protocol.setHeader2(sourceProtocol.getHeader2());
        //源地址
        protocol.setSourceAddress(sourceProtocol.getDestinationAddress());
        //目标地址
        protocol.setDestinationAddress(sourceProtocol.getSourceAddress());
        //帧类型
        protocol.setFrameType(frameType);
        //功能码
        protocol.setFunctionCode(sourceProtocol.getFunctionCode());
        //帧编号
        protocol.setFrameSerial(sourceProtocol.getFrameSerial());
        //设置数据
        protocol.setData(data);
        //数据段长度
        byte[] lengthByte = ConversionUtil.intToTwoByteArray(length);
        protocol.setLength(lengthByte);
        //CRC校验
        byte[] crcData = protocol.getCrcData();
        byte[] crc16 = VerifyUtil.getCRC16(crcData, 0);
        protocol.setCrc(crc16);
        return protocol;
    }

}
