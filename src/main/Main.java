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
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class Main extends Application {

    private static final Inventory _fullInventory = new Inventory();
    private static Inventory _displayedInventory = new Inventory();

    private static Stage mainStage;
    private static Stage addPartStage;
    private static Stage modifyPartStage;
    private static Stage addProductStage;
    private static Stage modifyProductStage;

    private static void preLoadInventory() {
        Part p1 = new Outsourced(1, "Part 1", 5, 5, 5, 300, "Joe's Hardware");
        Part p2 = new Outsourced(2, "Prt 2", 10, 25, 5, 200, "Jane's Supply");
        Part p3 = new InHouse(3, "P 3", 6.37, 12, 10, 50, 103);
        Part p4 = new InHouse(4, "Try a Search", 0.12, 60, 20, 75, 105);

        Product prod1 = new Product(1, "Product 1", 12.99, 60, 20, 75);
        prod1.addAssociatedPart(p1);
        prod1.addAssociatedPart(p4);

        Product prod2 = new Product(2, "Prod 2", 109.63, 5, 3, 10);
        prod2.addAssociatedPart(p1);
        prod2.addAssociatedPart(p2);
        prod2.addAssociatedPart(p3);
        prod2.addAssociatedPart(p4);

        _fullInventory.addPart(p1);
        _fullInventory.addPart(p2);
        _fullInventory.addPart(p3);
        _fullInventory.addPart(p4);
        _fullInventory.addProduct(prod1);
        _fullInventory.addProduct(prod2);


        _displayedInventory = _fullInventory;
    }

    @Override
    public void start(Stage primaryStage) {

        mainStage = primaryStage;
        preLoadInventory();
        mainStage.setResizable(false);
        Scene mainScreen = mainScreenDef(new BorderPane());
        mainStage.setScene(mainScreen);
        mainStage.show();
    }

    // Main Screen Def
    private
    Scene mainScreenDef(BorderPane root) {

        Scene scene = new Scene(root, 890, 350);

        HBox titlePane = mainTitleHbox();
        root.setTop(titlePane);

        GridPane partsGrid = mainPartsGrid();
        root.setLeft(partsGrid);

        GridPane productsGrid = mainProductsGrid();
        root.setRight(productsGrid);

        HBox exitBtn = mainExitBtn();
        root.setBottom(exitBtn);
        return scene;
    }

    private
    HBox mainTitleHbox() {
        HBox hbox = new HBox();

        hbox.setPadding(new Insets(30, 30, 0, 30));
        hbox.setSpacing(10);

        Text titleText = new Text("Inventory Management System");
        titleText.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));

        hbox.getChildren().add(titleText);

        return hbox;
    }

    private
    HBox mainExitBtn() {
        HBox hbox = new HBox();

        hbox.setPadding(new Insets(0, 0, 30, 550));
        hbox.setSpacing(10);

        Button exitBtn = new Button("Exit");
        exitBtn.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));
        exitBtn.setOnMouseClicked(mouseEvent -> Platform.exit());

        hbox.getChildren().add(exitBtn);

        return hbox;
    }

    private
    GridPane mainPartsGrid() {
        GridPane grid = new GridPane();

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
            ObservableList<Part> tmpList = _fullInventory.getAllParts();
            tmpList.forEach(part -> {
                if (part.getName().contains(searchText)) {
                    _displayedInventory.addPart(part);
                }
            });
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
            if(confirmDialog(null)) {
                _fullInventory.deletePart(partTableView.getSelectionModel().getSelectedItem());
                _displayedInventory = _fullInventory;
                partTableView.setItems(_displayedInventory.getAllParts());
                partTableView.refresh();
            }
        });
        grid.add(delBtn, 3, 2, 1, 1);

        return grid;
    }

    private
    GridPane mainProductsGrid() {
        GridPane grid = new GridPane();

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
            ObservableList<Product> tmpList = _fullInventory.getAllProducts();
            tmpList.forEach(product -> {
                if(product.getName().contains(searchText)) {
                    _displayedInventory.addProduct(product);
                }
            });
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
            if(confirmDialog(null)) {
                _fullInventory.deleteProduct(productTableView.getSelectionModel().getSelectedItem());
                _displayedInventory = _fullInventory;
                productTableView.setItems(_displayedInventory.getAllProducts());
                productTableView.refresh();
            }
        });
        grid.add(delBtn, 3, 2, 1, 1);

        return grid;
    }

    private Callback<TableColumn<Part, Double>, TableCell<Part, Double>> formatCurrencyCellFactoryPart() {
        return col -> new TableCell<Part, Double>() {
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
        return col -> new TableCell<Product, Double>() {
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
        addPartStage.setResizable(false);
        Scene addPartScreen = addPartScreenDef(new GridPane());
        addPartStage.setScene(addPartScreen);
        mainStage.hide();
        addPartStage.show();
    }

    private Scene addPartScreenDef(GridPane root) {
        Scene scene = new Scene(root, 350, 500);

        root.setPadding(new Insets(20, 30, 20, 30));
        root.setVgap(10);

        GridPane topGrid = new GridPane();

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

        Button saveBtn = new Button("Save");
        saveBtn.setOnMouseClicked(mouseEvent -> {
            if(nameEntry.getText().equals("")) {
                okBtnDialog("Name field cannot be empty!");
                return;
            }
            if (pcEntry.getText().equals("")) {
                okBtnDialog("Price/Cost field cannot be empty!");
                return;
            }
            try {
                if(Double.parseDouble(pcEntry.getText()) <= 0) {
                    throw new NumberFormatException();
                }
            } catch(NumberFormatException e) {
                okBtnDialog("Price/Cost format is invalid!");
                return;
            }
            if (invEntry.getText().equals("")) {
                okBtnDialog("Inv field cannot be empty!");
                return;
            }
            try {
                if(Integer.parseInt(invEntry.getText()) < 1) {
                    throw new NumberFormatException();
                }
            } catch(NumberFormatException e) {
                okBtnDialog("Inv format is invalid! Must be integer > 0!");
                return;
            }
            if (minEntry.getText().equals("")) {
                okBtnDialog("Min field cannot be empty!");
                return;
            }
            try {
                if(Integer.parseInt(minEntry.getText()) < 1) {
                    throw new NumberFormatException();
                }
            } catch(NumberFormatException e) {
                okBtnDialog("Min format is invalid! Must be integer > 0!");
                return;
            }
            if (maxEntry.getText().equals("")) {
                okBtnDialog("Max field cannot be empty!");
                return;
            }
            try {
                if(Integer.parseInt(maxEntry.getText()) < 1) {
                    throw new NumberFormatException();
                }
            } catch(NumberFormatException e) {
                okBtnDialog("Max format is invalid! Must be integer > 0!");
                return;
            }
            if(!(Integer.parseInt(maxEntry.getText()) > Integer.parseInt(invEntry.getText())
                    && Integer.parseInt(invEntry.getText()) > Integer.parseInt(minEntry.getText()))) {
                okBtnDialog("Max must be greater than Inv, and Inv must be greater than Min!");
                return;
            }

            if(rb1.isSelected()) {

                if (machIDEntry.getText().equals("")) {
                    okBtnDialog("Machine ID field cannot be empty!");
                    return;
                }
                try {
                    if(Integer.parseInt(machIDEntry.getText()) < 1) {
                        throw new NumberFormatException();
                    }
                } catch(NumberFormatException e) {
                    okBtnDialog("Machine ID format is invalid! Must be integer > 0!");
                    return;
                }
                _fullInventory.addPart(new InHouse(
                    getNextPartId(),
                    nameEntry.getText(),
                    Double.parseDouble(pcEntry.getText()),
                    Integer.parseInt(invEntry.getText()),
                    Integer.parseInt(minEntry.getText()),
                    Integer.parseInt(maxEntry.getText()),
                    Integer.parseInt(machIDEntry.getText())
            ));
            } else {
                if(compNameEntry.getText().equals("")) {
                    okBtnDialog("Company Name field cannot be empty!");
                    return;
                }
                _fullInventory.addPart(new Outsourced(
                        getNextPartId(),
                        nameEntry.getText(),
                        Double.parseDouble(pcEntry.getText()),
                        Integer.parseInt(invEntry.getText()),
                        Integer.parseInt(minEntry.getText()),
                        Integer.parseInt(maxEntry.getText()),
                        compNameEntry.getText()
                ));
            }
            _displayedInventory = _fullInventory;
            addPartStage.close();
            mainStage.show();
        });

        btnGrid.add(saveBtn, 1, 0);

        Pane s2 = new Pane();
        s2.setPrefWidth(50);
        btnGrid.add(s2, 2, 0);

        Button cancelBtn = new Button("Cancel");
        cancelBtn.setOnMouseClicked(mouseEvent -> confirmDialog(addPartStage));
        btnGrid.add(cancelBtn, 3, 0);

        AtomicBoolean altered = new AtomicBoolean(false);

        root.add(machIDGrid, 0, 6);
        root.add(btnGrid, 0, 7);

        rb1.setOnAction( action -> {
            if(altered.get()) {
                root.getChildren().remove(compNameGrid);
            }
            root.add(machIDGrid, 0, 6);
            altered.set(true);
        });
        rb2.setOnAction( action -> {
            root.getChildren().remove(machIDGrid);
            root.add(compNameGrid, 0, 6);
            altered.set(true);
        });

        return scene;
    }

    // Modify Part Screen Def
    private void modifyPartStageDef(Part modPart) {
        modifyPartStage = new Stage();
        modifyPartStage.setResizable(false);
        Scene modifyPartScreen;
        try {
            modifyPartScreen = modifyPartScreenDef(new GridPane(), modPart);
        } catch (Exception e) {
            return;
        }
        modifyPartStage.setScene(modifyPartScreen);
        mainStage.hide();
        modifyPartStage.show();
    }

    private Scene modifyPartScreenDef(GridPane root, Part modPart) {
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

        Button modBtn = new Button("Save");
        modBtn.setOnMouseClicked(mouseEvent -> {
            if(nameEntry.getText().equals("")) {
                okBtnDialog("Name field cannot be empty!");
                return;
            }
            if (pcEntry.getText().equals("")) {
                okBtnDialog("Price/Cost field cannot be empty!");
                return;
            }
            try {
                if(Double.parseDouble(pcEntry.getText()) <= 0) {
                    throw new NumberFormatException();
                }
            } catch(NumberFormatException e) {
                okBtnDialog("Price/Cost format is invalid!");
                return;
            }
            if (invEntry.getText().equals("")) {
                okBtnDialog("Inv field cannot be empty!");
                return;
            }
            try {
                if(Integer.parseInt(invEntry.getText()) < 1) {
                    throw new NumberFormatException();
                }
            } catch(NumberFormatException e) {
                okBtnDialog("Inv format is invalid! Must be integer > 0!");
                return;
            }
            if (minEntry.getText().equals("")) {
                okBtnDialog("Min field cannot be empty!");
                return;
            }
            try {
                if(Integer.parseInt(minEntry.getText()) < 1) {
                    throw new NumberFormatException();
                }
            } catch(NumberFormatException e) {
                okBtnDialog("Min format is invalid! Must be integer > 0!");
                return;
            }
            if (maxEntry.getText().equals("")) {
                okBtnDialog("Max field cannot be empty!");
                return;
            }
            try {
                if(Integer.parseInt(maxEntry.getText()) < 1) {
                    throw new NumberFormatException();
                }
            } catch(NumberFormatException e) {
                okBtnDialog("Max format is invalid! Must be integer > 0!");
                return;
            }
            if(!(Integer.parseInt(maxEntry.getText()) > Integer.parseInt(invEntry.getText())
                    && Integer.parseInt(invEntry.getText()) > Integer.parseInt(minEntry.getText()))) {
                okBtnDialog("Max must be greater than Inv, and Inv must be greater than Min!");
                return;
            }
            if(rb1.isSelected()) {

                if (machIDEntry.get().getText().equals("")) {
                    okBtnDialog("Machine ID field cannot be empty!");
                    return;
                }
                try {
                    if(Integer.parseInt(machIDEntry.get().getText()) < 1) {
                        throw new NumberFormatException();
                    }
                } catch(NumberFormatException e) {
                    okBtnDialog("Machine ID format is invalid! Must be integer > 0!");
                    return;
                }
                _fullInventory.updatePart(_fullInventory.getAllParts().indexOf(modPart), new InHouse(
                        modPart.getId(),
                        nameEntry.getText(),
                        Double.parseDouble(pcEntry.getText()),
                        Integer.parseInt(invEntry.getText()),
                        Integer.parseInt(minEntry.getText()),
                        Integer.parseInt(maxEntry.getText()),
                        Integer.parseInt(machIDEntry.get().getText())
                ));
            } else {
                if(compNameEntry.get().getText().equals("")) {
                    okBtnDialog("Company Name field cannot be empty!");
                    return;
                }
                _fullInventory.updatePart(_fullInventory.getAllParts().indexOf(modPart), new Outsourced(
                        modPart.getId(),
                        nameEntry.getText(),
                        Double.parseDouble(pcEntry.getText()),
                        Integer.parseInt(invEntry.getText()),
                        Integer.parseInt(minEntry.getText()),
                        Integer.parseInt(maxEntry.getText()),
                        compNameEntry.get().getText()
                ));
            }

            _displayedInventory = _fullInventory;
            modifyPartStage.close();
            mainStage.show();
        });

        btnGrid.add(modBtn, 1, 0);

        Pane s2 = new Pane();
        s2.setPrefWidth(50);
        btnGrid.add(s2, 2, 0);

        Button cancelBtn = new Button("Cancel");
        cancelBtn.setOnMouseClicked(mouseEvent -> confirmDialog(modifyPartStage));

        btnGrid.add(cancelBtn, 3, 0);

        if(dispIn.get() != null) {
            root.add(machIDGrid, 0, 6);
        } else {
            root.add(compNameGrid, 0, 6);
        }
        root.add(btnGrid, 0, 7);

        rb1.setOnAction( action -> {
            root.getChildren().remove(compNameGrid);
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
        });
        rb2.setOnAction( action -> {
            root.getChildren().remove(machIDGrid);
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
        });

        return scene;
    }

    // Add Product Screen Def
    private void addProductStageDef() {
        addProductStage = new Stage();
        addProductStage.setResizable(false);
        Scene addProductScreen = addProductScreenDef(new GridPane());
        addProductStage.setScene(addProductScreen);
        mainStage.hide();
        addProductStage.show();
    }

    private Scene addProductScreenDef(GridPane root) {
        Scene scene = new Scene(root, 700, 500);

        GridPane leftPane = new GridPane();

        leftPane.setPadding(new Insets(20, 30, 20, 30));
        leftPane.setVgap(10);

        GridPane topGrid = new GridPane();

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
            String searchText = searchArea.getText();
            if(searchText.equals("")){
                partTableView1.setItems(_fullInventory.getAllParts());
                partTableView1.refresh();
                return;
            }
            ObservableList<Part> tmp = FXCollections.observableArrayList();
            _fullInventory.getAllParts().forEach(prod -> {
                if(prod.getName().contains(searchText)) {
                    tmp.add(prod);
                }
            });
            partTableView1.setItems(tmp);
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
            Part item = partTableView1.getSelectionModel().getSelectedItem();
            if(!partsList.contains(item)) {
                partsList.add(item);
                partTableView2.setItems(partsList);
                partTableView2.refresh();
            } else {
                okBtnDialog("Cannot associate duplicate part.");
            }
        });

        Button delPartBtn = new Button("Delete");
        delPartBtn.setOnAction(action -> {
            if(confirmDialog(null)) {
                partsList.remove(partTableView2.getSelectionModel().getSelectedItem());
                partTableView2.setItems(partsList);
                partTableView2.refresh();
            }
        });
        rightPane.add(delPartBtn, 0, 4);

        GridPane btnGrid = new GridPane();

        Button addProductBtn = new Button("Save");
        addProductBtn.setOnMouseClicked(mouseEvent -> {
            if(nameEntry.getText().equals("")) {
                okBtnDialog("Name field cannot be empty!");
                return;
            }
            if (pcEntry.getText().equals("")) {
                okBtnDialog("Price/Cost field cannot be empty!");
                return;
            }
            try {
                if(Double.parseDouble(pcEntry.getText()) <= 0) {
                    throw new NumberFormatException();
                }
            } catch(NumberFormatException e) {
                okBtnDialog("Price/Cost format is invalid!");
                return;
            }
            if (invEntry.getText().equals("")) {
                okBtnDialog("Inv field cannot be empty!");
                return;
            }
            try {
                if(Integer.parseInt(invEntry.getText()) < 1) {
                    throw new NumberFormatException();
                }
            } catch(NumberFormatException e) {
                okBtnDialog("Inv format is invalid! Must be integer > 0!");
                return;
            }
            if (minEntry.getText().equals("")) {
                okBtnDialog("Min field cannot be empty!");
                return;
            }
            try {
                if(Integer.parseInt(minEntry.getText()) < 1) {
                    throw new NumberFormatException();
                }
            } catch(NumberFormatException e) {
                okBtnDialog("Min format is invalid! Must be integer > 0!");
                return;
            }
            if (maxEntry.getText().equals("")) {
                okBtnDialog("Max field cannot be empty!");
                return;
            }
            try {
                if(Integer.parseInt(maxEntry.getText()) < 1) {
                    throw new NumberFormatException();
                }
            } catch(NumberFormatException e) {
                okBtnDialog("Max format is invalid! Must be integer > 0!");
                return;
            }
            if(!(Integer.parseInt(maxEntry.getText()) > Integer.parseInt(invEntry.getText())
                    && Integer.parseInt(invEntry.getText()) > Integer.parseInt(minEntry.getText()))) {
                okBtnDialog("Max must be greater than Inv, and Inv must be greater than Min!");
                return;
            }
            if(partsList.isEmpty()) {
                okBtnDialog("Please associate at least one part!");
                return;
            }
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
            AtomicReference<Double> newTotal = new AtomicReference<Double>(0.0);
            newProd.getAllAssociatedParts().forEach(part -> newTotal.updateAndGet(v -> v + part.getPrice()));
            if(newProd.getPrice() < newTotal.get()) {
                okBtnDialog("Cost of associated parts mustn't exceed Product price!");
                return;
            }
            _fullInventory.addProduct(newProd);
            _displayedInventory = _fullInventory;
            mainStage.show();
            addProductStage.close();
        });

        btnGrid.add(addProductBtn, 0, 0);

        Pane s2 = new Pane();
        s2.setPrefWidth(50);
        btnGrid.add(s2, 1, 0);

        Button cancelBtn = new Button("Cancel");
        cancelBtn.setOnMouseClicked(mouseEvent -> confirmDialog(addProductStage));
        btnGrid.add(cancelBtn, 2, 0);

        rightPane.add(btnGrid, 0, 5);

        root.add(rightPane, 1,0, 1, 4);

        return scene;
    }


    // Modify Product Screen Def
    private void modifyProductStageDef(Product modProd) {
        modifyProductStage = new Stage();
        modifyProductStage.setResizable(false);
        Scene modifyProductScreen = modifyProductScreenDef(new GridPane(), modProd);
        modifyProductStage.setScene(modifyProductScreen);
        mainStage.hide();
        modifyProductStage.show();
    }

    private Scene modifyProductScreenDef(GridPane root, Product modProd) {
        Scene scene = new Scene(root, 700, 500);

        GridPane leftPane = new GridPane();

        leftPane.setPadding(new Insets(20, 30, 20, 30));
        leftPane.setVgap(10);

        GridPane topGrid = new GridPane();

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
            String searchText = searchArea.getText();
            if(searchText.equals("")){
                partTableView1.setItems(_fullInventory.getAllParts());
                partTableView1.refresh();
                return;
            }
            ObservableList<Part> tmp = FXCollections.observableArrayList();
            _fullInventory.getAllParts().forEach(prod -> {
                if(prod.getName().contains(searchText)) {
                    tmp.add(prod);
                }
            });
            partTableView1.setItems(tmp);
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
            Part item = partTableView1.getSelectionModel().getSelectedItem();
            if(!partsList.contains(item)) {
                partsList.add(item);
                partTableView2.setItems(partsList);
                partTableView2.refresh();
            } else {
                okBtnDialog("Cannot associate duplicate part.");
            }
        });

        Button delPartBtn = new Button("Delete");
        delPartBtn.setOnAction(action -> {
            if(confirmDialog(null)) {
                Part newPart = partTableView2.getSelectionModel().getSelectedItem();
                partsList.remove(newPart);
                modProd.deleteAssociatedPart(newPart);
                partTableView2.setItems(partsList);
                partTableView2.refresh();
            }
        });
        rightPane.add(delPartBtn, 0, 4);

        GridPane btnGrid = new GridPane();
        Pane s1 = new Pane();
        s1.setPrefWidth(50);

        Button modifyProductBtn = new Button("Save");
        modifyProductBtn.setOnMouseClicked(mouseEvent -> {
            if(nameEntry.getText().equals("")) {
                okBtnDialog("Name field cannot be empty!");
                return;
            }
            if (pcEntry.getText().equals("")) {
                okBtnDialog("Price/Cost field cannot be empty!");
                return;
            }
            try {
                if(Double.parseDouble(pcEntry.getText()) <= 0) {
                    throw new NumberFormatException();
                }
            } catch(NumberFormatException e) {
                okBtnDialog("Price/Cost format is invalid!");
                return;
            }
            if (invEntry.getText().equals("")) {
                okBtnDialog("Inv field cannot be empty!");
                return;
            }
            try {
                if(Integer.parseInt(invEntry.getText()) < 1) {
                    throw new NumberFormatException();
                }
            } catch(NumberFormatException e) {
                okBtnDialog("Inv format is invalid! Must be integer > 0!");
                return;
            }
            if (minEntry.getText().equals("")) {
                okBtnDialog("Min field cannot be empty!");
                return;
            }
            try {
                if(Integer.parseInt(minEntry.getText()) < 1) {
                    throw new NumberFormatException();
                }
            } catch(NumberFormatException e) {
                okBtnDialog("Min format is invalid! Must be integer > 0!");
                return;
            }
            if (maxEntry.getText().equals("")) {
                okBtnDialog("Max field cannot be empty!");
                return;
            }
            try {
                if(Integer.parseInt(maxEntry.getText()) < 1) {
                    throw new NumberFormatException();
                }
            } catch(NumberFormatException e) {
                okBtnDialog("Max format is invalid! Must be integer > 0!");
                return;
            }
            if(!(Integer.parseInt(maxEntry.getText()) > Integer.parseInt(invEntry.getText())
                    && Integer.parseInt(invEntry.getText()) > Integer.parseInt(minEntry.getText()))) {
                okBtnDialog("Max must be greater than Inv, and Inv must be greater than Min!");
                return;
            }
            if(partsList.isEmpty()) {
                okBtnDialog("Please associate at least one part!");
                return;
            }

            modProd.setName(nameEntry.getText());
            modProd.setPrice(Double.parseDouble(pcEntry.getText()));
            modProd.setStock(Integer.parseInt(invEntry.getText()));
            modProd.setMin(Integer.parseInt(minEntry.getText()));
            modProd.setMax(Integer.parseInt(maxEntry.getText()));


            AtomicReference<Double> newTotal = new AtomicReference<Double>(0.0);
            modProd.getAllAssociatedParts().forEach(part -> newTotal.updateAndGet(v -> v + part.getPrice()));
            if(modProd.getPrice() < newTotal.get()) {
                okBtnDialog("Cost of associated parts mustn't exceed Product price!");
                return;
            }

            _fullInventory.updateProduct(_fullInventory.getAllProducts().indexOf(_fullInventory.lookupProduct(modProd.getId())), modProd);
            _displayedInventory = _fullInventory;
            modifyProductStage.close();
            mainStage.show();
        });

        btnGrid.add(modifyProductBtn, 0, 0);

        Pane s2 = new Pane();
        s2.setPrefWidth(50);
        btnGrid.add(s2, 1, 0);

        Button cancelBtn = new Button("Cancel");
        cancelBtn.setOnMouseClicked(mouseEvent -> confirmDialog(modifyProductStage));
        btnGrid.add(cancelBtn, 2, 0);

        rightPane.add(btnGrid, 0, 5);

        root.add(rightPane, 1,0, 1, 4);

        return scene;
    }

    private Boolean confirmDialog(Stage thisStage) {
        AtomicReference<Boolean> retval = new AtomicReference<>();
        retval.set(false);
        Dialog<ButtonType> confirmDialog = new Dialog<>();
        confirmDialog.getDialogPane().setContentText("Are you sure?");
        confirmDialog.getDialogPane().getButtonTypes().add(ButtonType.YES);
        confirmDialog.getDialogPane().getButtonTypes().add(ButtonType.NO);
        confirmDialog.showAndWait().filter(resp -> resp == ButtonType.YES).ifPresent(a -> {
            retval.set(true);
            if(thisStage != null) {
                thisStage.close();
                mainStage.show();
            }
        });
        return retval.get();
    }

    private void okBtnDialog(String msg) {
        Dialog<ButtonType> cannotAddDuplicatePartDialog = new Dialog<>();
        cannotAddDuplicatePartDialog.getDialogPane().setContentText(msg);
        cannotAddDuplicatePartDialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        cannotAddDuplicatePartDialog.showAndWait();
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
