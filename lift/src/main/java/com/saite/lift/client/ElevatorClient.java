package com.saite.lift.client;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ArrayUtil;
import com.saite.lift.coder.SaiteDooeEncoder;
import com.saite.lift.coder.SaiteDoorDecoder;
import com.saite.lift.common.bean.SaiteDoorProtocol;
import com.saite.lift.common.util.ConversionUtil;
import com.saite.lift.common.util.ProtocolGenerator;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * @Author: liyq
 */
@Slf4j
public class ElevatorClient {

    public static void main(String[] args) throws Exception {
//        String host = "192.168.14.23";
//        String host = "192.168.20.127";
//        String host = "8.134.9.86";
//        String host = "121.32.24.86";
        String host = "192.168.28.200";
        new ElevatorClient().connet(10099, host);

    }

    protected void connet(int port, String host) throws Exception {
        NioEventLoopGroup nioEventLoopGroup = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(nioEventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<>() {
                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            ch
                                    .pipeline()
                                    .addLast("SaiteDooeEncoder", new SaiteDooeEncoder())
                                    .addLast("SaiteDoorDecoder", new SaiteDoorDecoder())
                                    .addLast("ping", new IdleStateHandler(10,5,10))
                                    .addLast(new NettyClientHandler());
                        }
                    });
            ChannelFuture future = bootstrap.connect(host, port).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            nioEventLoopGroup.shutdownGracefully();
        }
    }

    @Slf4j
    static class NettyClientHandler extends ChannelInboundHandlerAdapter {

//        static final String FILE_PATH = "E:\\任务\\2022-03-24 梯控项目运维系统需求\\test\\a.bin";
        static final String FILE_PATH = "D:\\temp\\test\\a.bin";

        int size = 0;

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            if (msg instanceof SaiteDoorProtocol) {
                SaiteDoorProtocol protocol = (SaiteDoorProtocol) msg;
                byte functionCode = protocol.getFunctionCode();
                String s = ConversionUtil.byteToHexString(functionCode);
                if(functionCode == (byte) 0x50) {
                    log.info("--心跳应答 :{}", s);
                }
                else if(functionCode == (byte) 0x51 ){
                    log.info("--告警应答 :{}", s);
                }
                else if(functionCode == (byte) 0x52) {
                    if (checkResponseFrame(protocol)) {
                        log.info("数据应答：");
                        return;
                    }
                    String hard = "0001";
                    String soft = "01000002";
                    byte[] data = ArrayUtil.addAll(ConversionUtil.hexStringToBytes(hard), ConversionUtil.hexStringToBytes(soft));
                    SaiteDoorProtocol version = ProtocolGenerator.getSaiteProtocolBySend((byte) 0x04, functionCode, protocol.getFrameSerial(), data);
                    ctx.writeAndFlush(version);
                }
                else if(functionCode == (byte) 0x53) {
                    if (checkResponseFrame(protocol)) {
                        return;
                    }
                    byte[] infos = new byte[14];
                    int listNum = 10;
                    byte[] listNumBytes = ConversionUtil.intToTwoByteArray(listNum);
                    infos[0] = listNumBytes[1];
                    infos[1] = listNumBytes[0];
                    infos[2] = (byte) 10;
                    int projectId = 11;
                    byte[] projectIdBytes = ConversionUtil.intToTwoByteArray(projectId);
                    infos[3] = projectIdBytes[1];
                    infos[4] = projectIdBytes[0];
                    infos[5] = 0;
                    infos[6] = 0;
                    infos[7] = 0;
                    infos[8] = 0;
                    infos[9] = 10;
                    infos[10] = 1;
                    infos[11] = 16;
                    infos[12] = 1;
                    infos[13] = 1;
                    Long a = 4294967295L;
                    byte[] aa = ConversionUtil.reverseByte(ConversionUtil.longToFourByteArray(a));
                    byte[] floorDetectionMark = aa;
                    Long b = 0L;
                    byte[] bb = ConversionUtil.reverseByte(ConversionUtil.longToFourByteArray(b));
                    byte[] buttonTriggerDuration = bb;
                    byte[] longControlDoorDuration = bb;
                    byte[] networkTimeoutDuration = bb;
                    byte[] takeLiftTaskTimeoutDuration = aa;
                    byte[] dispatchingSystemIpAddress = new byte[4];
                    dispatchingSystemIpAddress[0] = (byte) (54 & 0xFF);
                    dispatchingSystemIpAddress[1] = (byte) (12 & 0xFF);
                    dispatchingSystemIpAddress[2] = (byte) (168 & 0xFF);
                    dispatchingSystemIpAddress[3] = (byte) (196 & 0xFF);
                    System.out.println(ConversionUtil.bytesToHexString(dispatchingSystemIpAddress));
                    int port = 10099;
                    byte[] dispatchingSystemPort = ConversionUtil.reverseByte(ConversionUtil.intToTwoByteArray(port));
                    byte[] data = ArrayUtil.addAll(infos,
                            floorDetectionMark,buttonTriggerDuration, longControlDoorDuration, networkTimeoutDuration,takeLiftTaskTimeoutDuration,
                            dispatchingSystemIpAddress, dispatchingSystemPort, dispatchingSystemIpAddress, dispatchingSystemPort);
                    SaiteDoorProtocol version = ProtocolGenerator.getSaiteProtocolBySend((byte) 0x04, functionCode, protocol.getFrameSerial(), data);
                    ctx.writeAndFlush(version);
                }
                else if(functionCode == (byte) 0x54) {
                    if (checkResponseFrame(protocol)) {
                        return;
                    } else {
                        SaiteDoorProtocol reply = ProtocolGenerator.getSaiteProtocolByReply((byte)0x00, protocol, null);
                        ctx.writeAndFlush(reply);
                    }
                    TimeUnit.SECONDS.sleep(1);
                    log.info("收到配置信息: " + s);
                    byte[] bytes = new byte[1];
                    bytes[0] = (byte) 0;
                    log.info("数据内容：" + ConversionUtil.bytesToHexString(protocol.getData()));
                    SaiteDoorProtocol reply = ProtocolGenerator.getSaiteProtocolByReply((byte) 0x04, protocol, bytes);
                    ctx.writeAndFlush(reply);
                }
                else if(functionCode == (byte) 0x55) {
                    if (checkResponseFrame(protocol)) {
                        return;
                    }
                    log.info("开始下发升级包: "+s);
                    byte[] data = protocol.getData();
                    log.info("数据内容：" + ConversionUtil.bytesToHexString(data));
                    byte[] sizeBytes = ConversionUtil.subByte(data, 0, 4);
                    size = ConversionUtil.bytesToInt(sizeBytes);
                    log.info("文件长度: " + size);
                    byte[] crc32Bytes = ConversionUtil.subByte(data, 4, 4);
                    log.info("crc32值: " + ConversionUtil.bytesToHexString(crc32Bytes));
                    //应答
                    byte[] bytes = new byte[1];
                    bytes[0] = (byte) 0;
                    SaiteDoorProtocol reply = ProtocolGenerator.getSaiteProtocolByReply((byte) 0x04, protocol, bytes);
                    ctx.writeAndFlush(reply);
                }
                else if(functionCode == (byte) 0x56) {
                    if (checkResponseFrame(protocol)) {
                        return;
                    }
                    log.info("接收升级包碎片: "+ s);
                    byte[] data = protocol.getData();
                    byte[] lengthBytes = ConversionUtil.subByte(data, 0, 2);
                    int chunkSize = ConversionUtil.bytesToInt(lengthBytes);
                    log.info("当前碎片为：[{}]", chunkSize);
                    int merge = size % 256 == 0 ? size / 256 : size / 256 + 1;
                    int dataSize = data.length - 2;
                    if (chunkSize >= merge) {
                        dataSize = size - (chunkSize - 1) * 256;
                        log.info("最后一片{}，数据长度：{}", chunkSize, dataSize);
                    }
                    byte[] fileData = ConversionUtil.subByte(data, 2, dataSize);
                    File file = new File(FILE_PATH + chunkSize);
                    if (!FileUtil.exist(file)) {
                        File parentFile = file.getParentFile();
                        if (!parentFile.exists()) {
                            parentFile.mkdirs();
                        }
                        file.createNewFile();
                    }
                    FileUtil.writeBytes(fileData, file);
                    //应答
                    byte[] bytes = new byte[3];
                    bytes[0] = (byte) 0;
                    bytes[1] = lengthBytes[0];
                    bytes[2] = lengthBytes[1];
                    SaiteDoorProtocol reply = ProtocolGenerator.getSaiteProtocolByReply((byte) 0x04, protocol, bytes);
                    ctx.writeAndFlush(reply);
//                    if (chunkSize < 10) {
//                    }
                    //合并文件
                    if (chunkSize >= merge) {
                        merge(merge);
                    }
                }
                else if(functionCode == (byte) 0x57) {
                    log.info("升级结果答复: "+s);
                }
                else if(functionCode == (byte) 0x59){
                    SaiteDoorProtocol saiteDoorProtocol = ProtocolGenerator.getSaiteProtocolByReply((byte) 0x00, protocol, null);
                    ctx.channel().write(saiteDoorProtocol);
                    log.info("连接结果答复: "+s);
                }
                else if(functionCode == (byte) 0x5b){
                    byte[] bytes = new byte[1];
                    bytes[0] = 0x00;
                    SaiteDoorProtocol saiteDoorProtocol = ProtocolGenerator.getSaiteProtocolByReply((byte) 0x04, protocol, bytes);
                    ctx.channel().write(saiteDoorProtocol);
                    log.info("连接结果答复: "+s);
                } else if (functionCode == (byte) 0x5c) {
                    byte frameType = protocol.getFrameType();
                    if (frameType==0x00) {
                        return;
                    }
                    byte[] bytes = new byte[1];
                    bytes[0] = 0x01;
                    SaiteDoorProtocol saiteDoorProtocol = ProtocolGenerator.getSaiteProtocolByReply((byte) 0x04, protocol, bytes);
                    ctx.channel().write(saiteDoorProtocol);
                }
            }
            super.channelRead(ctx, msg);
        }

        private void merge(int merge) {
            BufferedOutputStream bufferedOutputStream = null;
            log.info("merge:" + merge);
            try {
                File mergeFile = new File(FILE_PATH);
                if (mergeFile.isFile() && mergeFile.exists()) {
                    Files.delete(Paths.get(mergeFile.getPath()));
                }
                //检查碎片
                for (int i = 1; i <= merge; i++) {
                    File tempFile = new File(FILE_PATH + i);
                    while (!tempFile.exists() ) {
                        TimeUnit.SECONDS.sleep(1);
                    }
                }
                bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(mergeFile, true));
                for (Integer i = 1; i <= merge; i++) {
                    File tempFile = new File(FILE_PATH + i);
                    byte[] bytes = FileUtil.readBytes(tempFile);
//                    byte[] bytes = FileUtils.readFileToByteArray(tempFile);
                    bufferedOutputStream.write(bytes);
                    bufferedOutputStream.flush();
                    CompletableFuture.runAsync(()->{
                        try {
                            Files.delete(Paths.get(tempFile.getPath()));
                        } catch (IOException e) {
                            log.warn("碎片删除异常：", e);
                        }
                    });
                }
            } catch (Exception e) {
                log.error("碎片合并异常", e);
            } finally {
                if (bufferedOutputStream!= null) {
                    try {
                        bufferedOutputStream.close();
                        log.info("碎片合并IO流关闭");
                    } catch (IOException e) {
                        log.error("碎片合并IO流关闭异常", e);
                    }
                }
            }

        }

        public boolean checkResponseFrame(SaiteDoorProtocol protocol) {
            return Objects.equals(protocol.getFrameType(), (byte)0x00);
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
            super.channelReadComplete(ctx);
        }

        int a = 0;
        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            if (evt instanceof IdleStateEvent) {
                IdleState state = ((IdleStateEvent) evt).state();
                switch (state) {
                    case READER_IDLE:
                        log.warn("读空闲！！");
                        break;
                    case WRITER_IDLE:
                        log.warn("写空闲！！");
                       byte function = 0x50;
                       String sn = "EC-8097";
                        byte[] snData = sn.getBytes(StandardCharsets.UTF_8);
                        int length = snData.length;
                        byte[] ints = ConversionUtil.intToTwoByteArray(length);
                        byte[] data1 = ConversionUtil.reverseByte(ints);
//                        int statusLength = 6;
//                        int statusLength = 7;
                        int statusLength = 8;
                        byte[] info = new byte[statusLength];
                        info[0] = 0x01;
                        info[1] = 0x03;
                        info[2] = 0x00;
                        info[3] = 0x01;
                        info[4] = (byte) 0xff;
                        info[5] = 0x04;
                        info[6] = 0x00;
                        info[7] = 0x00;
                        byte[] data = ArrayUtil.addAll(data1, snData, info);
                        SaiteDoorProtocol saiteDoorProtocol = generateProtocol(function, data);
                        a++;
                        if (a > 3) {
                            /*发送告警*/
//                            testAlarm(ctx);
//                            uploadStatus(ctx);
//                            TimeUnit.SECONDS.sleep(25);
                            log.warn("数据:{}",a);
                            a = 0;
                        }
                        ctx.channel().writeAndFlush(saiteDoorProtocol);
                        break;
                    case ALL_IDLE:
                        log.warn("读写空闲！！");
                        break;
                    default:
                        break;
                }
            }
            super.userEventTriggered(ctx, evt);
        }

        /**发送告警*/
        private void testAlarm(ChannelHandlerContext ctx) throws InterruptedException {
            TimeUnit.SECONDS.sleep(10);
            //告警测试
            byte[] alarm = new byte[4];
            alarm[0] = 0x01;
//            alarm[1] = 0x04;   30704
//            alarm[2] = 0x07;
//            alarm[3] = 0x03;
            alarm[1] = 0x06;
            alarm[2] = 0x00;
            alarm[3] = 0x04;
            byte alarmFunction = 0x51;
            SaiteDoorProtocol protocol = generateProtocol(alarmFunction, alarm);
            ctx.channel().writeAndFlush(protocol);
        }

        /**上报升级梯控结果*/
        private void uploadStatus(ChannelHandlerContext ctx) throws InterruptedException {
            TimeUnit.SECONDS.sleep(5);
            //告警测试
            byte[] alarm = new byte[2];
            alarm[0] = 0x01;
            alarm[1] = 0x01;
            byte alarmFunction = 0x57;
            SaiteDoorProtocol protocol = generateProtocol(alarmFunction, alarm);
            ctx.channel().writeAndFlush(protocol);
        }

        public SaiteDoorProtocol generateProtocol(byte function, byte[] data){
            Integer frameNo = 12;
            return ProtocolGenerator.getSaiteProtocolBySend((byte) 0x03, function, frameNo.byteValue(), data);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            log.info("异常", cause);
            ctx.channel().close();
            super.exceptionCaught(ctx, cause);
        }


    }
}
