package io.nity.grpc.autoconfigure;

import io.grpc.ServerBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.NettyServerBuilder;
import io.grpc.services.HealthStatusManager;
import io.netty.handler.ssl.ClientAuth;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslProvider;
import io.nity.grpc.GrpcServerBuilderConfigurer;
import io.nity.grpc.GrpcServerRunner;
import io.nity.grpc.GrpcService;
import io.nity.grpc.context.LocalRunningGrpcPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.util.StringUtils;

import javax.net.ssl.SSLException;
import java.io.File;
import java.net.InetSocketAddress;

@AutoConfigureOrder
@ConditionalOnBean(annotation = GrpcService.class)
@EnableConfigurationProperties(GrpcProperties.class)
public class GrpcAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(GrpcAutoConfiguration.class);

    @LocalRunningGrpcPort
    private int port;

    @Autowired
    private GrpcProperties grpcProperties;

    @Bean
    @ConditionalOnProperty(value = "grpc.server.enabled", havingValue = "true", matchIfMissing = true)
    public GrpcServerRunner grpcServerRunner(GrpcServerBuilderConfigurer configurer) throws SSLException {
        GrpcProperties.ServerProperties server = grpcProperties.getServer();

        String model = server.getModel();

        if (GrpcProperties.SERVER_MODEL_IN_PROCESS.equals(model)) {
            String inProcessServerName = server.getInProcessServerName();

            if (!StringUtils.hasText(inProcessServerName)) {
                log.error("please config required property [inProcessServerName] for InProcess model");
                throw new RuntimeException("Failed to create inProcessServer");
            }

            log.warn("gRPC Server will run in InProcess model. Please only use in testing");
            return new GrpcServerRunner(configurer, InProcessServerBuilder.forName(inProcessServerName));
        } else if (GrpcProperties.SERVER_MODEL_SIMPLE.equals(model)) {
            log.info("gRPC Server will run without tls. recommend only use in internal service");
            log.info("gRPC Server will listen on port {}", port);
            return new GrpcServerRunner(configurer, ServerBuilder.forPort(port));
        } else if (GrpcProperties.SERVER_MODEL_TLS.equals(model)) {
            String host = server.getHost();
            String certChainFilePath = server.getCertChainFilePath();
            String privateKeyFilePath = server.getPrivateKeyFilePath();

            if (!StringUtils.hasText(host)) {
                log.error("please config required property [host] for Tls model");
                throw new RuntimeException("Failed to create Tls Server");
            }
            if (!StringUtils.hasText(certChainFilePath)) {
                log.error("please config required property [certChainFilePath] for Tls model");
                throw new RuntimeException("Failed to create Tls Server");
            }
            if (!StringUtils.hasText(privateKeyFilePath)) {
                log.error("please config required property [privateKeyFilePath] for Tls model");
                throw new RuntimeException("Failed to create Tls Server");
            }

            log.info("gRPC Server will run with tls");
            log.info("gRPC Server will listen on {}:{}", host, port);

            NettyServerBuilder serverBuilder = NettyServerBuilder.forAddress(new InetSocketAddress(host, port));
            serverBuilder.sslContext(getSslContextBuilder(certChainFilePath, privateKeyFilePath, null).build());

            return new GrpcServerRunner(configurer, serverBuilder);
        } else if (GrpcProperties.SERVER_MODEL_TLS_MUTUAL.equals(model)) {
            String host = server.getHost();
            String certChainFilePath = server.getCertChainFilePath();
            String privateKeyFilePath = server.getPrivateKeyFilePath();
            String trustCertCollectionFilePath = server.getTrustCertCollectionFilePath();

            if (!StringUtils.hasText(host)) {
                log.error("please config required property [host] for TLS with mutual model");
                throw new RuntimeException("Failed to create TLS with mutual Server");
            }
            if (!StringUtils.hasText(certChainFilePath)) {
                log.error("please config required property [certChainFilePath] for TLS with mutual model");
                throw new RuntimeException("Failed to create TLS with mutual Server");
            }
            if (!StringUtils.hasText(privateKeyFilePath)) {
                log.error("please config required property [privateKeyFilePath] for TLS with mutual model");
                throw new RuntimeException("Failed to create TLS with mutual Server");
            }
            if (!StringUtils.hasText(trustCertCollectionFilePath)) {
                log.error("please config required property [trustCertCollectionFilePath] for TLS with mutual model");
                throw new RuntimeException("Failed to create TLS with mutual with mutual Server");
            }

            log.info("gRPC Server will run with TLS with mutual");
            log.info("gRPC Server will listen on {}:{}", host, port);
            return new GrpcServerRunner(configurer, ServerBuilder.forPort(port));
        }

        throw new RuntimeException("UnImplement model [" + model + "] when create gRPC Server");
    }

    @Bean
    public HealthStatusManager healthStatusManager() {
        return new HealthStatusManager();
    }

    @Bean
    @ConditionalOnMissingBean(GrpcServerBuilderConfigurer.class)
    public GrpcServerBuilderConfigurer serverBuilderConfigurer() {
        return new GrpcServerBuilderConfigurer();
    }

    private SslContextBuilder getSslContextBuilder(String certChainFilePath, String privateKeyFilePath, String trustCertCollectionFilePath) {
        File certChainFile = new File(certChainFilePath);
        File privateKeyFile = new File(privateKeyFilePath);
        log.info("loading certChainFile:{}", certChainFile.getAbsolutePath());
        log.info("loading privateKeyFile:{}", privateKeyFile.getAbsolutePath());
        SslContextBuilder sslClientContextBuilder = SslContextBuilder.forServer(certChainFile, privateKeyFile);

        if (trustCertCollectionFilePath != null) {
            File trustCertCollectionFile = new File(trustCertCollectionFilePath);
            log.info("loading trustCertCollectionFile:{}", trustCertCollectionFile.getAbsolutePath());
            sslClientContextBuilder.trustManager(trustCertCollectionFile);
            sslClientContextBuilder.clientAuth(ClientAuth.REQUIRE);
        }

        sslClientContextBuilder.protocols("TLSv1.2");

        return GrpcSslContexts.configure(sslClientContextBuilder, SslProvider.OPENSSL);
    }
}
