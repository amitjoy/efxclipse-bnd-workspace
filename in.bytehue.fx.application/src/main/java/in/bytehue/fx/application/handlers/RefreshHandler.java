package in.bytehue.fx.application.handlers;

import java.util.UUID;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;

public class RefreshHandler {
    public static final String REFRESH_EVENT = "media/refresh";

    @Execute
    public void execute(final IEventBroker broker) {
        broker.send(REFRESH_EVENT, UUID.randomUUID().toString());
    }
}
