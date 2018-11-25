package io.nity.grpc.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@ConfigurationProperties(prefix = "grpc", ignoreUnknownFields = true)
public class GrpcProperties {

    public static final int DEFAULT_GRPC_PORT = 50051;

    @NestedConfigurationProperty
    private final ServerProperties server = new ServerProperties();

    @NestedConfigurationProperty
    private final StubProperties stub = new StubProperties();

    public class ServerProperties {

        /**
         * gRPC server port
         */
        private int port = DEFAULT_GRPC_PORT;

        /**
         * Enables the embedded grpc server.
         */
        private boolean enabled = true;


        /**
         * In process server name.
         * If  the value is not empty, the embedded in-process server will be created and started.
         */
        private String inProcessServerName;

        /**
         * Enables server reflection using <a href="https://github.com/grpc/grpc-java/blob/master/documentation/server-reflection-tutorial.md">ProtoReflectionService</a>.
         * Available only from gRPC 1.3 or higher.
         */
        private boolean enableReflection = false;

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
    }

    public class StubProperties {

        /**
         * gRPC stub host
         */
        private String host = "";

        /**
         * gRPC stub port
         */
        private int port = DEFAULT_GRPC_PORT;

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
    }

    public ServerProperties getServer() {
        return server;
    }

    public StubProperties getStub() {
        return stub;
    }
}
