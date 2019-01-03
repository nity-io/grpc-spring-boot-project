package io.nity.grpc.sample.grpc;

import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.examples.helloworld.GreeterGrpc;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.NegotiationType;
import io.grpc.netty.NettyChannelBuilder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.nity.grpc.DisposableManagedChannel;
import io.nity.grpc.autoconfigure.GrpcProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import javax.net.ssl.SSLException;
import java.io.File;

/**
 * grpc存根配置类，完成Channel和所有Stub的初始化
 */
@Configuration
@EnableConfigurationProperties(GrpcProperties.class)
public class GrpcStubConfig {

    private static final Logger log = LoggerFactory.getLogger(GrpcStubConfig.class);

    @Autowired
    protected GrpcProperties grpcProperties;

    @Bean
    public GreeterGrpc.GreeterBlockingStub getGreeterBlockingStub(Channel channel) {
        GreeterGrpc.GreeterBlockingStub blockingStub = GreeterGrpc.newBlockingStub(channel);
        return blockingStub;
    }

    @Bean
    @ConditionalOnProperty(value = "grpc.stub.enabled", havingValue = "true", matchIfMissing = true)
    public DisposableManagedChannel getChannel() throws SSLException {
        GrpcProperties.StubProperties stub = grpcProperties.getStub();
        String model = stub.getModel();

        int port = stub.getPort();

        ManagedChannel channel;

        if (GrpcProperties.SERVER_MODEL_IN_PROCESS.equals(model)) {
            String inProcessServerName = stub.getInProcessServerName();

            if (!StringUtils.hasText(inProcessServerName)) {
                log.error("please config required property [inProcessServerName] for InProcess model");
                throw new RuntimeException("Failed to create inProcessChannel");
            }

            log.warn("will create InProcessChannel. Please only use in testing");
            channel = InProcessChannelBuilder.forName(inProcessServerName).directExecutor().build();
        } else if (GrpcProperties.SERVER_MODEL_SIMPLE.equals(model)) {
            String host = stub.getHost();
            if (!StringUtils.hasText(host)) {
                log.error("please config required property [host] for simple model");
                throw new RuntimeException("Failed to create channel without tls");
            }

            log.info("will create channel without tls. recommend only use in internal service");
            log.info("creating channel on {}:{}", host, port);

            channel = ManagedChannelBuilder.forAddress(host, port)
                    .usePlaintext()
                    .build();
        } else if (GrpcProperties.SERVER_MODEL_TLS.equals(model)) {
            String host = stub.getHost();
            String trustCertCollectionFilePath = stub.getTrustCertCollectionFilePath();

            if (!StringUtils.hasText(host)) {
                log.error("please config required property [host] for Tls model");
                throw new RuntimeException("Failed to create Tls channel");
            }
            if (!StringUtils.hasText(trustCertCollectionFilePath)) {
                log.error("please config required property [trustCertCollectionFilePath] for Tls model");
                throw new RuntimeException("Failed to create Tls channel");
            }

            log.info("will create channel with tls");
            log.info("creating channel on {}:{}", host, port);

            SslContext sslContext = buildSslContext(trustCertCollectionFilePath, null, null);

            channel = NettyChannelBuilder.forAddress(host, port)
                    .negotiationType(NegotiationType.TLS)
                    .sslContext(sslContext)
                    .build();
        } else if (GrpcProperties.SERVER_MODEL_TLS_MUTUAL.equals(model)) {
            String host = stub.getHost();
            String trustCertCollectionFilePath = stub.getTrustCertCollectionFilePath();
            String clientCertChainFilePath = stub.getClientCertChainFilePath();
            String clientPrivateKeyFilePath = stub.getClientPrivateKeyFilePath();

            if (!StringUtils.hasText(host)) {
                log.error("please config required property [host] for TLS with mutual model");
                throw new RuntimeException("Failed to create channel with TLS with mutual");
            }
            if (!StringUtils.hasText(trustCertCollectionFilePath)) {
                log.error("please config required property [trustCertCollectionFilePath] for TLS with mutual model");
                throw new RuntimeException("Failed to create channel with TLS with mutual");
            }
            if (!StringUtils.hasText(clientCertChainFilePath)) {
                log.error("please config required property [clientCertChainFilePath] for TLS with mutual model");
                throw new RuntimeException("Failed to create channel with TLS with mutual");
            }
            if (!StringUtils.hasText(clientPrivateKeyFilePath)) {
                log.error("please config required property [clientPrivateKeyFilePath] for TLS with mutual model");
                throw new RuntimeException("Failed to create channel with TLS with mutual");
            }

            log.info("will create channel with TLS with mutual");
            log.info("creating channel on {}:{}", host, port);

            SslContext sslContext = buildSslContext(trustCertCollectionFilePath, clientCertChainFilePath, clientPrivateKeyFilePath);

            channel = NettyChannelBuilder.forAddress(host, port)
                    .negotiationType(NegotiationType.TLS)
                    .sslContext(sslContext)
                    .build();
        } else {
            throw new RuntimeException("UnImplement model [" + model + "] when create channel");
        }

        DisposableManagedChannel disposableManagedChannel = new DisposableManagedChannel(channel);
        return disposableManagedChannel;
    }

    private static SslContext buildSslContext(String trustCertCollectionFilePath,
                                              String clientCertChainFilePath,
                                              String clientPrivateKeyFilePath) throws SSLException {
        SslContextBuilder builder = GrpcSslContexts.forClient();
        if (trustCertCollectionFilePath != null) {
            builder.trustManager(new File(trustCertCollectionFilePath));
        }
        if (clientCertChainFilePath != null && clientPrivateKeyFilePath != null) {
            builder.keyManager(new File(clientCertChainFilePath), new File(clientPrivateKeyFilePath));
        }
        builder.protocols("TLSv1.2");
        return builder.build();
    }
}
