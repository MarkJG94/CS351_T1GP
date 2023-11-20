import java.util.ArrayList;

public class Marketplace {

    ArrayList<Resource> marketResources;
    public Marketplace(ArrayList<Resource> resourceList){
        this.marketResources = resourceList;
    }

    public boolean addResourceToMarket(int resourceID, int quantity) {
        if (quantity > 0) {
            /*Check for item already existing*/
            int resourceIndex = getResourceIndex(resourceID);
            if (resourceIndex != -1){
                marketResources.get(resourceIndex).setQuantity(marketResources.get(resourceIndex).getQuantity() + quantity);
                return true;
            }
        }
        /*Can't add negative number*/
        return false;
    }

    public boolean removeResourceFromMarket(int resourceID, int quantity) {
        if (quantity > 0) {
            /*Check for item already existing*/
            int resourceIndex = getResourceIndex(resourceID);
            if (resourceIndex != -1){
                marketResources.get(resourceIndex).setQuantity(marketResources.get(resourceIndex).getQuantity() - quantity);
                return true;
            }
        }
        return false;
    }
    
    public int calculateTotalCost(int quantity, int resourceID) {
        if (quantity > 0){
            int resourceIndex = getResourceIndex(resourceID);
            if (resourceIndex == -1){
                return -1;
            }
            return marketResources.get(resourceIndex).getCost() * quantity;
        }
        return -1;
    }

    public int calculateTotalValue(int quantity, int resourceID) {
        if (quantity > 0){
            int resourceIndex = getResourceIndex(resourceID);
            if (resourceIndex == -1){
                return -1;
            }
            return marketResources.get(resourceIndex).getValue() * quantity;
        }
        return -1;
    }

    public int getResourceIndex(int resourceID) {
        for (Resource resource : marketResources) {
            if (resource.getId() == resourceID) {
                return marketResources.indexOf(resource);
            }
        }
        return -1;
    }
    
    public int getResourceQuantity(int resourceID) {
        int resourceIndex = getResourceIndex(resourceID);
        if (resourceIndex != -1){
            return marketResources.get(resourceIndex).getQuantity();
        }
        return -1;
    }

    public Resource getResourceDetails(int resourceID) {
        if(marketResources.size() > resourceID - 1){
            return marketResources.get(resourceID - 1);
        } else {
            return null;
        }
    }

    public ArrayList<Resource> getMarketResources(){
        return marketResources;
    }
}
