package in.bytehue.fx.application.handlers;

import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;

public class OpenHandler {
    public static final String OPEN_EVENT = "media/open";

    @Execute
    public void execute(final IEventBroker broker) throws InvocationTargetException, InterruptedException {
        broker.send(OPEN_EVENT, UUID.randomUUID().toString());
    }
}