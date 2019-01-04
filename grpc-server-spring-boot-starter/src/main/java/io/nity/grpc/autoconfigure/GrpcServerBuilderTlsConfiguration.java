package io.nity.grpc.autoconfigure;

import io.grpc.ServerBuilder;
import io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.NettyServerBuilder;
import io.netty.handler.ssl.ClientAuth;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslProvider;
import io.nity.grpc.GrpcService;
import io.nity.grpc.context.LocalRunningGrpcPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.util.StringUtils;

import javax.net.ssl.SSLException;
import java.io.File;
import java.net.InetSocketAddress;

@ConditionalOnBean(annotation = GrpcService.class)
@EnableConfigurationProperties(GrpcServerProperties.class)
public class GrpcServerBuilderTlsConfiguration {

    private static final Logger log = LoggerFactory.getLogger(GrpcServerBuilderTlsConfiguration.class);

    @LocalRunningGrpcPort
    private int port;

    @Autowired
    private GrpcServerProperties grpcProperties;

    @Bean
    @ConditionalOnProperty(value = "grpc.server.model", havingValue = GrpcServerProperties.SERVER_MODEL_TLS)
    public ServerBuilder getServerBuilder() throws SSLException {
        GrpcServerProperties.ServerProperties server = grpcProperties.getServer();
        ServerBuilder serverBuilder;

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

        NettyServerBuilder nettyServerBuilder = NettyServerBuilder.forAddress(new InetSocketAddress(host, port));
        nettyServerBuilder.sslContext(buildSslContext(certChainFilePath, privateKeyFilePath, null));

        serverBuilder = nettyServerBuilder;

        return serverBuilder;
    }

    private SslContext buildSslContext(String certChainFilePath, String privateKeyFilePath, String trustCertCollectionFilePath) throws SSLException {
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

        SslContextBuilder sslContextBuilder = GrpcSslContexts.configure(sslClientContextBuilder, SslProvider.OPENSSL);
        return sslContextBuilder.build();
    }
}
