package com.mamba.mocking.dubbo.provider;

import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MockServiceFactoryBean<T> implements FactoryBean<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MockServiceFactoryBean.class);

    private final Class<T> interfaceType;

    private MockInvocation mockInvocation;

    public MockServiceFactoryBean(Class<T> interfaceType, Map<String, Map<String, String>> mockMethodMap) {
        this(interfaceType, mockMethodMap, 0);
    }

    public MockServiceFactoryBean(Class<T> interfaceType, Map<String, Map<String, String>> mockMethodMap, int defaultDelay) {
        MockReturn mockReturnDefault = new MockReturn(Math.max(defaultDelay, 0), null);
        Map<Method, MockReturn> mockReturnMap = genMockReturnMap(interfaceType, mockMethodMap, mockReturnDefault.getDelay());
        this.interfaceType = interfaceType;
        this.mockInvocation = new MockInvocation(mockReturnMap, mockReturnDefault);
    }

    @Override
    public T getObject() {
        Object instance = Proxy.newProxyInstance(this.interfaceType.getClassLoader(), new Class<?>[]{this.interfaceType}, this.mockInvocation);
        return this.interfaceType.cast(instance);
    }

    @Override
    public Class<?> getObjectType() {
        return this.interfaceType;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    private static Map<Method, MockReturn> genMockReturnMap(Class<?> interfaceType, Map<String, Map<String, String>> mockMethodMap, int defaultDelay) {
        if (mockMethodMap == null || mockMethodMap.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, Method> methodMap = Arrays.stream(interfaceType.getDeclaredMethods()).collect(Collectors.toMap(MockServiceFactoryBean::genMethodIndex, Function.identity()));
        Map<Method, MockReturn> mockReturnMap = new HashMap<>((int) Math.ceil(mockMethodMap.size() / 0.75));
        for (Map.Entry<String, Map<String, String>> mockMethodEntry : mockMethodMap.entrySet()) {
            Map<String, String> mockReturn = mockMethodEntry.getValue();
            if (mockReturn == null || mockReturn.isEmpty()) {
                continue;
            }
            Method method = methodMap.get(mockMethodEntry.getKey());
            if (method == null) {
                LOGGER.warn("Invalid Method: {}.{}", interfaceType.getName(), mockMethodEntry.getKey());
                continue;
            }
            int delay = getValue(mockReturn, "delay", defaultDelay, Integer::parseInt);
            Object value = getValue(mockReturn, "return", null, json -> JSON.parseObject(json, method.getGenericReturnType()));
            if (value == null && delay == defaultDelay) {
                continue;
            }
            mockReturnMap.put(method, new MockReturn(delay, value));
        }
        return mockReturnMap;
    }

    private static String genMethodIndex(Method method) {
        if (method.getParameterCount() <= 0) {
            return method.getName().concat("()");
        }
        StringBuilder sb = new StringBuilder();
        sb.append(method.getName()).append('(');
        for (Class<?> parameterType : method.getParameterTypes()) {
            sb.append(parameterType.getName()).append(',');
        }
        sb.setCharAt(sb.length() - 1, ')');
        return sb.toString();
    }

    private static <T> T getValue(Map<String, String> map, String key, T defaultValue, Function<String, T> valueTransfer) {
        String value = map.get(key);
        if (value == null) {
            return defaultValue;
        }
        if (value.isEmpty()) {
            return defaultValue;
        }
        String tmpValue = value.trim();
        if (tmpValue.isEmpty()) {
            return defaultValue;
        }
        return valueTransfer.apply(tmpValue);
    }

    @AllArgsConstructor
    private static class MockInvocation implements InvocationHandler {

        private final Map<Method, MockReturn> mockReturnMap;

        private final MockReturn mockReturnDefault;

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            MockReturn mockReturn = this.mockReturnMap.getOrDefault(method, this.mockReturnDefault);
            int delay = mockReturn.getDelay();
            if (delay > 0) {
                Thread.sleep(delay);
            }
            LOGGER.info("mock method: {}, delay: {}", method, delay);
            return mockReturn.getValue();
        }
    }

    @Getter
    @AllArgsConstructor
    private static class MockReturn {

        private final int delay;

        private final Object value;
    }
}
