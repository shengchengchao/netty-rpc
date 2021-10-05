package com.xixi.netty.rpc.client;

import com.xixi.netty.rpc.protocol.message.ResponseMessage;
import lombok.Data;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author shengchengchao
 * @Description
 * @createTime 2021/10/5
 */
@Data
public class ResponseFuture {

    private final long beginTimestamp = System.currentTimeMillis();
    private long timeoutMilliseconds;
    private String requestId;
    private volatile boolean sendRequestSucceed = false;
    private volatile Throwable cause;
    private volatile ResponseMessage response;

    private CountDownLatch countDownLatch = new CountDownLatch(1);

    public ResponseFuture(long timeout, String id) {
        timeoutMilliseconds = timeout;
        requestId = id;
    }

    public ResponseMessage waitResponse(long timeoutMilliseconds) throws InterruptedException {

        countDownLatch.await(timeoutMilliseconds, TimeUnit.MICROSECONDS);
        return response;

    }

    public void putResponse(ResponseMessage response) {
        this.response = response;
        countDownLatch.countDown();
    }


    public Boolean timeout() {
        return System.currentTimeMillis() - beginTimestamp > timeoutMilliseconds;
    }

    public ResponseFuture() {
    }
}
