package com.ethan.netty.concurrency;

public class VolatileCanStop implements Runnable {

    private volatile boolean canceled = false;

    @Override
    public void run() {
        int num = 0;
        try {
            while (!canceled && num <= 1000000) {
                if (num % 10 == 0) {
                    System.out.println(num + "是10的倍數");
                }
                num++;
                Thread.sleep(1);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws InterruptedException {
         VolatileCanStop r = new VolatileCanStop();
         Thread t = new Thread(r);
         t.start();
         Thread.sleep(3000);
         r.canceled = true;
    }
}
