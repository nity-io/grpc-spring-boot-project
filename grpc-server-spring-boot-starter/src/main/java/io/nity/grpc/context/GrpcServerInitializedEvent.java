package io.nity.grpc.context;

import io.grpc.Server;
import org.springframework.context.ApplicationEvent;

public class GrpcServerInitializedEvent extends ApplicationEvent {

    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public GrpcServerInitializedEvent(Server source) {
        super(source);
    }

    public Server getServer() {
        return (Server) getSource();
    }
}
