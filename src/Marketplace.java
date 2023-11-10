import java.util.ArrayList;
import java.util.List;

public class Marketplace implements Market {


    ArrayList<Resource> marketResources = new ArrayList<Resource>();
    ArrayList<Item> marketItems = new ArrayList<Item>();


    public boolean addItem(int itemId, int quantity) {
        if (quantity > 0) {
            /*Check for item already existing*/
            for (Item item : marketItems) {
                if (item.getId() == itemId) {
                    /*If exists, update the quantity and return true*/
                    item.setQuantity(item.getQuantity() + quantity);
                    return true;
                }
            }
            /*If not existing, create new item and return true*/
            Item new_item = new Item(getItemDetails(itemId));
            marketItems.add(new_item);
            return true;
        }
        /*Can't add negative number*/
        return false;
    }

    public boolean removeItem(int itemId, int quantity) {
        if (quantity > 0) {
            /*Check for item already existing*/
            for (Item item : marketItems) {
                if (item.getId() == itemId) {
                    /*If exists, update the quantity and return true*/
                    item.setQuantity(item.getQuantity() - quantity);
                    return true;
                } else {
                    /*Item doesn't exist*/
                    return false;
                }
            }
        }
        /*Can't deduct negative number*/
        return false;
    }

    public boolean addFinds






}

