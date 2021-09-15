package in.bytehue.fx.application.model;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Media {
    private final ReadOnlyObjectProperty<MediaType> type;
    private final SimpleStringProperty              name = new SimpleStringProperty(this, "name");
    private final SimpleStringProperty              url  = new SimpleStringProperty(this, "url");

    public Media(final MediaType type) {
        this.type = new SimpleObjectProperty<>(this, "type", type);
    }

    public Media(final MediaType type, final String name, final String url) {
        this(type);
        this.name.set(name);
        this.url.set(url);
    }

    public MediaType getType() {
        return type.get();
    }

    public ReadOnlyObjectProperty<MediaType> type() {
        return type;
    }

    public void setName(final String name) {
        this.name.set(name);
    }

    public String getName() {
        return name.get();
    }

    public StringProperty name() {
        return name;
    }

    public void setUrl(final String url) {
        this.url.set(url);
    }

    public String getUrl() {
        return url.get();
    }

    public StringProperty url() {
        return url;
    }

    public static final String serialize(final Media media) {
        return media.type.getValue().name() + "##_##" + media.name.getValue() + "##_##" + media.url.getValue();
    }

    public static final Media deserialize(final String serializedObject) {
        final String[] parts = serializedObject.split("##_##");
        final Media    m     = new Media(MediaType.valueOf(parts[0]));
        m.setName(parts[1]);
        m.setUrl(parts[2]);
        return m;
    }
}
