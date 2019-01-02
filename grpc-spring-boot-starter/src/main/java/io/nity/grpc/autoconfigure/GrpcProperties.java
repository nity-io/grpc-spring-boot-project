package io.nity.grpc.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@ConfigurationProperties(prefix = "grpc", ignoreUnknownFields = true)
public class GrpcProperties {

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
     * service with TLS with mutual, safely use to talk to external systems
     */
    public static final String SERVER_MODEL_TLS_MUTUAL = "tlsMutual";


    @NestedConfigurationProperty
    private final ServerProperties server = new ServerProperties();

    @NestedConfigurationProperty
    private final StubProperties stub = new StubProperties();

    public class ServerProperties {

        /**
         * Enables the embedded grpc server.
         */
        private boolean enabled = true;

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

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
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

    public class StubProperties {

        /**
         * Enables the embedded grpc server.
         */
        private boolean enabled = true;

        /**
         * gRPC running model, default simple
         */
        private String model = SERVER_MODEL_SIMPLE;

        /**
         * In process server name.
         */
        private String inProcessServerName;

        /**
         * gRPC stub host
         */
        private String host = "";

        /**
         * gRPC stub port
         */
        private int port = DEFAULT_SERVER_PORT;

        private String trustCertCollectionFilePath;
        private String clientCertChainFilePath;
        private String clientPrivateKeyFilePath;

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getTrustCertCollectionFilePath() {
            return trustCertCollectionFilePath;
        }

        public void setTrustCertCollectionFilePath(String trustCertCollectionFilePath) {
            this.trustCertCollectionFilePath = trustCertCollectionFilePath;
        }

        public String getClientCertChainFilePath() {
            return clientCertChainFilePath;
        }

        public void setClientCertChainFilePath(String clientCertChainFilePath) {
            this.clientCertChainFilePath = clientCertChainFilePath;
        }

        public String getClientPrivateKeyFilePath() {
            return clientPrivateKeyFilePath;
        }

        public void setClientPrivateKeyFilePath(String clientPrivateKeyFilePath) {
            this.clientPrivateKeyFilePath = clientPrivateKeyFilePath;
        }

        public String getInProcessServerName() {
            return inProcessServerName;
        }

        public void setInProcessServerName(String inProcessServerName) {
            this.inProcessServerName = inProcessServerName;
        }
    }

    public ServerProperties getServer() {
        return server;
    }

    public StubProperties getStub() {
        return stub;
    }
}
