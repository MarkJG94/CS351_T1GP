/*
    Resource class containing the cost, value, name, id and quantity of a single resource
 */
public class Resource {
    private int cost; // Cost is the whole number that it costs to purchase a resource
    private int value; // Value is the whole number value to sell a resource
    private String name;
    private int id;
    private int quantity;

    // Constructor to create a resource when provided an ID, cost, quantity, name and value
    public Resource(int id, int cost, int quantity, String name, int value){
        this.id = id;
        this.cost = cost;
        this.quantity = quantity;
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }
    public int getId() {
        return id;
    }

    public int getCost() {
        return cost;
    }
    public int getValue() {
        return value;
    }

    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

}
