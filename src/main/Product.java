package main;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.lang.reflect.Constructor;

public class Product {
    private ObservableList<Part> _associatedParts;
    private int _id;
    private String _name;
    private double _price;
    private int _stock;
    private int _min;
    private int _max;

    public Product(
            int id,
            String name,
            double price,
            int stock,
            int min,
            int max
    ) {
        _id = id;
        _name = name;
        _price = price;
        _stock = stock;
        _min = min;
        _max = max;
        _associatedParts = FXCollections.observableArrayList();
    }

    public void setId(int id) {
        _id = id;
    }
    public int getId() {
        return _id;
    }

    public void setName(String name) {
        _name = name;
    }
    public String getName() {
        return _name;
    }

    public void setPrice(double price) {
        _price = price;
    }
    public double getPrice() {
        return _price;
    }

    public void setStock(int stock) {
        _stock = stock;
    }
    public int getStock() {
        return _stock;
    }

    public void setMin(int min) {
        _min = min;
    }
    public int getMin() {
        return _min;
    }

    public void setMax(int max) {
        _max = max;
    }
    public int getMax() {
        return _max;
    }

    public void addAssociatedPart(Part part) {
        _associatedParts.add(part);
    }

    public boolean deleteAssociatedPart(Part selectedAsPart) {
        return _associatedParts.remove(selectedAsPart);
    }

    public ObservableList<Part> getAllAssociatedParts() {
        return _associatedParts;
    }
}
