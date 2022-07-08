package com.chenghq.demo;

import static java.lang.Thread.sleep;

public class TestJdkPlugin {

    public static void main(String[] args) {
        TestJdkPlugin testJdkPlugin = new TestJdkPlugin();
        testJdkPlugin.printMessage();
    }

    public void printMessage() {
        while (true) {
            try {
                doPrint();
                sleep(2000);
            } catch (InterruptedException e) {
                // ignore
            }
        }
    }

    public void doPrint() {
        System.out.println("after hotswap");
    }
}
