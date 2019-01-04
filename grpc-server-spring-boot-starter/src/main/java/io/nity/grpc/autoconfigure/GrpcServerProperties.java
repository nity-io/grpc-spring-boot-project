package io.nity.grpc.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@ConfigurationProperties(prefix = "grpc", ignoreUnknownFields = true)
public class GrpcServerProperties {

    public static final int DEFAULT_SERVER_PORT = 50051;

    /**
     * fully-featured, high performance, useful in testing
     */
    public static final String SERVER_MODEL_IN_PROCESS = "inProcess";

    /**
     * plaintext without TLS.
     * While this is convenient for testing environments, users must be aware of the security risks of doing so for real production systems.
     */
    public static final String SERVER_MODEL_SIMPLE = "simple";

    /**
     * service with TLS, safely use to talk to external systems
     */
    public static final String SERVER_MODEL_TLS = "tls";

    /**
     * custom ServerBuilder
     */
    public static final String SERVER_MODEL_CUSTOM = "custom";


    @NestedConfigurationProperty
    private final ServerProperties server = new ServerProperties();

    public class ServerProperties {

        /**
         * gRPC running model, default simple
         */
        private String model = SERVER_MODEL_SIMPLE;

        /**
         * gRPC server host
         */
        private String host;

        /**
         * gRPC server port
         */
        private int port = DEFAULT_SERVER_PORT;

        /**
         * In process server name.
         */
        private String inProcessServerName;

        /**
         * Enables server reflection using <a href="https://github.com/grpc/grpc-java/blob/master/documentation/server-reflection-tutorial.md">ProtoReflectionService</a>.
         * Available only from gRPC 1.3 or higher.
         */
        private boolean enableReflection = false;

        /**
         * gRPC tls server certChainFilePath
         */
        private String certChainFilePath;

        /**
         * gRPC tls server privateKeyFilePath
         */
        private String privateKeyFilePath;

        /**
         * gRPC tls server trustCertCollectionFilePath
         */
        private String trustCertCollectionFilePath;

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public String getInProcessServerName() {
            return inProcessServerName;
        }

        public void setInProcessServerName(String inProcessServerName) {
            this.inProcessServerName = inProcessServerName;
        }

        public boolean isEnableReflection() {
            return enableReflection;
        }

        public void setEnableReflection(boolean enableReflection) {
            this.enableReflection = enableReflection;
        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public String getCertChainFilePath() {
            return certChainFilePath;
        }

        public void setCertChainFilePath(String certChainFilePath) {
            this.certChainFilePath = certChainFilePath;
        }

        public String getPrivateKeyFilePath() {
            return privateKeyFilePath;
        }

        public void setPrivateKeyFilePath(String privateKeyFilePath) {
            this.privateKeyFilePath = privateKeyFilePath;
        }

        public String getTrustCertCollectionFilePath() {
            return trustCertCollectionFilePath;
        }

        public void setTrustCertCollectionFilePath(String trustCertCollectionFilePath) {
            this.trustCertCollectionFilePath = trustCertCollectionFilePath;
        }

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }
    }

    public ServerProperties getServer() {
        return server;
    }

}
