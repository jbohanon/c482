import javafx.collections.ObservableList;

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
    }


}
