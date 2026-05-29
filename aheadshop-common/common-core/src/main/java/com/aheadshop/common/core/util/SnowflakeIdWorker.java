package com.aheadshop.common.core.util;

import java.net.InetAddress;

/**
 * 雪花算法生成唯一ID
 */
public class SnowflakeIdWorker {

    private final long workerId;
    private final long datacenterId;
    private long sequence = 0L;
    private long lastTimestamp = -1L;

    private static final long EPOCH = 1704067200000L; // 2024-01-01 00:00:00
    private static final long WORKER_ID_BITS = 5L;
    private static final long DATACENTER_ID_BITS = 5L;
    private static final long SEQUENCE_BITS = 12L;

    private static final long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS);
    private static final long MAX_DATACENTER_ID = ~(-1L << DATACENTER_ID_BITS);

    private static final long WORKER_ID_SHIFT = SEQUENCE_BITS;
    private static final long DATACENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;
    private static final long TIMESTAMP_LEFT_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATACENTER_ID_BITS;
    private static final long SEQUENCE_MASK = ~(-1L << SEQUENCE_BITS);

    private static final SnowflakeIdWorker INSTANCE = new SnowflakeIdWorker();

    private SnowflakeIdWorker() {
        long workerId;
        try {
            InetAddress address = InetAddress.getLocalHost();
            byte[] ipBytes = address.getAddress();
            workerId = ((ipBytes[ipBytes.length - 2] & 0xFF) << 8) | (ipBytes[ipBytes.length - 1] & 0xFF);
        } catch (Exception e) {
            workerId = 1L;
        }
        this.workerId = workerId % (MAX_WORKER_ID + 1);
        this.datacenterId = 1L;
    }

    public static SnowflakeIdWorker getInstance() {
        return INSTANCE;
    }

    public synchronized long nextId() {
        long timestamp = System.currentTimeMillis();

        if (timestamp < lastTimestamp) {
            throw new RuntimeException("时钟回拨，拒绝生成ID");
        }

        if (timestamp == lastTimestamp) {
            sequence = (sequence + 1) & SEQUENCE_MASK;
            if (sequence == 0) {
                timestamp = waitNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }

        lastTimestamp = timestamp;

        return ((timestamp - EPOCH) << TIMESTAMP_LEFT_SHIFT)
                | (datacenterId << DATACENTER_ID_SHIFT)
                | (workerId << WORKER_ID_SHIFT)
                | sequence;
    }

    /**
     * 生成订单号（基于雪花ID）
     */
    public static String generateOrderNo() {
        return String.valueOf(INSTANCE.nextId());
    }

    private long waitNextMillis(long lastTimestamp) {
        long timestamp = System.currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }
}