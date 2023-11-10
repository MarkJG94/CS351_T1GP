public class Item {

    private int cost;
    private String name;
    private int id;
    private int quantity;

    public Item(int id, int cost, int quantity, String name){
        this.id = id;
        this.cost = cost;
        this.quantity = quantity;
        this.name = name;
    }

    public Item(Item itemDetails) {
    }

    public int getId() {
        return id;
    }

    public int getCost() {
        return cost;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}

