package com.mamba.mocking.dubbo.api.service;

import com.mamba.mocking.dubbo.api.dto.request.PaymentCreateRequest;
import com.mamba.mocking.dubbo.api.dto.response.PaymentCreateResponse;

public interface PaymentService {

    PaymentCreateResponse create(PaymentCreateRequest request);
}
