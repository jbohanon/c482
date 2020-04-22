import javafx.collections.ObservableList;

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

        _allParts.forEach((part) -> {
                if(part.getId() == partId) {
                    retval.set(part);
                }
        });
        return retval.get();
    }

}
