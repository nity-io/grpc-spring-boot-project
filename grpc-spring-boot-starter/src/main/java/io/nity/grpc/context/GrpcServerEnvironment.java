package io.nity.grpc.context;

import io.nity.grpc.autoconfigure.GrpcProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.util.SocketUtils;

import java.util.Properties;

public class GrpcServerEnvironment implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        MutablePropertySources sources = environment.getPropertySources();
        Properties properties = new Properties();
        Integer configuredPort = environment.getProperty("grpc.server.port", Integer.class);

        if (null == configuredPort) {
            properties.put(LocalRunningGrpcPort.propertyName, GrpcProperties.DEFAULT_SERVER_PORT);
        } else if (0 == configuredPort) {
            properties.put(LocalRunningGrpcPort.propertyName, SocketUtils.findAvailableTcpPort());
        } else {
            properties.put(LocalRunningGrpcPort.propertyName, configuredPort);
        }

        sources.addLast(new PropertiesPropertySource("grpc", properties));
    }
}
