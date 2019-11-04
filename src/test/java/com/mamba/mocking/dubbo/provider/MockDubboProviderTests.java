package com.mamba.mocking.dubbo.provider;

import org.junit.jupiter.api.Test;

class MockDubboProviderTests {

    static {
        System.setProperty("dubbo.application.qos.enable", "false");
    }

    @Test
    void test1() throws Exception {
        String jarPath = System.getProperty("user.dir") + "/sample/sample.jar";
        String providerCfg = System.getProperty("user.dir") + "/sample/provider.xml";
        MockDubboProvider.main("-jar", jarPath, "-cfg", providerCfg);
        System.out.println(1);
    }

    @Test
    void test2() throws Exception {
        String jarPath = System.getProperty("user.dir") + "/sample/sample.jar";
        String providerCfg = System.getProperty("user.dir") + "/sample/provider.xml";
        MockDubboProvider.main("-jar", jarPath, "--provider", providerCfg);
        System.out.println(1);
    }
}