package main;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.skin.TableViewSkin;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class Main extends Application {

    private static final Inventory _fullInventory = new Inventory();
    private static Inventory _displayedInventory = new Inventory();

    private static Stage addPartStage;
    private static Stage modifyPartStage;
    private static Stage addProductStage;
    private static Stage modifyProductStage;

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
            HBox titlePane = mainTitleHbox();
            root.setTop(titlePane);

            GridPane partsGrid = mainPartsGrid();
            root.setLeft(partsGrid);

            GridPane productsGrid = mainProductsGrid();
            root.setRight(productsGrid);

            HBox exitBtn = mainExitBtn();
            root.setBottom(exitBtn);
        } catch (Exception e) {
            throw e;
        }
        return scene;
    }

    private @NotNull
    HBox mainTitleHbox() {
        HBox hbox = new HBox();

        hbox.setPadding(new Insets(30, 30, 0, 30));
        hbox.setSpacing(10);

        Text titleText = new Text("Inventory Management System");
        titleText.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));

        hbox.getChildren().add(titleText);

        return hbox;
    }

    private @NotNull
    HBox mainExitBtn() {
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
    GridPane mainPartsGrid() {
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
        TextField searchArea = new TextField();
        grid.add(searchBtn, 2, 0, 1, 1);
        grid.add(searchArea, 3, 0, 1, 1);

        TableView<Part> partTableView = new TableView<>(_displayedInventory.getAllParts());
//        TableViewSkin skin = new TableViewSkin(partTableView);
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

        searchBtn.setOnAction(action -> {
            String searchText = searchArea.getText();
            if (searchText.equals("")) {
                _displayedInventory = _fullInventory;
                partTableView.setItems(_displayedInventory.getAllParts());
                partTableView.refresh();
                return;
            }
            Inventory tmpInv = _displayedInventory;
            ObservableList<Product> tmpProd = tmpInv.getAllProducts();

            _displayedInventory = new Inventory();

            tmpProd.forEach(product -> _displayedInventory.addProduct(product));
            ObservableList<Part> tmpList = _fullInventory.lookupPart(searchText);
            tmpList.forEach(part -> _displayedInventory.addPart(part));
            partTableView.setItems(_displayedInventory.getAllParts());
            partTableView.refresh();
        });

        Pane spacer1 = new Pane();
        spacer1.setPrefWidth(100);
        grid.add(spacer1, 0, 2, 1, 1);

        Button addBtn = new Button("Add");
        addBtn.setPrefWidth(60);
        addBtn.setAlignment(Pos.CENTER);
        addBtn.setOnMouseClicked(mouseEvent -> addPartStageDef());
        grid.add(addBtn, 1, 2, 1, 1);

        Button modifyBtn = new Button("Modify");
        modifyBtn.setPrefWidth(60);
        modifyBtn.setOnMouseClicked(mouseEvent -> modifyPartStageDef(partTableView.getSelectionModel().getSelectedItem()));
        grid.add(modifyBtn, 2, 2, 1, 1);

        Button delBtn = new Button("Delete");
        delBtn.setPrefWidth(60);
        delBtn.setOnMouseClicked(mouseEvent -> {
            _fullInventory.deletePart(partTableView.getSelectionModel().getSelectedItem());
            _displayedInventory = _fullInventory;
            partTableView.setItems(_displayedInventory.getAllParts());
            partTableView.refresh();
        });
        grid.add(delBtn, 3, 2, 1, 1);

        return grid;
    }

    private @NotNull
    GridPane mainProductsGrid() {
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
        TextField searchArea = new TextField();
        grid.add(searchBtn, 2, 0, 1, 1);
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

        searchBtn.setOnAction(action -> {
            String searchText = searchArea.getText();
            if (searchText.equals("")) {
                _displayedInventory = _fullInventory;
                productTableView.setItems(_displayedInventory.getAllProducts());
                productTableView.refresh();
                return;
            }
            Inventory tmpInv = _displayedInventory;
            ObservableList<Part> tmpParts = tmpInv.getAllParts();

            _displayedInventory = new Inventory();

            tmpParts.forEach(part -> _displayedInventory.addPart(part));
            ObservableList<Product> tmpList = _fullInventory.lookupProduct(searchText);
            tmpList.forEach(product -> _displayedInventory.addProduct(product));
            productTableView.setItems(_displayedInventory.getAllProducts());
            productTableView.refresh();
        });
        
        Pane spacer1 = new Pane();
        spacer1.setPrefWidth(100);
        grid.add(spacer1, 0, 2, 1, 1);

        Button addBtn = new Button("Add");
        addBtn.setPrefWidth(60);
        addBtn.setAlignment(Pos.CENTER);
        addBtn.setOnAction( actionEvent -> addProductStageDef());
        grid.add(addBtn, 1, 2, 1, 1);

        Button modifyBtn = new Button("Modify");
        modifyBtn.setOnAction(action -> modifyProductStageDef(productTableView.getSelectionModel().getSelectedItem()));
        modifyBtn.setPrefWidth(60);
        grid.add(modifyBtn, 2, 2, 1, 1);

        Button delBtn = new Button("Delete");
        delBtn.setPrefWidth(60);
        delBtn.setOnMouseClicked(mouseEvent -> {
            _fullInventory.deleteProduct(productTableView.getSelectionModel().getSelectedItem());
            _displayedInventory = _fullInventory;
            productTableView.setItems(_displayedInventory.getAllProducts());
            productTableView.refresh();
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
        Scene addPartScreen = addPartScreenDef(new GridPane());
        addPartStage.setScene(addPartScreen);
        addPartStage.show();
    }

    private @NotNull
    Scene addPartScreenDef(GridPane root) {
        Scene scene = new Scene(root, 350, 500);

        root.setPadding(new Insets(20, 30, 20, 30));
        root.setVgap(10);

        GridPane topGrid = new GridPane();

//        root.setGridLinesVisible(true);
//        topGrid.setGridLinesVisible(true);

        Text addPartText = new Text("Add Part");
        addPartText.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));
        topGrid.add(addPartText, 0, 0);

        final ToggleGroup grp = new ToggleGroup();

        RadioButton rb1 = new RadioButton("In-House");
        rb1.setMinWidth(60);
        rb1.setToggleGroup(grp);
        rb1.setSelected(true);

        RadioButton rb2 = new RadioButton("Outsourced");
        rb2.setToggleGroup(grp);

        topGrid.add(rb1, 1, 0);
        topGrid.add(rb2, 2, 0);

        topGrid.setHgap(15);

        root.add(topGrid, 0, 0);

        final int gridSpacing = 100;

        GridPane idGrid = new GridPane();
        Text idLabel = new Text("ID");
        idGrid.setHgap(gridSpacing - idLabel.getLayoutBounds().getWidth());
        TextField idEntry = new TextField();
        idEntry.setPromptText("Auto Gen - Disabled");
        idEntry.setDisable(true);
        idEntry.setEditable(false);
        idGrid.add(idLabel, 0, 0);
        idGrid.add(idEntry, 1, 0);
        root.add(idGrid, 0, 1);

        GridPane nameGrid = new GridPane();
        Text nameLabel = new Text("Name");
        nameGrid.setHgap(gridSpacing - nameLabel.getLayoutBounds().getWidth());
        TextField nameEntry = new TextField();
        nameEntry.setPromptText("Part Name");
        nameGrid.add(nameLabel, 0, 0);
        nameGrid.add(nameEntry, 1, 0);
        root.add(nameGrid, 0, 2);

        GridPane invGrid = new GridPane();
        Text invLabel = new Text("Inv");
        invGrid.setHgap(gridSpacing - invLabel.getLayoutBounds().getWidth());
        TextField invEntry = new TextField();
        invEntry.setPromptText("Inv");
        invGrid.add(invLabel, 0, 0);
        invGrid.add(invEntry, 1, 0);
        root.add(invGrid, 0, 3);

        GridPane pcGrid = new GridPane();
        Text pcLabel = new Text("Price/Cost");
        pcGrid.setHgap(gridSpacing - pcLabel.getLayoutBounds().getWidth());
        TextField pcEntry = new TextField();
        pcEntry.setPromptText("Price/Cost");
        pcGrid.add(pcLabel, 0, 0);
        pcGrid.add(pcEntry, 1, 0);
        root.add(pcGrid, 0, 4);

        GridPane minmaxGrid = new GridPane();
        minmaxGrid.setHgap(25);
        Text maxLabel = new Text("Max");
        TextField maxEntry = new TextField();
        maxEntry.setPromptText("Max");
        minmaxGrid.add(maxLabel, 0, 0);
        minmaxGrid.add(maxEntry, 1, 0);

        Text minLabel = new Text("Min");
        TextField minEntry = new TextField();
        minEntry.setPromptText("Min");
        minmaxGrid.add(minLabel, 2, 0);
        minmaxGrid.add(minEntry, 3, 0);

        root.add(minmaxGrid, 0, 5);

        GridPane machIDGrid = new GridPane();
        Text machIDLabel = new Text("Machine ID");
        machIDGrid.setHgap(gridSpacing - machIDLabel.getLayoutBounds().getWidth());
        TextField machIDEntry = new TextField();
        machIDEntry.setPromptText("Mach ID");
        machIDGrid.add(machIDLabel, 0, 0);
        machIDGrid.add(machIDEntry, 1, 0);

        GridPane compNameGrid = new GridPane();
        Text compNameLabel = new Text("Company Name");
        compNameGrid.setHgap(gridSpacing - compNameLabel.getLayoutBounds().getWidth());
        TextField compNameEntry = new TextField();
        compNameEntry.setPromptText("Comp Nm");
        compNameGrid.add(compNameLabel, 0, 0);
        compNameGrid.add(compNameEntry, 1, 0);

        GridPane btnGrid = new GridPane();
        Pane s1 = new Pane();
        s1.setPrefWidth(50);
        btnGrid.add(s1, 0, 0);

        Button addInBtn = new Button("Add");
        addInBtn.setOnMouseClicked(mouseEvent -> {
            _fullInventory.addPart(new InHouse(
                    getNextPartId(),
                    nameEntry.getText(),
                    Double.parseDouble(pcEntry.getText()),
                    Integer.parseInt(invEntry.getText()),
                    Integer.parseInt(minEntry.getText()),
                    Integer.parseInt(maxEntry.getText()),
                    Integer.parseInt(machIDEntry.getText())
            ));
            _displayedInventory = _fullInventory;
            addPartStage.close();
        });

        Button addOutBtn = new Button("Add");
        addOutBtn.setOnMouseClicked(mouseEvent -> {
            _fullInventory.addPart(new Outsourced(
                    getNextPartId(),
                    nameEntry.getText(),
                    Double.parseDouble(pcEntry.getText()),
                    Integer.parseInt(invEntry.getText()),
                    Integer.parseInt(minEntry.getText()),
                    Integer.parseInt(maxEntry.getText()),
                    compNameEntry.getText()
            ));
            _displayedInventory = _fullInventory;
            addPartStage.close();
        });

        btnGrid.add(addInBtn, 1, 0);

        Pane s2 = new Pane();
        s2.setPrefWidth(50);
        btnGrid.add(s2, 2, 0);

        Button cancelBtn = new Button("Cancel");
        cancelBtn.setOnMouseClicked(mouseEvent -> addPartStage.close());
        btnGrid.add(cancelBtn, 3, 0);

        AtomicBoolean altered = new AtomicBoolean(false);

        root.add(machIDGrid, 0, 6);
        root.add(btnGrid, 0, 7);

        rb1.setOnAction( action -> {
            if(altered.get()) {
                root.getChildren().remove(compNameGrid);
                btnGrid.getChildren().remove(addOutBtn);
            }
            root.add(machIDGrid, 0, 6);
            btnGrid.add(addInBtn, 1, 0);
            altered.set(true);
        });
        rb2.setOnAction( action -> {
            root.getChildren().remove(machIDGrid);
            btnGrid.getChildren().remove(addInBtn);
            root.add(compNameGrid, 0, 6);
            btnGrid.add(addOutBtn, 1, 0);
            altered.set(true);
        });

        return scene;
    }

    // Modify Part Screen Def
    private void modifyPartStageDef(Part modPart) {
        modifyPartStage = new Stage();
        modifyPartStage.setAlwaysOnTop(true);
//        modifyPartStage.initModality(Modality.WINDOW_MODAL);
        modifyPartStage.setResizable(false);
        Scene modifyPartScreen;
        try {
            modifyPartScreen = modifyPartScreenDef(new GridPane(), modPart);
        } catch (Exception e) {
            return;
        }
        modifyPartStage.setScene(modifyPartScreen);
        modifyPartStage.show();
    }

    private @NotNull
    Scene modifyPartScreenDef(GridPane root, Part modPart) throws Exception {
        AtomicReference<InHouse> dispIn = new AtomicReference<>();
        AtomicReference<Outsourced> dispOut = new AtomicReference<>();
        if(modPart.getClass() == InHouse.class) {
            dispIn.set((InHouse) modPart);
        } else {
            dispOut.set((Outsourced) modPart);
        }
        Scene scene = new Scene(root, 350, 500);

        root.setPadding(new Insets(20, 30, 20, 30));
        root.setVgap(10);

        GridPane topGrid = new GridPane();

//        root.setGridLinesVisible(true);
//        topGrid.setGridLinesVisible(true);

        Text modifyPartText = new Text("Modify Part");
        modifyPartText.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));
        topGrid.add(modifyPartText, 0, 0);

        final ToggleGroup grp = new ToggleGroup();

        RadioButton rb1 = new RadioButton("In-House");
        rb1.setMinWidth(60);
        rb1.setToggleGroup(grp);

        RadioButton rb2 = new RadioButton("Outsourced");
        rb2.setToggleGroup(grp);

        if(dispIn.get() != null) {
            rb1.setSelected(true);
        } else {
            rb2.setSelected(true);
        }

        topGrid.add(rb1, 1, 0);
        topGrid.add(rb2, 2, 0);

        topGrid.setHgap(15);

        root.add(topGrid, 0, 0);

        final int gridSpacing = 100;

        GridPane idGrid = new GridPane();
        Text idLabel = new Text("ID");
        idGrid.setHgap(gridSpacing - idLabel.getLayoutBounds().getWidth());
        TextField idEntry = new TextField((String.valueOf(modPart.getId())));
        idEntry.setDisable(true);
        idEntry.setEditable(false);
        idGrid.add(idLabel, 0, 0);
        idGrid.add(idEntry, 1, 0);
        root.add(idGrid, 0, 1);

        GridPane nameGrid = new GridPane();
        Text nameLabel = new Text("Name");
        nameGrid.setHgap(gridSpacing - nameLabel.getLayoutBounds().getWidth());
        TextField nameEntry = new TextField(modPart.getName());
        nameGrid.add(nameLabel, 0, 0);
        nameGrid.add(nameEntry, 1, 0);
        root.add(nameGrid, 0, 2);

        GridPane invGrid = new GridPane();
        Text invLabel = new Text("Inv");
        invGrid.setHgap(gridSpacing - invLabel.getLayoutBounds().getWidth());
        TextField invEntry = new TextField(String.valueOf(modPart.getStock()));
        invGrid.add(invLabel, 0, 0);
        invGrid.add(invEntry, 1, 0);
        root.add(invGrid, 0, 3);

        GridPane pcGrid = new GridPane();
        Text pcLabel = new Text("Price/Cost");
        pcGrid.setHgap(gridSpacing - pcLabel.getLayoutBounds().getWidth());
        TextField pcEntry = new TextField(String.valueOf(modPart.getPrice()));
        pcGrid.add(pcLabel, 0, 0);
        pcGrid.add(pcEntry, 1, 0);
        root.add(pcGrid, 0, 4);

        GridPane minmaxGrid = new GridPane();
        minmaxGrid.setHgap(25);
        Text maxLabel = new Text("Max");
        TextField maxEntry = new TextField(String.valueOf(modPart.getMax()));
        minmaxGrid.add(maxLabel, 0, 0);
        minmaxGrid.add(maxEntry, 1, 0);

        Text minLabel = new Text("Min");
        TextField minEntry = new TextField(String.valueOf(modPart.getMin()));
        minmaxGrid.add(minLabel, 2, 0);
        minmaxGrid.add(minEntry, 3, 0);

        root.add(minmaxGrid, 0, 5);

        GridPane machIDGrid = new GridPane();
        Text machIDLabel = new Text("Machine ID");
        machIDGrid.setHgap(gridSpacing - machIDLabel.getLayoutBounds().getWidth());
        AtomicReference<TextField> machIDEntry = new AtomicReference<>();
        machIDEntry.set(new TextField());
        if(dispIn.get() != null) {
            machIDEntry.set(new TextField(String.valueOf((dispIn.get().getMachineId()))));
        }
        machIDGrid.add(machIDLabel, 0, 0);
        machIDGrid.add(machIDEntry.get(), 1, 0);

        GridPane compNameGrid = new GridPane();
        Text compNameLabel = new Text("Company Name");
        compNameGrid.setHgap(gridSpacing - compNameLabel.getLayoutBounds().getWidth());
        AtomicReference<TextField> compNameEntry = new AtomicReference<>();
        compNameEntry.set(new TextField());
        if(dispOut.get() != null) {
            compNameEntry.set(new TextField(dispOut.get().getCompanyName()));
        }
        compNameGrid.add(compNameLabel, 0, 0);
        compNameGrid.add(compNameEntry.get(), 1, 0);

        GridPane btnGrid = new GridPane();
        Pane s1 = new Pane();
        s1.setPrefWidth(50);
        btnGrid.add(s1, 0, 0);

        Button modInBtn = new Button("Save");
        modInBtn.setOnMouseClicked(mouseEvent -> {
            _fullInventory.updatePart(_fullInventory.getAllParts().indexOf(modPart), new InHouse(
                    modPart.getId(),
                    nameEntry.getText(),
                    Double.parseDouble(pcEntry.getText()),
                    Integer.parseInt(invEntry.getText()),
                    Integer.parseInt(minEntry.getText()),
                    Integer.parseInt(maxEntry.getText()),
                    Integer.parseInt(machIDEntry.get().getText())
            ));
            _displayedInventory = _fullInventory;
            modifyPartStage.close();
        });

        Button modOutBtn = new Button("Save");
        modOutBtn.setOnMouseClicked(mouseEvent -> {
            _fullInventory.updatePart(_fullInventory.getAllParts().indexOf(modPart), new Outsourced(
                    modPart.getId(),
                    nameEntry.getText(),
                    Double.parseDouble(pcEntry.getText()),
                    Integer.parseInt(invEntry.getText()),
                    Integer.parseInt(minEntry.getText()),
                    Integer.parseInt(maxEntry.getText()),
                    compNameEntry.get().getText()
            ));
            _displayedInventory = _fullInventory;
            modifyPartStage.close();
        });

        btnGrid.add(modInBtn, 1, 0);

        Pane s2 = new Pane();
        s2.setPrefWidth(50);
        btnGrid.add(s2, 2, 0);

        Button cancelBtn = new Button("Cancel");
        cancelBtn.setOnMouseClicked(mouseEvent -> modifyPartStage.close());
        btnGrid.add(cancelBtn, 3, 0);

        if(dispIn.get() != null) {
            root.add(machIDGrid, 0, 6);
        } else {
            root.add(compNameGrid, 0, 6);
        }
        root.add(btnGrid, 0, 7);

        rb1.setOnAction( action -> {
            if(root.getChildren().contains(compNameGrid)) {
                root.getChildren().remove(compNameGrid);
                btnGrid.getChildren().remove(modOutBtn);
            }
            dispIn.set(new InHouse(
                    modPart.getId(),
                    nameEntry.getText(),
                    Double.parseDouble(pcEntry.getText()),
                    Integer.parseInt(invEntry.getText()),
                    Integer.parseInt(minEntry.getText()),
                    Integer.parseInt(maxEntry.getText()),
                    -1
            ));
            dispOut.set(null);
            root.add(machIDGrid, 0, 6);
            btnGrid.add(modInBtn, 1, 0);
        });
        rb2.setOnAction( action -> {
            root.getChildren().remove(machIDGrid);
            btnGrid.getChildren().remove(modInBtn);
            dispOut.set(new Outsourced(
                    modPart.getId(),
                    nameEntry.getText(),
                    Double.parseDouble(pcEntry.getText()),
                    Integer.parseInt(invEntry.getText()),
                    Integer.parseInt(minEntry.getText()),
                    Integer.parseInt(maxEntry.getText()),
                    ""
            ));
            dispIn.set(null);
            root.add(compNameGrid, 0, 6);
            btnGrid.add(modOutBtn, 1, 0);
        });

        return scene;
    }

    // Add Product Screen Def
    private void addProductStageDef() {
        addProductStage = new Stage();
        addProductStage.setAlwaysOnTop(true);
//        addPartStage.initModality(Modality.WINDOW_MODAL);
        addProductStage.setResizable(false);
        Scene addProductScreen = addProductScreenDef(new GridPane());
        addProductStage.setScene(addProductScreen);
        addProductStage.show();
    }

    private @NotNull
    Scene addProductScreenDef(GridPane root) {
        Scene scene = new Scene(root, 700, 500);

        GridPane leftPane = new GridPane();

        leftPane.setPadding(new Insets(20, 30, 20, 30));
        leftPane.setVgap(10);

        GridPane topGrid = new GridPane();

//        leftPane.setGridLinesVisible(true);
//        topGrid.setGridLinesVisible(true);

        Text addProductText = new Text("Add Product");
        addProductText.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));
        topGrid.add(addProductText, 0, 0);

        topGrid.setHgap(10);

        leftPane.add(topGrid, 0, 0);

        final int gridSpacing = 50;

        GridPane idGrid = new GridPane();
        Text idLabel = new Text("ID");
        idGrid.setHgap(gridSpacing - idLabel.getLayoutBounds().getWidth());
        TextField idEntry = new TextField();
        idEntry.setPromptText("Auto Gen - Disabled");
        idEntry.setDisable(true);
        idEntry.setEditable(false);
        idGrid.add(idLabel, 0, 0);
        idGrid.add(idEntry, 1, 0);
        leftPane.add(idGrid, 0, 1);

        GridPane nameGrid = new GridPane();
        Text nameLabel = new Text("Name");
        nameGrid.setHgap(gridSpacing - nameLabel.getLayoutBounds().getWidth());
        TextField nameEntry = new TextField();
        nameEntry.setPromptText("Product Name");
        nameGrid.add(nameLabel, 0, 0);
        nameGrid.add(nameEntry, 1, 0);
        leftPane.add(nameGrid, 0, 2);

        GridPane invGrid = new GridPane();
        Text invLabel = new Text("Inv");
        invGrid.setHgap(gridSpacing - invLabel.getLayoutBounds().getWidth());
        TextField invEntry = new TextField();
        invEntry.setPromptText("Inv");
        invGrid.add(invLabel, 0, 0);
        invGrid.add(invEntry, 1, 0);
        leftPane.add(invGrid, 0, 3);

        GridPane pcGrid = new GridPane();
        Text pcLabel = new Text("Price");
        pcGrid.setHgap(gridSpacing - pcLabel.getLayoutBounds().getWidth());
        TextField pcEntry = new TextField();
        pcEntry.setPromptText("Price");
        pcGrid.add(pcLabel, 0, 0);
        pcGrid.add(pcEntry, 1, 0);
        leftPane.add(pcGrid, 0, 4);

        GridPane minmaxGrid = new GridPane();
        minmaxGrid.setHgap(27);
        Text maxLabel = new Text("Max");
        TextField maxEntry = new TextField();
        maxEntry.setPromptText("Max");
        minmaxGrid.add(maxLabel, 0, 0);
        minmaxGrid.add(maxEntry, 1, 0);

        Text minLabel = new Text("Min");
        TextField minEntry = new TextField();
        minEntry.setPromptText("Min");
        minmaxGrid.add(minLabel, 2, 0);
        minmaxGrid.add(minEntry, 3, 0);

        root.add(leftPane, 0,0, 1, 4);

        leftPane.add(minmaxGrid, 0, 5);

        ObservableList<Part> partsList = FXCollections.observableArrayList();

        GridPane rightPane = new GridPane();
        rightPane.setVgap(10);

        GridPane partSearch = new GridPane();
        partSearch.setHgap(15);
        Button searchBtn = new Button("Search");
        partSearch.add(searchBtn, 0, 0);

        TextField searchArea = new TextField();
        partSearch.add(searchArea, 1, 0);

        rightPane.add(partSearch, 0, 0);

        TableView<Part> partTableView1 = new TableView<>(_fullInventory.getAllParts());
        partTableView1.setMinSize(380, 110);
        partTableView1.setMaxSize(425, 128);


        TableColumn<Part, String> partID1 = new TableColumn<>("Part ID");
        partID1.setCellValueFactory(new PropertyValueFactory<>("id"));
        partTableView1.getColumns().add(partID1);

        TableColumn<Part, String> partName1 = new TableColumn<>("Part Name");
        partName1.setCellValueFactory(new PropertyValueFactory<>("name"));
        partTableView1.getColumns().add(partName1);

        TableColumn<Part, String> partInventory1 = new TableColumn<>("Inventory Level");
        partInventory1.setCellValueFactory(new PropertyValueFactory<>("stock"));
        partTableView1.getColumns().add(partInventory1);

        TableColumn<Part, Double> partPrice1 = new TableColumn<>("Price/Cost per Unit");
        partPrice1.setCellValueFactory(new PropertyValueFactory<>("price"));
        partPrice1.setCellFactory(formatCurrencyCellFactoryPart());
        partTableView1.getColumns().add(partPrice1);
        rightPane.add(partTableView1, 0, 1);

        searchBtn.setOnAction(actionEvent -> {
            if(searchArea.getText().equals("")){
                partTableView1.setItems(_fullInventory.getAllParts());
                partTableView1.refresh();
                return;
            }
            partTableView1.setItems(_fullInventory.lookupPart(searchArea.getText()));
            partTableView1.refresh();
        });

        Button addPartBtn = new Button("Add");

        rightPane.add(addPartBtn, 0, 2);

        TableView<Part> partTableView2 = new TableView<>(partsList);
        partTableView2.setMinSize(380, 110);
        partTableView2.setMaxSize(425, 128);


        TableColumn<Part, String> partID = new TableColumn<>("Part ID");
        partID.setCellValueFactory(new PropertyValueFactory<>("id"));
        partTableView2.getColumns().add(partID);

        TableColumn<Part, String> partName = new TableColumn<>("Part Name");
        partName.setCellValueFactory(new PropertyValueFactory<>("name"));
        partTableView2.getColumns().add(partName);

        TableColumn<Part, String> partInventory = new TableColumn<>("Inventory Level");
        partInventory.setCellValueFactory(new PropertyValueFactory<>("stock"));
        partTableView2.getColumns().add(partInventory);

        TableColumn<Part, Double> partPrice = new TableColumn<>("Price/Cost per Unit");
        partPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        partPrice.setCellFactory(formatCurrencyCellFactoryPart());
        partTableView2.getColumns().add(partPrice);
        rightPane.add(partTableView2, 0, 3);

        addPartBtn.setOnAction(action -> {
            partsList.add(partTableView1.getSelectionModel().getSelectedItem());
            partTableView2.setItems(partsList);
            partTableView2.refresh();
        });

        Button delPartBtn = new Button("Delete");
        delPartBtn.setOnAction(action -> {
            partsList.remove(partTableView2.getSelectionModel().getSelectedItem());
            partTableView2.setItems(partsList);
            partTableView2.refresh();
        });
        rightPane.add(delPartBtn, 0, 4);

        GridPane btnGrid = new GridPane();
        Pane s1 = new Pane();
        s1.setPrefWidth(50);
//        btnGrid.add(s1, 0, 0);

        Button addProductBtn = new Button("Save");
        addProductBtn.setOnMouseClicked(mouseEvent -> {
            Product newProd = new Product(
                    getNextProductId(),
                    nameEntry.getText(),
                    Double.parseDouble(pcEntry.getText()),
                    Integer.parseInt(invEntry.getText()),
                    Integer.parseInt(minEntry.getText()),
                    Integer.parseInt(maxEntry.getText()));
            for (Part part : partsList) {
                newProd.addAssociatedPart(part);
            }
            _fullInventory.addProduct(newProd);
            _displayedInventory = _fullInventory;
            addProductStage.close();
        });

        btnGrid.add(addProductBtn, 0, 0);

        Pane s2 = new Pane();
        s2.setPrefWidth(50);
        btnGrid.add(s2, 1, 0);

        Button cancelBtn = new Button("Cancel");
        cancelBtn.setOnMouseClicked(mouseEvent -> addProductStage.close());
        btnGrid.add(cancelBtn, 2, 0);

        rightPane.add(btnGrid, 0, 5);

        root.add(rightPane, 1,0, 1, 4);

        return scene;
    }


    // Modify Product Screen Def
    private void modifyProductStageDef(Product modProd) {
        modifyProductStage = new Stage();
        modifyProductStage.setAlwaysOnTop(true);
//        modifyPartStage.initModality(Modality.WINDOW_MODAL);
        modifyProductStage.setResizable(false);
        Scene modifyProductScreen = modifyProductScreenDef(new GridPane(), modProd);
        modifyProductStage.setScene(modifyProductScreen);
        modifyProductStage.show();
    }

    private @NotNull
    Scene modifyProductScreenDef(GridPane root, Product modProd) {
        Scene scene = new Scene(root, 700, 500);

        GridPane leftPane = new GridPane();

        leftPane.setPadding(new Insets(20, 30, 20, 30));
        leftPane.setVgap(10);

        GridPane topGrid = new GridPane();

//        leftPane.setGridLinesVisible(true);
//        topGrid.setGridLinesVisible(true);

        Text modifyProductText = new Text("Modify Product");
        modifyProductText.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));
        topGrid.add(modifyProductText, 0, 0);

        topGrid.setHgap(10);

        leftPane.add(topGrid, 0, 0);

        final int gridSpacing = 50;

        GridPane idGrid = new GridPane();
        Text idLabel = new Text("ID");
        idGrid.setHgap(gridSpacing - idLabel.getLayoutBounds().getWidth());
        TextField idEntry = new TextField();
        idEntry.setPromptText("Auto Gen - Disabled");
        idEntry.setText(Integer.toString(modProd.getId()));
        idEntry.setDisable(true);
        idEntry.setEditable(false);
        idGrid.add(idLabel, 0, 0);
        idGrid.add(idEntry, 1, 0);
        leftPane.add(idGrid, 0, 1);

        GridPane nameGrid = new GridPane();
        Text nameLabel = new Text("Name");
        nameGrid.setHgap(gridSpacing - nameLabel.getLayoutBounds().getWidth());
        TextField nameEntry = new TextField(modProd.getName());
        nameEntry.setPromptText("Product Name");
        nameGrid.add(nameLabel, 0, 0);
        nameGrid.add(nameEntry, 1, 0);
        leftPane.add(nameGrid, 0, 2);

        GridPane invGrid = new GridPane();
        Text invLabel = new Text("Inv");
        invGrid.setHgap(gridSpacing - invLabel.getLayoutBounds().getWidth());
        TextField invEntry = new TextField(Integer.toString(modProd.getStock()));
        invEntry.setPromptText("Inv");
        invGrid.add(invLabel, 0, 0);
        invGrid.add(invEntry, 1, 0);
        leftPane.add(invGrid, 0, 3);

        GridPane pcGrid = new GridPane();
        Text pcLabel = new Text("Price");
        pcGrid.setHgap(gridSpacing - pcLabel.getLayoutBounds().getWidth());
        TextField pcEntry = new TextField(Double.toString(modProd.getPrice()));
        pcEntry.setPromptText("Price");
        pcGrid.add(pcLabel, 0, 0);
        pcGrid.add(pcEntry, 1, 0);
        leftPane.add(pcGrid, 0, 4);

        GridPane minmaxGrid = new GridPane();
        minmaxGrid.setHgap(27);
        Text maxLabel = new Text("Max");
        maxLabel.minWidth(100);
        maxLabel.setTextAlignment(TextAlignment.CENTER);
        TextField maxEntry = new TextField(Integer.toString(modProd.getMax()));
        maxEntry.setPromptText("Max");
        minmaxGrid.add(maxLabel, 0, 0);
        minmaxGrid.add(maxEntry, 1, 0);

        Text minLabel = new Text("Min");
        minLabel.minWidth(100);
        minLabel.setTextAlignment(TextAlignment.CENTER);
        TextField minEntry = new TextField(Integer.toString(modProd.getMin()));
        minEntry.setPromptText("Min");
        minmaxGrid.add(minLabel, 2, 0);
        minmaxGrid.add(minEntry, 3, 0);

        root.add(leftPane, 0,0, 1, 4);

        leftPane.add(minmaxGrid, 0, 5);

        ObservableList<Part> partsList = modProd.getAllAssociatedParts();

        GridPane rightPane = new GridPane();
        rightPane.setVgap(10);

        GridPane partSearch = new GridPane();
        Button searchBtn = new Button("Search");
        partSearch.add(searchBtn, 0, 0);

        TextField searchArea = new TextField();
        partSearch.add(searchArea, 1, 0);

        rightPane.add(partSearch, 0, 0);

        TableView<Part> partTableView1 = new TableView<>(_fullInventory.getAllParts());
        partTableView1.setMinSize(380, 110);
        partTableView1.setMaxSize(425, 128);


        TableColumn<Part, String> partID1 = new TableColumn<>("Part ID");
        partID1.setCellValueFactory(new PropertyValueFactory<>("id"));
        partTableView1.getColumns().add(partID1);

        TableColumn<Part, String> partName1 = new TableColumn<>("Part Name");
        partName1.setCellValueFactory(new PropertyValueFactory<>("name"));
        partTableView1.getColumns().add(partName1);

        TableColumn<Part, String> partInventory1 = new TableColumn<>("Inventory Level");
        partInventory1.setCellValueFactory(new PropertyValueFactory<>("stock"));
        partTableView1.getColumns().add(partInventory1);

        TableColumn<Part, Double> partPrice1 = new TableColumn<>("Price/Cost per Unit");
        partPrice1.setCellValueFactory(new PropertyValueFactory<>("price"));
        partPrice1.setCellFactory(formatCurrencyCellFactoryPart());
        partTableView1.getColumns().add(partPrice1);
        rightPane.add(partTableView1, 0, 1);

        searchBtn.setOnAction(actionEvent -> {
            if(searchArea.getText().equals("")){
                partTableView1.setItems(_fullInventory.getAllParts());
                partTableView1.refresh();
                return;
            }
            partTableView1.setItems(_fullInventory.lookupPart(searchArea.getText()));
            partTableView1.refresh();
        });

        Button addPartBtn = new Button("Add");

        rightPane.add(addPartBtn, 0, 2);

        TableView<Part> partTableView2 = new TableView<>(partsList);
        partTableView2.setMinSize(380, 110);
        partTableView2.setMaxSize(425, 128);


        TableColumn<Part, String> partID = new TableColumn<>("Part ID");
        partID.setCellValueFactory(new PropertyValueFactory<>("id"));
        partTableView2.getColumns().add(partID);

        TableColumn<Part, String> partName = new TableColumn<>("Part Name");
        partName.setCellValueFactory(new PropertyValueFactory<>("name"));
        partTableView2.getColumns().add(partName);

        TableColumn<Part, String> partInventory = new TableColumn<>("Inventory Level");
        partInventory.setCellValueFactory(new PropertyValueFactory<>("stock"));
        partTableView2.getColumns().add(partInventory);

        TableColumn<Part, Double> partPrice = new TableColumn<>("Price/Cost per Unit");
        partPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        partPrice.setCellFactory(formatCurrencyCellFactoryPart());
        partTableView2.getColumns().add(partPrice);
        rightPane.add(partTableView2, 0, 3);

        addPartBtn.setOnAction(action -> {
            Part newPart = partTableView1.getSelectionModel().getSelectedItem();
//            partsList.add(newPart);
            modProd.addAssociatedPart(newPart);
            partTableView2.setItems(partsList);
            partTableView2.refresh();
        });

        Button delPartBtn = new Button("Delete");
        delPartBtn.setOnAction(action -> {
            Part newPart = partTableView2.getSelectionModel().getSelectedItem();
            partsList.remove(newPart);
            modProd.deleteAssociatedPart(newPart);
            partTableView2.setItems(partsList);
            partTableView2.refresh();
        });
        rightPane.add(delPartBtn, 0, 4);

        GridPane btnGrid = new GridPane();
        Pane s1 = new Pane();
        s1.setPrefWidth(50);
//        btnGrid.add(s1, 0, 0);

        Button modifyProductBtn = new Button("Save");
        modifyProductBtn.setOnMouseClicked(mouseEvent -> {
            _fullInventory.updateProduct(_fullInventory.getAllProducts().indexOf(_fullInventory.lookupProduct(modProd.getId())), modProd);
//                    .addProduct(new Product(
//                    getNextProductId(),
            modProd.setName(nameEntry.getText());
            modProd.setPrice(Double.parseDouble(pcEntry.getText()));
            modProd.setStock(Integer.parseInt(invEntry.getText()));
            modProd.setMin(Integer.parseInt(minEntry.getText()));
            modProd.setMax(Integer.parseInt(maxEntry.getText()));
//            ));
            _displayedInventory = _fullInventory;
            modifyProductStage.close();
        });

        btnGrid.add(modifyProductBtn, 0, 0);

        Pane s2 = new Pane();
        s2.setPrefWidth(50);
        btnGrid.add(s2, 1, 0);

        Button cancelBtn = new Button("Cancel");
        cancelBtn.setOnMouseClicked(mouseEvent -> modifyProductStage.close());
        btnGrid.add(cancelBtn, 2, 0);

        rightPane.add(btnGrid, 0, 5);

        root.add(rightPane, 1,0, 1, 4);

        return scene;
    }

    private static int getNextPartId() {
        return 1 +
                _fullInventory
                        .getAllParts()
                        .stream()
                        .mapToInt(Part::getId)
                        .max()
                        .orElse(0);
    }

    private static int getNextProductId() {
        return 1 +
                _fullInventory
                        .getAllProducts()
                        .stream()
                        .mapToInt(Product::getId)
                        .max()
                        .orElse(0);
    }

    public static void main(String[] args) {
        launch(args);

    }
}
