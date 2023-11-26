/*
    Resource class containing the cost, value, name, id and quantity of a single resource
 */
public class Resource {
    private final int cost; // Cost is the whole number that it costs to purchase a resource
    private final int value; // Value is the whole number value to sell a resource
    private final String name;
    private final int id;
    private int quantity;

    // Constructor to create a resource when provided an ID, cost, quantity, name and value
    public Resource(int id, int cost, int quantity, String name, int value){
        this.id = id;
        this.cost = cost;
        this.quantity = quantity;
        this.name = name;
        this.value = value;
    }

    // Returns the resource name
    public String getName() {
        return name;
    }

    // Returns the resource ID
    public int getId() {
        return id;
    }

    // Returns the resource cost
    public int getCost() {
        return cost;
    }

    // Returns the resource value
    public int getValue() {
        return value;
    }

    // Returns the resource quantity
    public int getQuantity() {
        return quantity;
    }

    // Sets the resource quantity to a new value
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

}
