package com.pixurvival.core.benchmarks;

public class Test {

    public static void main(String[] args) throws InterruptedException {

        Object o = new Object();
        new Thread(() -> {

            try {
                Thread.sleep(2000);
                synchronized (o) {
                    o.notifyAll();
                }
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }).start();
        synchronized (o) {
            o.wait();
        }
        System.out.println("ok");
    }
}
