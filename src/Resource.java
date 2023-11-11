public class Resource {
    private int cost;
    private String name;
    private int id;
    private int quantity;

    public Resource(int id, int cost, int quantity, String name){
        this.id = id;
        this.cost = cost;
        this.quantity = quantity;
        this.name = name;
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

    public String getName() {
        return name;
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

    public void setName(String name) {
        this.name = name;
    }
}
