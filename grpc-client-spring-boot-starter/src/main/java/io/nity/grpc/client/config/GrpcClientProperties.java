/*
 * Copyright 2019 The nity.io gRPC Spring Boot Project Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.nity.grpc.client.config;

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
