package in.bytehue.fx.application.parts;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MBasicFactory;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.model.application.ui.basic.MStackElement;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

import in.bytehue.fx.application.handlers.OpenHandler;
import in.bytehue.fx.application.model.Media;
import in.bytehue.fx.application.model.MediaType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;

public class MediaListPart {
    static class MediaCell extends ListCell<Media> {
        @Override
        protected void updateItem(final Media item, final boolean empty) {
            if (!empty && item != null) {
                setText(item.getName());
                switch (item.getType()) {
                    case MOVIE:
                        setGraphic(new ImageView("platform:/plugin/in.bytehue.fx.application/icons/kaffeine.png"));
                        break;
                    case PICTURE:
                        setGraphic(new ImageView(
                                "platform:/plugin/in.bytehue.fx.application/icons/games-config-background.png"));
                        break;
                    default:
                        setGraphic(new ImageView("platform:/plugin/in.bytehue.fx.application/icons/player-volume.png"));
                        break;
                }
            }
            super.updateItem(item, empty);
        }
    }

    @Inject
    EPartService partService;

    @Inject
    EModelService modelService;

    @Inject
    MApplication application;

    private ListView<Media> list;

    @PostConstruct
    void init(final BorderPane pane) {
        list = new ListView<>(createList());
        list.setId("mediaList");
        list.setCellFactory(param -> new MediaCell());
        list.setOnMouseClicked(event -> {
            if (event.getClickCount() > 1) {
                handleOpen();
            }
            if (event.getButton() == MouseButton.SECONDARY) {
                System.err.println("secondary! Yay!");
            }
        });

        pane.setCenter(list);
    }

    void handleOpen() {
        final MPartStack stack    = (MPartStack) modelService.find("content.stack", application);
        final Media      m        = list.getSelectionModel().getSelectedItem();
        final String     instance = Media.serialize(m);

        for (final MStackElement e : stack.getChildren()) {
            if (e instanceof MPart && instance.equals(e.getPersistedState().get(MediaPart.MEDIA_OBJECT_KEY))) {
                partService.activate((MPart) e);
                return;
            }
        }

        final MPart p = MBasicFactory.INSTANCE.createPart();
        p.setLabel(m.getName());
        if (m.getType() == MediaType.MOVIE) {
            p.setIconURI("platform:/plugin/in.bytehue.fx.application/icons/22/kaffeine.png");
        } else if (m.getType() == MediaType.PICTURE) {
            p.setIconURI("platform:/plugin/in.bytehue.fx.application/icons/22/games-config-background.png");
        } else {
            p.setIconURI("platform:/plugin/in.bytehue.fx.application/icons/22/player-volume.png");
        }

        p.setContributionURI("bundleclass://in.bytehue.fx.application/in.bytehue.fx.application.parts.MediaPart");
        p.getPersistedState().put(MediaPart.MEDIA_OBJECT_KEY, instance);
        stack.getChildren().add(p);
        partService.activate(p, true);
    }

    @Focus
    void focus() {
        list.requestFocus();
    }

    @Inject
    @Optional
    public void openMedia(@UIEventTopic(OpenHandler.OPEN_EVENT) final String event) {
        handleOpen();
    }

    private static ObservableList<Media> createList() {
        final ObservableList<Media> l = FXCollections.observableArrayList();
        l.add(new Media(MediaType.PICTURE, "Desert",
                "platform:/plugin/in.bytehue.fx.application/icons/resources/pics/pic1.jpg"));
        l.add(new Media(MediaType.PICTURE, "Lighthouse",
                "platform:/plugin/in.bytehue.fx.application/icons/resources/pics/pic2.jpg"));
        l.add(new Media(MediaType.MOVIE, "Grog",
                "platform:/plugin/in.bytehue.fx.application/icons/resources/movs/mov1.flv"));
        l.add(new Media(MediaType.MOVIE, "OTN", "http://download.oracle.com/otndocs/products/javafx/oow2010-2.flv"));
        return l;
    }
}
