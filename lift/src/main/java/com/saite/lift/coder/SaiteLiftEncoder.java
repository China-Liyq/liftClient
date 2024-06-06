package com.saite.lift.coder;

import cn.hutool.core.util.ArrayUtil;
import com.saite.lift.common.bean.SaiteLiftProtocol;
import com.saite.lift.common.util.ConversionUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

/**
 * @author llw
 */
@Slf4j
public class SaiteLiftEncoder extends MessageToByteEncoder<SaiteLiftProtocol> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, SaiteLiftProtocol protocol, ByteBuf out) throws Exception {
        out.writeByte(protocol.getHeader1());
        out.writeByte(protocol.getHeader2());
        //数据长度,需要先发低字节
        byte[] lengthByte = protocol.getLength();
        out.writeByte(lengthByte[1]);
        out.writeByte(lengthByte[0]);
        out.writeByte(protocol.getSourceAddress());
        out.writeByte(protocol.getDestinationAddress());
        out.writeByte(protocol.getFrameType());
        out.writeByte(protocol.getFunctionCode());
        out.writeByte(protocol.getFrameSerial());
        if (protocol.getData() != null) {
            if (protocol.getData().length > 1) {
                out.writeBytes(protocol.getData());
            }else {
                out.writeBytes(protocol.getData());
            }
        }
        out.writeBytes(protocol.getCrc());
        // 测试
        byte[] bytes = new byte[9];
        bytes[0] = protocol.getHeader1();
        bytes[1] = protocol.getHeader2();
        bytes[2] = lengthByte[1];
        bytes[3] = lengthByte[0];
        bytes[4] = protocol.getSourceAddress();
        bytes[5] = protocol.getDestinationAddress();
        bytes[6] = protocol.getFrameType();
        bytes[7] = protocol.getFunctionCode();
        bytes[8] = protocol.getFrameSerial();
        log.info("出口数据：[{}]", ConversionUtil.bytesToHexString(ArrayUtil.addAll(bytes, protocol.getData(), protocol.getCrc())));

        //测试数据用完删除
        byte functionCode = protocol.getFunctionCode();
        if(functionCode == (byte) 0x50){
//            log.info("查询梯控状态数据 :"+protocol);
        }else if(functionCode == (byte) 0x51){
//            log.info("控制梯控内召数据 :"+protocol);
        }else if(functionCode == (byte) 0x52){
//            log.info("控制梯控开门数据 :"+protocol);
        }else if(functionCode == (byte) 0x53){
//            log.info("控制梯控长开门数据 :"+protocol);
        }else if(functionCode == (byte) 0x54){
//            log.info("控制梯控乘梯任务数据 :"+protocol);
        }else if(functionCode == (byte) 0x55){
            log.info("出口数据：{}-{}","55", ConversionUtil.bytesToHexString(protocol.getData()));
        }else if(functionCode == (byte) 0x56){
            log.info("出口数据：{}-{}","56", ConversionUtil.bytesToHexString(protocol.getData()));
        }else if(functionCode == (byte) 0x57){
//            log.info("收到赛特梯控的调试信息 :"+protocol);
        }
    }
}
