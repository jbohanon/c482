package main;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.jetbrains.annotations.NotNull;

public class Main extends Application {

    private static final Inventory _fullInventory = new Inventory();

    private static void preLoadInventory() {
        _fullInventory.addPart(new Outsourced(1, "Part 1", 5, 5, 5, 300, "Joe's Hardware"));
        _fullInventory.addPart(new Outsourced(2, "Part 2", 10, 25, 5, 200, "Jane's Supply"));
        _fullInventory.addPart(new InHouse(3, "Part 3", 6.37, 12, 10, 50, 103));
        _fullInventory.addPart(new InHouse(4, "Part 4", 0.12, 60, 20, 75, 105));
    }

    @Override
    public void start(@NotNull Stage primaryStage) throws Exception {

        preLoadInventory();
//        Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
        primaryStage.setResizable(false);
        Scene mainScreen = mainScreenDef(primaryStage, new BorderPane());
        primaryStage.setScene(mainScreen);
        primaryStage.show();
    }

    private @NotNull
    Scene mainScreenDef(Stage primaryStage, BorderPane root) throws Exception {
        Scene scene = new Scene(root, 1200, 400);

        HBox titlePane = addTitleHbox();
        root.setTop(titlePane);

        GridPane partsGrid = addPartsGrid();
        root.setLeft(partsGrid);

        return scene;
    }

    private @NotNull
    HBox addTitleHbox() {
        HBox hbox = new HBox();

        hbox.setPadding(new Insets(30, 30, 0, 30));
        hbox.setSpacing(10);

        Text titleText = new Text("Inventory Management System");
        titleText.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));

        hbox.getChildren().add(titleText);

        return hbox;
    }

    private @NotNull
    GridPane addPartsGrid() {
        GridPane grid = new GridPane();

//        grid.setGridLinesVisible(true);

        grid.setMaxWidth(Control.USE_PREF_SIZE);
        grid.setHgap(5);
        grid.setVgap(5);
        grid.setMinSize(350, 150);
        grid.setMaxSize(401, 190);

        grid.setTranslateX(30);
        grid.setTranslateY(30);
//        grid.setMaxHeight(Control.USE_PREF_SIZE);
        grid.setPadding(new Insets(10));
        grid.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(4), BorderWidths.DEFAULT)));

        Text partsText = new Text("Parts");
        partsText.minWidth(Control.USE_PREF_SIZE);
//        partsText.minWidth(Control.USE_PREF_SIZE);
        partsText.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));
        partsText.setTextAlignment(TextAlignment.LEFT);

//        grid.addColumn(2, partsText);
        grid.add(partsText, 0, 0, 1, 1);

        Pane spacer = new Pane();
        spacer.setPrefWidth(100);
        grid.add(spacer, 1, 0, 1, 1);

        Button searchBtn = new Button("Search");
//        searchBtn.setTranslateX(230);
        grid.add(searchBtn, 2, 0, 1, 1);

        TextField searchArea = new TextField();
        grid.add(searchArea, 3, 0, 1, 1);

        TableView<Part> partTableView = new TableView<>(_fullInventory.getAllParts());
        partTableView.setMinSize(380, 110);
        partTableView.setMaxSize(425, 128);


        TableColumn<Part, String> partID = new TableColumn<>("Part ID");
        partID.setCellValueFactory(new PropertyValueFactory<>("id"));
        partTableView.getColumns().add(partID);

        TableColumn<Part, String> partName = new TableColumn<>("Part Name");
        partName.setCellValueFactory(new PropertyValueFactory<>("name"));
        partTableView.getColumns().add(partName);

        TableColumn<Part, String> partInventory = new TableColumn<>("Inventory Level");
        partInventory.setCellValueFactory(new PropertyValueFactory<>("stock"));
        partTableView.getColumns().add(partInventory);

        TableColumn<Part, Double> partPrice = new TableColumn<>("Price/Cost per Unit");
//        partPrice.setText("$");
//        partPrice.setStyle("$");
        partPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        partPrice.setCellFactory(getCustomCellFactory());
        partTableView.getColumns().add(partPrice);

        grid.add(partTableView, 0,1,4,1);

        return grid;
    }

    private Callback<TableColumn<Part, Double>, TableCell<Part, Double>> getCustomCellFactory() {
        return col -> new TableCell<>() {

            @Override
            public void updateItem(final Double item, boolean empty) {
                if (item != null) {
                    setText(String.format("$%1$,.2f", item));
                }
            }
        };
    }

    public static void main(String[] args) {
        launch(args);

    }
}
