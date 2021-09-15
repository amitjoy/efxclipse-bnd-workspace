package in.bytehue.fx.application.themes;

import org.eclipse.fx.ui.services.theme.Stylesheet;
import org.eclipse.fx.ui.services.theme.Theme;
import org.eclipse.fx.ui.theme.AbstractTheme;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

@Component(service = Theme.class)
public class DefaultTheme extends AbstractTheme {

    public DefaultTheme() {
        super("theme.default", "Default Theme", DefaultTheme.class.getClassLoader().getResource("css/default.css"));
    }

    @Override
    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    public void registerStylesheet(final Stylesheet stylesheet) {
        super.registerStylesheet(stylesheet);
    }

    @Override
    public void unregisterStylesheet(final Stylesheet stylesheet) {
        super.unregisterStylesheet(stylesheet);
    }

}
