import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class Inventory {
    private ObservableList<Part> _allParts;
    private ObservableList<Product> _allProducts;

    public void addPart(Part newPart) {
        _allParts.add(newPart);
    }

    public void addProduct(Product newProduct) {
        _allProducts.add(newProduct);
    }

    public Part lookupPart(int partId) {
        AtomicReference<Part> retval = new AtomicReference<>();

        _allParts.forEach((p) -> {
            if (p.getId() == partId) {
                retval.set(p);
            }
        });
        return retval.get();
    }

    public Product lookupProduct(int productId) {
//        AtomicReference<Product> retval = new AtomicReference<>();
        final Product[] retval = new Product[1];

        _allProducts.forEach((p) -> {
            if (p.getId() == productId) {
//                retval.set(p);
                retval[0] = p;
            }
        });
        return retval[0];
    }

    public ObservableList<Part> lookupPart(String partName) {
        ObservableList<Part> retval = FXCollections.emptyObservableList();
        _allParts.forEach((p) -> {
            if (p.getName().equals(partName)) {
                retval.add(p);
            }
        });
        return retval;
    }

    public ObservableList<Product> lookupProduct(String productName) {
        ObservableList<Product> retval = FXCollections.emptyObservableList();
        _allProducts.forEach((p) -> {
            if (p.getName().equals(productName)) {
                retval.add(p);
            }
        });
        return retval;
    }

    public void updatePart(int index, Part selectedPart) {
        _allParts.set(index, selectedPart);
    }

    public void updateProduct(int index, Product newProduct) {
        _allProducts.set(index, newProduct);
    }

    public boolean deletePart(Part selectedPart) {
        return _allParts.remove(selectedPart);
    }

    public boolean deleteProduct(Product selectedProduct) {
        return _allProducts.remove(selectedProduct);
    }

    public ObservableList<Part> getAllParts() {
        return _allParts;
    }

    public ObservableList<Product> getAllProducts() {
        return _allProducts;
    }
}
