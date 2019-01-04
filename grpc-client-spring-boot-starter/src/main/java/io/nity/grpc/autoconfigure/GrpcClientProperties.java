package io.nity.grpc.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@ConfigurationProperties(prefix = "grpc", ignoreUnknownFields = true)
public class GrpcClientProperties {

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
     * custom Channel
     */
    public static final String SERVER_MODEL_CUSTOM = "custom";

    @NestedConfigurationProperty
    private final StubProperties stub = new StubProperties();

    public class StubProperties {

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

    public StubProperties getStub() {
        return stub;
    }
}
