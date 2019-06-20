package org.furcoder.zero_caterpillar.gui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.furcoder.zero_caterpillar.service.ServiceUtils;
import org.reflections.Reflections;

import java.util.*;

public class ServiceGui extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
//        Text text = new Text(50, 50, "Hi");
////        text.setFont(new Font(50));
////
////        text.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event) -> {
////            text.setText(new Date().toString());
////        });
////
////        Group root = new Group(text);
////
////        Scene scene = new Scene(root, 500, 500);
////
////        primaryStage.setTitle("Hello");
////        primaryStage.setScene(scene);
////        primaryStage.show();
        initGui();
    }

    private Set<Class<?>> getServices() {
        Reflections reflections = new Reflections("org.furcoder.zero_caterpillar");

        return reflections.getTypesAnnotatedWith(Service.class);
    }

    private Map<String, Class<?>> getNameClassMap(Set<Class<?>> serviceClasses) {
        Map<String, Class<?>> nameClassMap = new HashMap<>(serviceClasses.size());

        for (Class<?> clz : serviceClasses) {
            Service service = clz.getAnnotation(Service.class);
            nameClassMap.put(service.name(), clz);
        }

        return nameClassMap;
    }

    private void initGui() {
        Map<String, Class<?>> nameClassMap = getNameClassMap(getServices());

        List<CheckBox> checkBoxes = new ArrayList<>(nameClassMap.size());
        for (var entry : nameClassMap.entrySet()) {
            CheckBox checkBox = new CheckBox(entry.getKey());
            checkBox.setIndeterminate(false);
            checkBoxes.add(checkBox);
        }

        GridPane gridPane = new GridPane();
        gridPane.setMinSize(800, 800);
        gridPane.setPadding(new Insets(5, 5, 5, 5));
        gridPane.setVgap(5);
        gridPane.setHgap(5);
        gridPane.setAlignment(Pos.CENTER);

        gridPane.add(new Label("任务选择"), 0, 0);
        VBox checkBoxesVBox = new VBox();
        checkBoxesVBox.setAlignment(Pos.CENTER);
        checkBoxesVBox.setSpacing(10);
        checkBoxesVBox.getChildren().addAll(checkBoxes);
        gridPane.add(checkBoxesVBox, 0, 1);

        Button startServiceButton = new Button("开始任务");
        startServiceButton.setOnMouseClicked(e -> {
            for (var box : checkBoxes) {
                if (box.isSelected()) {
                    Class<?> clz = nameClassMap.get(box.getText());
                    ServiceUtils.instantiate(null);
                }
            }
        });
    }
}
