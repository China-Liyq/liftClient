package com.saite.lift.coder;

import com.saite.lift.common.bean.SaiteLiftProtocol;
import com.saite.lift.common.util.ConversionUtil;
import com.saite.lift.common.util.VerifyUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;

/**
 * @author llw
 */
@Slf4j
public class SaiteLiftDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf inBuf, List<Object> list) throws Exception {
        int count = inBuf.readableBytes();
        //确认可读字节数大于基本字节数
        if (count < 11) {
            return;
        }
        while (count >= 11) {
            //判断数据是否合法,防止socket字节流攻击
            String originalData = ByteBufUtil.hexDump(inBuf);
            log.info("进口数据：{}", originalData);
            //标记当前readIndex位置
            inBuf.markReaderIndex();
            //帧头
            byte header1 = inBuf.readByte();
            try {
                if (header1 == (byte) 0x55) {
                    SaiteLiftProtocol protocol = new SaiteLiftProtocol();
                    byte header2 = inBuf.readByte();
                    protocol.setHeader1(header1);
                    protocol.setHeader2(header2);
                    //数据长度
                    byte[] lenByte = new byte[2];
                    inBuf.readBytes(lenByte);
                    short len = (short) ConversionUtil.bytesToInt(lenByte);
//                    log.info("数据长度：{}", len);
                    if (count < len + 11) {
                        inBuf.resetReaderIndex();
                        return;
                    }
                    protocol.setLength(lenByte);
                    //源地址
                    byte sourceAddress = inBuf.readByte();
                    protocol.setSourceAddress(sourceAddress);
                    //目标地址
                    byte destinationAddress = inBuf.readByte();
                    protocol.setDestinationAddress(destinationAddress);
                    //帧类型
                    byte frameType = inBuf.readByte();
                    protocol.setFrameType(frameType);
                    //功能码
                    byte functionCode = inBuf.readByte();
                    protocol.setFunctionCode(functionCode);
                    //帧编号
                    byte frameSerial = inBuf.readByte();
                    protocol.setFrameSerial(frameSerial);
                    if (len > 0) {
                        //含有数据段
                        byte[] data = new byte[len];
                        inBuf.readBytes(data);
                        protocol.setData(data);
                    }
                    //CRC16
                    byte[] crc16 = new byte[2];
                    inBuf.readBytes(crc16);
                    protocol.setCrc(crc16);
                    byte[] crcData = protocol.getReceviceCrcData();
                    byte[] newCRC16 = VerifyUtil.getCRC16(crcData, 0);
                    //判断crc是否一致
                    if (!Arrays.equals(crc16, newCRC16)) {
                        log.info("saite门控校验失败,接收的crc16: {} ,本地的crc16: {} 原数据:{}", ConversionUtil.bytesToHexString(crc16), ConversionUtil.bytesToHexString(newCRC16), originalData);
                        inBuf.clear();
                        return;
                    }
                    list.add(protocol);
                } else {
                    log.error("丢弃数据,帧头：[{}]", Integer.toHexString(header1 & 0xFF));
                    inBuf.clear();
                }
                count = inBuf.readableBytes();
                if (count <= 0) {
                    inBuf.clear();
                }
            }catch (Exception e){
                log.error("saite 门控解析数据异常：",e);
            }
        }
    }
}
