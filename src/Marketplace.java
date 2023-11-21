import java.util.ArrayList;

public class Marketplace
{
    
    ArrayList<Resource> marketResources;
    
    public Marketplace( ArrayList<Resource> resourceList )
    {
        this.marketResources = resourceList;
    }
    
    public boolean addResourceToMarket( int resourceID, int quantity )
    {
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
    
    public boolean removeResourceFromMarket( int resourceID, int quantity )
    {
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
                    if ( ( r.getQuantity() - quantity ) < 0 )
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
    
    public int calculateTotalCost( int quantity, int resourceID )
    {
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
    
    public int calculateTotalValue( int quantity, int resourceID )
    {
        if ( quantity > 0 )
        {
            int resourceIndex = getResourceIndex( resourceID );
            if ( resourceIndex == -1 )
            {
                return -1;
            }
            return getResource( resourceIndex ).getValue() * quantity;
        }
        return -1;
    }
    
    public int getResourceIndex( int resourceID )
    {
        if ( ( resourceID - 1 ) >= 0 )
        {
            return resourceID - 1;
        }
        return -1;
    }
    
    public int getResourceQuantity( int resourceID )
    {
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
    
    public Resource getResourceDetails( int resourceID )
    {
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
    
    public ArrayList<Resource> getMarketResources()
    {
        
        return marketResources;
    }
    
    public Resource getResource( int resourceIndex )
    {
        Resource r = marketResources.get( resourceIndex );
        if ( r != null )
        {
            return r;
        }
        return null;
    }
}

