package main;

public class Outsourced extends Part {

    private String _companyName;

    public Outsourced(
            int id,
            String name,
            double price,
            int stock,
            int min,
            int max,
            String companyName) {
        super(id, name, price, stock, min, max);
        _id = id;
        _name = name;
        _price = price;
        _stock = stock;
        _min = min;
        _max = max;
        _companyName = companyName;
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

    public void setCompanyName(String companyName) {
        _companyName = companyName;
    }
    public String getCompanyName() {
        return _companyName;
    }
}
