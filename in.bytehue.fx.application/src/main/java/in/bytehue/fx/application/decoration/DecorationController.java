package in.bytehue.fx.application.decoration;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.ToolBar;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class DecorationController implements Initializable {
    private double mouseDragOffsetX = 0;
    private double mouseDragOffsetY = 0;

    @FXML
    private ToolBar decorationArea;

    private Rectangle2D backupWindowBounds;

    public DecorationController() {
        System.err.println("Creating controller ....");
    }

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        decorationArea.setOnMousePressed(event -> {
            mouseDragOffsetX = event.getSceneX();
            mouseDragOffsetY = event.getSceneY();
        });
        decorationArea.setOnMouseDragged(event -> {
            final Stage w = getStage();
            w.setX(event.getScreenX() - mouseDragOffsetX);
            w.setY(event.getScreenY() - mouseDragOffsetY);
        });
    }

    @FXML
    public void handleClose(final ActionEvent event) {
        Platform.exit();
    }

    @FXML
    public void handleMin(final ActionEvent event) {
        getStage().setIconified(true);
    }

    @FXML
    public void handleMax(final ActionEvent event) {
        final Stage       stage  = getStage();
        final double      stageY = stage.getY();
        final Screen      screen = Screen.getScreensForRectangle(stage.getX(), stageY, 1, 1).get(0);
        final Rectangle2D bounds = screen.getVisualBounds();
        if (bounds.getMinX() == stage.getX() && bounds.getMinY() == stageY && bounds.getWidth() == stage.getWidth()
                && bounds.getHeight() == stage.getHeight()) {
            if (backupWindowBounds != null) {
                stage.setX(backupWindowBounds.getMinX());
                stage.setY(backupWindowBounds.getMinY());
                stage.setWidth(backupWindowBounds.getWidth());
                stage.setHeight(backupWindowBounds.getHeight());
            }
        } else {
            backupWindowBounds = new Rectangle2D(stage.getX(), stage.getY(), stage.getWidth(), stage.getHeight());
            final double newStageY = screen.getVisualBounds().getMinY();
            stage.setX(screen.getVisualBounds().getMinX());
            stage.setY(newStageY);
            stage.setWidth(screen.getVisualBounds().getWidth());
            stage.setHeight(screen.getVisualBounds().getHeight());
        }
    }

    Stage getStage() {
        return (Stage) decorationArea.getScene().getWindow();
    }
}
