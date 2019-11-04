package com.mamba.mocking.dubbo.provider;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.core.io.FileSystemResource;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;

public class MockDubboProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(MockDubboProvider.class);

    @Parameter(names = {"-jar"}, description = "jar path", required = true)
    private String jarFile;

    @Parameter(names = {"-cfg", "--provider"}, description = "provider file", required = true)
    private File providerFile;

    public void serve() throws Exception {
        URL jarPath = toURI(this.jarFile).toURL();
        FileSystemResource providerResource = new FileSystemResource(this.providerFile);
        try (URLClassLoader classLoader = URLClassLoader.newInstance(new URL[]{jarPath}, Thread.currentThread().getContextClassLoader())) {
            Thread.currentThread().setContextClassLoader(classLoader);
            GenericXmlApplicationContext context = new GenericXmlApplicationContext(providerResource);
            context.start();
            LOGGER.info("dubbo provider is started...");
            synchronized (MockDubboProvider.class) {
                MockDubboProvider.class.wait();
            }
            context.stop();
        }
    }

    private static URI toURI(String path) {
        File file = new File(path);
        if (file.exists()) {
            return file.toURI();
        } else {
            return URI.create(path);
        }
    }

    public static void main(String... args) throws Exception {
        MockDubboProvider provider = new MockDubboProvider();
        JCommander.newBuilder().addObject(provider).build().parse(args);
        provider.serve();
    }
}
