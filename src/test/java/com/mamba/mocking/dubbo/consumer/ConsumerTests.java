package com.mamba.mocking.dubbo.consumer;

import com.mamba.mocking.dubbo.api.dto.request.PaymentCreateRequest;
import com.mamba.mocking.dubbo.api.dto.response.PaymentCreateResponse;
import com.mamba.mocking.dubbo.api.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.GenericXmlApplicationContext;

class ConsumerTests {

    static {
        System.setProperty("dubbo.application.qos.enable", "false");
    }

    @Test
    void test_call_method() throws Exception {
        GenericXmlApplicationContext context = new GenericXmlApplicationContext("consumer.xml");
        try {
            context.start();
            PaymentService paymentService = context.getBean(PaymentService.class);
            long beginTime = System.currentTimeMillis();
            PaymentCreateResponse response = paymentService.create(new PaymentCreateRequest());
            long endTime = System.currentTimeMillis();
            System.out.println("create Latency: " + (endTime - beginTime));
            System.out.println("create Return: " + response);
        } finally {
            context.stop();
        }
    }
}
