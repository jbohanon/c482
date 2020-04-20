package main;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

        Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
        primaryStage.setResizable(false);
        Scene mainScreen = mainScreenDef(primaryStage, new BorderPane());
        primaryStage.setScene(mainScreen);
        primaryStage.show();
    }

    private Scene mainScreenDef(Stage primaryStage, BorderPane root) throws Exception {
        Scene scene = new Scene(root, 600, 600);

        HBox titlePane = addTitleHbox();
        root.setTop(titlePane);

        GridPane partsGrid = addPartsGrid();
        root.setLeft(partsGrid);

//        grid.setAlignment(Pos.TOP_LEFT);
//        grid.setHgap(5);
//        grid.setVgap(5);
//        grid.setPadding(new Insets(25, 25, 25, 25));
//        Text sceneTitle = new Text("Inventory Management System");
//        sceneTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
//        grid.add(sceneTitle, 0, 0, 2, 1);
//        Button btn = new Button();
//        btn.setText("Yo, yo, yo");
//        btn.setOnAction(new EventHandler<ActionEvent>() {
//            @Override
//            public void handle(ActionEvent event) {
//                System.out.println("It worked?");
//            }
//        });
//        grid.add(btn, 0, 1);
        return scene;
    }

    private HBox addTitleHbox() {
        HBox hbox = new HBox();

        hbox.setPadding(new Insets(30, 60, 0, 60));
        hbox.setSpacing(10);

        Text titleText = new Text("Inventory Management System");
        titleText.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));

        hbox.getChildren().add(titleText);

        return hbox;
    }

    private GridPane addPartsGrid() {
        GridPane grid = new GridPane();
        grid.setMaxWidth(300);
//        grid.setGridLinesVisible(true);
        grid.setHgap(5);
        grid.setVgap(5);
        grid.setTranslateX(60);
        grid.setTranslateY(30);
        grid.setMaxHeight(Control.USE_PREF_SIZE);
        grid.setPadding(new Insets(10));
        grid.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(4), BorderWidths.DEFAULT)));

        Text partsText = new Text("Parts");
        partsText.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));


        partsText.setTextAlignment(TextAlignment.LEFT);
        grid.add(partsText, 0, 0, 2, 1);

        Button searchBtn = new Button("Search");
        grid.add(searchBtn, 3, 0);

        grid.add(subList("Part ID"), 0,1,1,1);
        grid.add(subList("Part Name"), 1,1,1,1);
        grid.add(subList("Inventory Level"), 2,1,1,1);
        grid.add(subList("Price/Cost per Unit"), 3,1,1,1);

        return grid;
    }

    private GridPane subList(String title) {

        GridPane grid = new GridPane();

        HBox titleHbox = new HBox();
        titleHbox.setAlignment(Pos.CENTER);
        titleHbox.setMaxHeight(Control.USE_PREF_SIZE);
        titleHbox.setBackground(
                new Background(
                        new BackgroundFill(
                                Paint.valueOf("rgb(90%,90%,90%)"),
                                null,
                                new Insets(5, 0, 5, 0)
                        )
                )
        );
        Text titleBox = new Text(title);
        titleBox.setTextAlignment(TextAlignment.CENTER);
        titleHbox.getChildren().add(titleBox);
//        titleBox.
        grid.add(titleHbox, 0, 0);

        ListView<String> listArea = new ListView<String>();
        listArea.setMaxWidth(Control.USE_PREF_SIZE);
//        listArea.setBackground(BackgroundRepeat.REPEAT);
        listArea.setMaxHeight(Control.USE_PREF_SIZE);
        grid.add(listArea, 0, 1);

        return grid;
    }

    public static void main(String[] args) {
        launch(args);

    }
}
