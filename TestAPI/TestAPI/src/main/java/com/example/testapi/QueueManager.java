package com.example.testapi;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class QueueManager {

    private final Lock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();
    private final LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<>();

    public void enterQueue(String userId) throws InterruptedException {
        lock.lock();
        try {
            queue.add(userId);
            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public void leaveQueue(String userId) {
        lock.lock();
        try {
            queue.remove(userId);
            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public int getQueueSize() {
        return queue.size();
    }

    public int getPosition(String userId) {
        lock.lock();
        try {
            int position = 0;
            for (String id : queue) {
                if (id.equals(userId)) {
                    return position;
                }
                position++;
            }
            return -1; // userId가 대기열에 없을 경우
        } finally {
            lock.unlock();
        }
    }
}
