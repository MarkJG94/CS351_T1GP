import java.util.ArrayList;

/*
    Class to contain the list of resources owned by the "Marketplace".
    There should only be one instance and contained within the server class
 */
public class Marketplace
{

    ArrayList<Resource> marketResources;

    public Marketplace( ArrayList<Resource> resourceList )
    {
        this.marketResources = resourceList;
    }

    // Method to add resources to the marketplace resource list
    public boolean addResourceToMarket( int resourceID, int quantity ) {
        Object lock0;
        if ( quantity > 0 )
        {
            /*Check for item already existing*/
            int resourceIndex = getResourceIndex( resourceID );
            if ( resourceIndex != -1 )
            {
                Resource r = getResource( resourceIndex );
                lock0 = r;
                synchronized ( lock0 )
                {
                    r.setQuantity( r.getQuantity() + quantity );
                }
                return true;
            }
        }
        /*Can't add negative number*/
        return false;
    }

    // Method to remove resources from the marketplace resource list
    public boolean removeResourceFromMarket( int resourceID, int quantity ) {
        Object lock0;
        if ( quantity > 0 )
        {
            /*Check for item already existing*/
            int resourceIndex = getResourceIndex( resourceID );
            if ( resourceIndex != -1 )
            {
                Resource r = getResource( resourceIndex );
                lock0 = r;
                synchronized ( lock0 )
                {
                    if ( ( r.getQuantity() < quantity ) )
                    {
                        return false;
                    }
                    r.setQuantity( r.getQuantity() - quantity );
                }
                return true;
            }
        }
        return false;
    }

    // Method to calculate the total cost when provided an ResourceID and a quantity
    public int calculateTotalCost( int quantity, int resourceID ) {
        if ( quantity > 0 )
        {
            int resourceIndex = getResourceIndex( resourceID );
            if ( resourceIndex == -1 )
            {
                return -1;
            }
            return getResource( resourceIndex ).getCost() * quantity;
        }
        return -1;
    }

    // Method to calculate the total value when provided an ResourceID and a quantity
    public int calculateTotalValue( int quantity, int resourceID ) {
        if ( quantity > 0 )
        {
            int resourceIndex = getResourceIndex( resourceID );
            if ( resourceIndex == -1)
            {
                return -1;
            }
            return getResource( resourceIndex ).getValue() * quantity;
        }
        return -1;
    }

    // Method to return the index integer of a given resourceID, or -1 if the resource ID does not exist
    public int getResourceIndex( int resourceID ) {
        if ( ( resourceID - 1 ) >= 0 && resourceID -1 <= marketResources.size() )
        {
            return resourceID - 1;
        }
        return -1;
    }

    // Method to return the quantity of a provided resource when given the resources ID
    public int getResourceQuantity( int resourceID ) {
        Object lock0;
        int returnValue;
        int resourceIndex = getResourceIndex( resourceID );
        if ( resourceIndex != -1 )
        {
            lock0 = getResource( resourceIndex );
            synchronized ( lock0 )
            {
                returnValue = marketResources.get( resourceIndex ).getQuantity();
            }
            return returnValue;
        }
        return -1;
    }

    // Method to get return a resource when provided a ResourceID, or null if the resource does not exist
    public Resource getResourceDetails( int resourceID ) {
        int resourceIndex = getResourceIndex( resourceID );
        if ( marketResources.size() > resourceIndex )
        {
            return getResource( resourceIndex );
        }
        else
        {
            return null;
        }
    }

    // Returns the arraylist of resources
    public ArrayList<Resource> getMarketResources() {

        return marketResources;
    }

    // Method to return a resource when given the resource index within the resource arraylist
    public Resource getResource( int resourceIndex ) {
        Resource r = marketResources.get( resourceIndex );
        if ( r != null )
        {
            return r;
        }
        return null;
    }

}
