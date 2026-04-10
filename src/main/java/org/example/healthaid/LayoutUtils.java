package org.example.healthaid;

import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

public final class LayoutUtils {

    private LayoutUtils() {
    }

    public static ScrollPane createScrollablePage(VBox content) {
        content.setMaxWidth(Double.MAX_VALUE);
        content.setAlignment(Pos.TOP_CENTER);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setPannable(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        content.prefWidthProperty().bind(scrollPane.widthProperty());

        var css = LayoutUtils.class.getResource("/styles.css");
        if (css != null) {
            scrollPane.getStylesheets().add(css.toExternalForm());
        }

        return scrollPane;
    }
}
