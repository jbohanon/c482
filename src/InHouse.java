public class InHouse extends Part {

    private int _machineId;

    public InHouse(
            int id,
            String name,
            double price,
            int stock,
            int min,
            int max,
            int machineId) {
        super(id, name, price, stock, min, max);
        _id = id;
        _name = name;
        _price = price;
        _stock = stock;
        _min = min;
        _max = max;
        _machineId = machineId;
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

    public void setMachineId(int machineId) {
        _machineId = machineId;
    }
    public int getMachineId() {
        return _machineId;
    }
}
