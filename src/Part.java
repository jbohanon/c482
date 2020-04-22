public abstract class Part {
    protected int _id;
    protected String _name;
    protected double _price;
    protected int _stock;
    protected int _min;
    protected int _max;

    public Part(
            int id,
            String name,
            double price,
            int stock,
            int min,
            int max) {
//        this(id, name, price, stock, min, max);
        _id = id;
        _name = name;
        _price = price;
        _stock = stock;
        _min = min;
        _max = max;
    }

    public abstract void setId(int id);
    public abstract int getId();

    public abstract void setName(String name);
    public abstract String getName();

    public abstract void setPrice(double price);
    public abstract double getPrice();

    public abstract void setStock(int stock);
    public abstract int getStock();

    public abstract void setMin(int min);
    public abstract int getMin();

    public abstract void setMax(int max);
    public abstract int getMax();
}
