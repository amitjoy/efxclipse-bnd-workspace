package in.bytehue.fx.application.parts;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;

import in.bytehue.fx.application.handlers.RefreshHandler;
import in.bytehue.fx.application.model.Media;
import in.bytehue.fx.application.model.MediaType;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

public class MediaPart {
    public static final String MEDIA_OBJECT_KEY = "MediaObject";

    private static final String KEY_TRANSLATE_X  = "translateX";
    private static final String KEY_TRANSLATE_Y  = "translateY";
    private static final String KEY_SCALE_FACTOR = "scale";

    @Inject
    MPart part;

    private MediaPlayer mediaPlayer;

    private Node focusNode;

    @PostConstruct
    void init(final BorderPane p) {
        final Media m = Media.deserialize(part.getPersistedState().get(MEDIA_OBJECT_KEY));
        if (m.getType() == MediaType.PICTURE) {
            initPicture(p, m);
        } else if (m.getType() == MediaType.MOVIE) {
            initMovie(p, m);
        } else {
            initSound(p, m);
        }
    }

    private void initPicture(final BorderPane p, final Media m) {
        final AnchorPane transformStack = new AnchorPane();
        final String     scaleFactor    = part.getPersistedState().get(KEY_SCALE_FACTOR);
        final String     translateX     = part.getPersistedState().get(KEY_TRANSLATE_X);
        final String     translateY     = part.getPersistedState().get(KEY_TRANSLATE_Y);

        if (scaleFactor != null) {
            transformStack.setScaleX(Double.parseDouble(scaleFactor));
            transformStack.setScaleY(Double.parseDouble(scaleFactor));
        }

        if (translateX != null) {
            transformStack.setTranslateX(Double.parseDouble(translateX));
        }

        if (translateY != null) {
            transformStack.setTranslateY(Double.parseDouble(translateY));
        }

        final ImageView v = new ImageView(m.getUrl());
        focusNode = v;
        transformStack.getChildren().add(v);
        p.setCenter(transformStack);
        p.setOnScroll(event -> {
            final int    direction = event.getDeltaY() < 0 || event.isShiftDown() ? -1 : 1;
            final double val       = Math.max(transformStack.getScaleX() + 0.05 * direction, 0.1);
            transformStack.setScaleX(val);
            transformStack.setScaleY(val);
            part.getPersistedState().put(KEY_SCALE_FACTOR, val + "");
        });

        final AtomicReference<MouseEvent> deltaEvent = new AtomicReference<>();
        p.setOnMousePressed(event -> deltaEvent.set(event));

        p.setOnMouseDragged(event -> {
            final double deltaX = event.getX() - deltaEvent.get().getX();
            final double deltaY = event.getY() - deltaEvent.get().getY();

            final double targetX = transformStack.getTranslateX() + deltaX;
            final double targetY = transformStack.getTranslateY() + deltaY;
            transformStack.setTranslateX(targetX);
            transformStack.setTranslateY(targetY);
            part.getPersistedState().put(KEY_TRANSLATE_X, targetX + "");
            part.getPersistedState().put(KEY_TRANSLATE_Y, targetY + "");

            deltaEvent.set(event);
        });
    }

    private void initMovie(final BorderPane p, final Media m) {
        mediaPlayer = new MediaPlayer(new javafx.scene.media.Media(platformUriFix(m.getUrl())));
        mediaPlayer.setAutoPlay(true);

        final MediaView mediaView = new MediaView(mediaPlayer);
        focusNode = mediaView;
        p.setCenter(mediaView);

        p.setOnScroll(event -> {
            final int    direction = event.getDeltaY() < 0 || event.isShiftDown() ? -1 : 1;
            final double val       = Math.max(mediaView.getScaleX() + 0.05 * direction, 0.1);
            mediaView.setScaleX(val);
            mediaView.setScaleY(val);
            part.getPersistedState().put(KEY_SCALE_FACTOR, val + "");
        });
    }

    @Inject
    @Optional
    void refresh(@UIEventTopic(RefreshHandler.REFRESH_EVENT) final String event) {
        if (part.getParent().getSelectedElement() == part && mediaPlayer != null) {
            mediaPlayer.seek(mediaPlayer.getStartTime());
        }
    }

    @Focus
    void focus() {
        if (focusNode != null) {
            focusNode.requestFocus();
        }
    }

    private String platformUriFix(final String uri) {
        if (!uri.startsWith("platform:")) {
            return uri;
        }
        try {
            final URL              url = new URL(uri);
            final InputStream      in  = url.openStream();
            final byte[]           buf = new byte[1024];
            int                    l;
            final File             f   = File.createTempFile("movie", "");
            final FileOutputStream out = new FileOutputStream(f);
            while ((l = in.read(buf)) != -1) {
                out.write(buf, 0, l);
            }
            out.close();
            return f.toURI().toURL().toExternalForm();
        } catch (final IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return uri;
    }

    private void initSound(final BorderPane p, final Media m) {

    }
}
