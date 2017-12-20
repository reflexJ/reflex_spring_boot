package com.reflex.service.lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedLongSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * @author: xuyongjian
 * @date: 2017/12/13
 */
public class Mutex implements Lock {

     private static class Sync extends AbstractQueuedLongSynchronizer{

         @Override
         protected boolean isHeldExclusively(){
             return getState() == 1L;
         }

         @Override
         protected boolean tryAcquire(long arg) {
             if (compareAndSetState(0, 1)) {
                 setExclusiveOwnerThread(Thread.currentThread());
                 return true;
             }
             return false;
         }

         @Override
         protected boolean tryRelease(long arg) {
             if (getState() == 0) {
                 throw new IllegalMonitorStateException();
             }
             setExclusiveOwnerThread(null);
             setState(0);
             return true;
         }

         Condition newCondition() {
             return new ConditionObject();
         }
     }

    private final Sync sync=new Sync();

    @Override
    public void lock() {
        sync.acquire(1);
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        sync.acquireInterruptibly(1);
    }

    @Override
    public boolean tryLock() {
        return sync.tryAcquire(1);
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return sync.tryAcquireNanos(1, unit.toNanos(time));
    }

    @Override
    public void unlock() {
        sync.tryRelease(1);
    }

    @Override
    public Condition newCondition() {
        return sync.newCondition();
    }
}