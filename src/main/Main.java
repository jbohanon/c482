package main;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.jetbrains.annotations.NotNull;

public class Main extends Application {

    private static final Inventory _fullInventory = new Inventory();
    private static Inventory _displayedInventory = new Inventory();

    private static Stage addPartStage;

    private static void preLoadInventory() {
        _fullInventory.addPart(new Outsourced(1, "Part 1", 5, 5, 5, 300, "Joe's Hardware"));
        _fullInventory.addPart(new Outsourced(2, "Part 2", 10, 25, 5, 200, "Jane's Supply"));
        _fullInventory.addPart(new InHouse(3, "Part 3", 6.37, 12, 10, 50, 103));
        _fullInventory.addPart(new InHouse(4, "Part 4", 0.12, 60, 20, 75, 105));
        _fullInventory.addProduct(new Product(1, "Product 1", 12.99, 60, 20, 75));
        _fullInventory.addProduct(new Product(2, "Product 2", 109.63, 5, 3, 10));

        _displayedInventory = _fullInventory;
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

    // Main Screen Def
    private @NotNull
    Scene mainScreenDef(Stage primaryStage, BorderPane root) throws Exception {

        Scene scene = new Scene(root, 890, 350);

        try {
            HBox titlePane = addTitleHbox();
            root.setTop(titlePane);

            GridPane partsGrid = addPartsGrid();
            root.setLeft(partsGrid);

            GridPane productsGrid = addProductsGrid();
            root.setRight(productsGrid);

            HBox exitBtn = addExitBtn();
            root.setBottom(exitBtn);
        } catch (Exception e) {
            throw e;
        }
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
    HBox addExitBtn() {
        HBox hbox = new HBox();

        hbox.setPadding(new Insets(0, 0, 30, 550));
        hbox.setSpacing(10);

        Button exitBtn = new Button("Exit");
        exitBtn.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));
        exitBtn.setOnMouseClicked(mouseEvent -> {
            Platform.exit();
        });

        hbox.getChildren().add(exitBtn);

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
        grid.setPadding(new Insets(10));
        grid.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(4), BorderWidths.DEFAULT)));

        Text partsText = new Text("Parts");
        partsText.minWidth(Control.USE_PREF_SIZE);
        partsText.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));
        partsText.setTextAlignment(TextAlignment.LEFT);

        grid.add(partsText, 0, 0, 1, 1);

        Button searchBtn = new Button("Search");
        grid.add(searchBtn, 2, 0, 1, 1);

        TextField searchArea = new TextField();
        grid.add(searchArea, 3, 0, 1, 1);

        TableView<Part> partTableView = new TableView<>(_displayedInventory.getAllParts());
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
        partPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        partPrice.setCellFactory(formatCurrencyCellFactoryPart());
        partTableView.getColumns().add(partPrice);

        grid.add(partTableView, 0,1,4,1);

        Pane spacer1 = new Pane();
        spacer1.setPrefWidth(100);
        grid.add(spacer1, 0, 2, 1, 1);

        Button addBtn = new Button("Add");
        addBtn.setPrefWidth(60);
        addBtn.setAlignment(Pos.CENTER);
        addBtn.setOnMouseClicked(mouseEvent -> {
            addPartStageDef();
        });
        grid.add(addBtn, 1, 2, 1, 1);

        Button modifyBtn = new Button("Modify");
        modifyBtn.setPrefWidth(60);
        grid.add(modifyBtn, 2, 2, 1, 1);

        Button delBtn = new Button("Delete");
        delBtn.setPrefWidth(60);
        delBtn.setOnMouseClicked(mouseEvent -> {
            _fullInventory.deletePart(partTableView.getSelectionModel().getSelectedItem());
            _displayedInventory = _fullInventory;
        });
        grid.add(delBtn, 3, 2, 1, 1);

        return grid;
    }

    private @NotNull
    GridPane addProductsGrid() {
        GridPane grid = new GridPane();

//        grid.setGridLinesVisible(true);

        grid.setMaxWidth(Control.USE_PREF_SIZE);
        grid.setHgap(5);
        grid.setVgap(5);
        grid.setMinSize(350, 150);
        grid.setMaxSize(401, 190);

        grid.setTranslateX(-30);
        grid.setTranslateY(30);
        grid.setPadding(new Insets(10));
        grid.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(4), BorderWidths.DEFAULT)));

        Text productsText = new Text("Products");
        productsText.minWidth(Control.USE_PREF_SIZE);
        productsText.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));
        productsText.setTextAlignment(TextAlignment.LEFT);

        grid.add(productsText, 0, 0, 1, 1);

        Button searchBtn = new Button("Search");
        grid.add(searchBtn, 2, 0, 1, 1);

        TextField searchArea = new TextField();
        grid.add(searchArea, 3, 0, 1, 1);

        TableView<Product> productTableView = new TableView<>(_displayedInventory.getAllProducts());
        productTableView.setMinSize(380, 110);
        productTableView.setMaxSize(425, 128);


        TableColumn<Product, String> productID = new TableColumn<>("Product ID");
        productID.setCellValueFactory(new PropertyValueFactory<>("id"));
        productTableView.getColumns().add(productID);

        TableColumn<Product, String> productName = new TableColumn<>("Product Name");
        productName.setCellValueFactory(new PropertyValueFactory<>("name"));
        productTableView.getColumns().add(productName);

        TableColumn<Product, String> productInventory = new TableColumn<>("Inventory Level");
        productInventory.setCellValueFactory(new PropertyValueFactory<>("stock"));
        productTableView.getColumns().add(productInventory);

        TableColumn<Product, Double> productPrice = new TableColumn<>("Price per Unit");
        productPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        productPrice.setCellFactory(formatCurrencyCellFactoryProduct());
        productTableView.getColumns().add(productPrice);

        grid.add(productTableView, 0,1,4,1);

        Pane spacer1 = new Pane();
        spacer1.setPrefWidth(100);
        grid.add(spacer1, 0, 2, 1, 1);

        Button addBtn = new Button("Add");
        addBtn.setPrefWidth(60);
        addBtn.setAlignment(Pos.CENTER);
        grid.add(addBtn, 1, 2, 1, 1);

        Button modifyBtn = new Button("Modify");
        modifyBtn.setPrefWidth(60);
        grid.add(modifyBtn, 2, 2, 1, 1);

        Button delBtn = new Button("Delete");
        delBtn.setPrefWidth(60);
        delBtn.setOnMouseClicked(mouseEvent -> {
            _fullInventory.deleteProduct(productTableView.getSelectionModel().getSelectedItem());
            _displayedInventory = _fullInventory;
        });
        grid.add(delBtn, 3, 2, 1, 1);

        return grid;
    }

    private Callback<TableColumn<Part, Double>, TableCell<Part, Double>> formatCurrencyCellFactoryPart() {
        return col -> new TableCell<>() {
            @Override
            public void updateItem(final Double item, boolean empty) {
                if (item != null) {
                    setText(String.format("$%1$,.2f", item));
                } else {
                    setText("");
                }
            }
        };
    }

    private Callback<TableColumn<Product, Double>, TableCell<Product, Double>> formatCurrencyCellFactoryProduct() {
        return col -> new TableCell<>() {
            @Override
            public void updateItem(final Double item, boolean empty) {
                if (item != null) {
                    setText(String.format("$%1$,.2f", item));
                } else {
                    setText("");
                }
            }
        };
    }

    // Add Part Screen Def
    private void addPartStageDef() {
        addPartStage = new Stage();
        addPartStage.setAlwaysOnTop(true);
//        addPartStage.initModality(Modality.WINDOW_MODAL);
        addPartStage.setResizable(false);
        Scene addPartScreen = addPartScreenDef(new StackPane());
        addPartStage.setScene(addPartScreen);
        addPartStage.show();
    }

    private @NotNull
    Scene addPartScreenDef(StackPane root) {
        Scene scene = new Scene(root, 350, 500);

        Button btn = new Button("Woohoo");
        btn.setOnMouseClicked(mouseEvent -> {
            addPartStage.close();
        });
        root.getChildren().add(btn);

        return scene;
    }

    public static void main(String[] args) {
        launch(args);

    }
}
