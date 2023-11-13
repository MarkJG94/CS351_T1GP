import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class MarketplaceTest {

    Marketplace marketplace = new Marketplace();
    ArrayList<Resource> resourceList = new ArrayList<Resource>();
    Resource wood = new Resource(1, 1, 1000, "wood");
    Resource iron = new Resource(2, 2, 1000, "iron");
    Resource silver = new Resource(3, 5, 1000, "silver");
    Resource gold = new Resource(4, 10, 1000, "gold");

    ArrayList<User> userList = new ArrayList<User>();
    User user1 = new User("User One", resourceList, 10000);
    User user2 = new User("User Two", resourceList, 100);
    User user3 = new User("User Three", resourceList, 10000);
    User user4 = new User("User Four", resourceList, 1);

    @BeforeEach
    void setup(){
        resourceList.add(wood);
        resourceList.add(iron);
        resourceList.add(silver);
        resourceList.add(gold);
        marketplace.marketResources = resourceList;

        userList.add(user1);
        userList.add(user2);
        userList.add(user3);
        userList.add(user4);
        marketplace.userList = userList;

    }

    @Test
    void givenValidThenUserExists() {

        assertEquals(true, marketplace.userExists("User One"));
    }

    @Test
    void givenInvalidThenUserExistsReturnFalse() {

        assertEquals(false, marketplace.userExists("User Five"));
    }

    @Test
    void givenValidThenGetFunds() {

        assertEquals(10000, marketplace.getFunds("User One"));
    }

    @Test
    void givenInvalidThenGetFundsReturnsError() {

        assertEquals(-1, marketplace.getFunds("User Five"));
    }

    @Test
    void givenValidThenGetResourceQuantity() {

        assertEquals(wood, marketplace.getResourceDetails(1));
        assertEquals(1, marketplace.getResourceDetails(1).getId());
        assertEquals(1, marketplace.getResourceDetails(1).getCost());
        assertEquals(1000, marketplace.getResourceDetails(1).getQuantity());
        assertEquals("wood", marketplace.getResourceDetails(1).getName());
    }

    @Test
    void givenInvalidThenGetResourceQuantityReturnsNull() {

        assertEquals(null, marketplace.getResourceDetails(5));
    }

    @Test
    void givenValidThenGetResourceDetails() {

        assertEquals(1000, marketplace.getResourceQuantity(1));
    }

    @Test
    void givenInvalidThenGetResourceDetailsReturnsError() {

        assertEquals(-1, marketplace.getResourceQuantity(5));
    }

    @Test
    void givenValidThenAddResourceToMarket() {
        assertEquals(true, marketplace.addResourceToMarket(1,100));
        assertEquals(1100, marketplace.marketResources.get(0).getQuantity());
    }

    @Test
    void givenInvalidIdThenAddResourceToMarketReturnFalse() {

        assertEquals(false, marketplace.addResourceToMarket(5,100));
    }

    @Test
    void givenInvalidAmountThenAddResourceToMarketReturnFalse() {

        assertEquals(false, marketplace.addResourceToMarket(1,-100));
    }

    @Test
    void givenValidThenRemoveResourceFromMarket() {

        assertEquals(true, marketplace.removeResourceFromMarket(1,100));
        assertEquals(900, marketplace.marketResources.get(0).getQuantity());
    }

    @Test
    void givenInvalidIdThenRemoveResourceFromMarketReturnFalse() {

        assertEquals(false, marketplace.removeResourceFromMarket(5,100));
    }

    @Test
    void givenInvalidAmountThenRemoveResourceFromMarketReturnFalse() {

        assertEquals(false, marketplace.removeResourceFromMarket(1,-100));
    }






    @Test
    void givenValidThenAddResourceToUser() {
        assertEquals(true, marketplace.addResourceToUser(1,100, "User One"));
        assertEquals(1100, marketplace.marketResources.get(0).getQuantity());
    }

    @Test
    void givenInvalidIdThenAddResourceToUserReturnFalse() {

        assertEquals(false, marketplace.addResourceToUser(5,100, "User One"));
    }

    @Test
    void givenInvalidAmountThenAddResourceToUserReturnFalse() {

        assertEquals(false, marketplace.addResourceToUser(1,-100, "User One"));
    }

    @Test
    void givenValidThenRemoveResourceFromUser() {

        assertEquals(true, marketplace.removeResourceFromUser(1,100, "User One"));
        assertEquals(900, marketplace.marketResources.get(0).getQuantity());
    }

    @Test
    void givenInvalidIdThenRemoveResourceFromUserReturnFalse() {

        assertEquals(false, marketplace.removeResourceFromUser(5,100, "User One"));
    }

    @Test
    void givenInvalidAmountThenRemoveResourceFroUserReturnFalse() {

        assertEquals(false, marketplace.removeResourceFromUser(1,-100, "User One"));
    }








    @Test
    void givenValidThenAddFunds() {

        assertEquals(10100, marketplace.addFunds("User One", 100));
    }

    @Test
    void givenInvalidIdThenAddFundsReturnError() {

        assertEquals(-1, marketplace.addFunds("User Five",100));
    }

    @Test
    void givenInvalidAmountThenAddFundsReturnError() {

        assertEquals(-1, marketplace.addFunds("User One",-100));
    }

    @Test
    void givenValidThenRemoveFunds() {

        assertEquals(9900, marketplace.deductFunds("User One", 100));
    }

    @Test
    void givenInvalidIdThenRemoveFundsReturnError() {

        assertEquals(-1, marketplace.deductFunds("User Five",100));
    }

    @Test
    void givenInvalidAmountThenRemoveFundsReturnError() {

        assertEquals(-1, marketplace.deductFunds("User One",-100));
    }

    @Test
    void givenNotEnoughInAccountAmountThenRemoveFundsReturnError() {

        assertEquals(-1, marketplace.deductFunds("User Four",-100));
    }

    @Test
    void givenValidThenTransferFunds() {

        assertEquals(true, marketplace.transferFunds("User One", "User Two", 500));

        assertEquals(9500, marketplace.getFunds("User One"));
        assertEquals(600, marketplace.getFunds("User Two"));

    }

    @Test
    void givenSourceInvalidIdThenTransferFundsReturnFalse() {

        assertEquals(false, marketplace.transferFunds("User Five", "User Two", 500));

        assertEquals(100, marketplace.getFunds("User Two"));
    }

    @Test
    void givenDestinationInvalidIdThenTransferFundsReturnFalse() {

        assertEquals(false, marketplace.transferFunds("User One", "User Five", 500));

        assertEquals(10000, marketplace.getFunds("User One"));
    }

    @Test
    void givenInvalidAmountThenTransferFundsReturnFalse() {

        assertEquals(false, marketplace.transferFunds("User One", "User Five", -500));

        assertEquals(10000, marketplace.getFunds("User One"));
        assertEquals(100, marketplace.getFunds("User Two"));
    }

    @Test
    void givenNotEnoughInAccountAmountThenTransferFundsReturnFalse() {

        assertEquals(false, marketplace.transferFunds("User Five", "User One", 500));

        assertEquals(10000, marketplace.getFunds("User One"));
        assertEquals(100, marketplace.getFunds("User Two"));
    }

    @Test
    void givenSourceAndDestinationSameThenTransferFundsReturnFalse() {

        assertEquals(false, marketplace.transferFunds("User One", "User One", 500));

        assertEquals(10000, marketplace.getFunds("User One"));
    }

    @Test
    void givenValidThenGetUserInventory() {

        assertEquals(resourceList, marketplace.getUserInventory("User One"));

        Resource resourceOne = marketplace.getUserInventory("User One").get(0);
        Resource resourceTwo = marketplace.getUserInventory("User One").get(1);
        Resource resourceThree = marketplace.getUserInventory("User One").get(2);
        Resource resourceFour = marketplace.getUserInventory("User One").get(3);

        assertEquals(wood, marketplace.getUserInventory("User One").get(0));
            assertEquals(1, resourceOne.getId());
            assertEquals(1, resourceOne.getCost());
            assertEquals(1000, resourceOne.getQuantity());
            assertEquals("wood", resourceOne.getName());

        assertEquals(iron, marketplace.getUserInventory("User One").get(1));
            assertEquals(2, resourceTwo.getId());
            assertEquals(2, resourceTwo.getCost());
            assertEquals(1000, resourceTwo.getQuantity());
            assertEquals("iron", resourceTwo.getName());

        assertEquals(silver, marketplace.getUserInventory("User One").get(2));
            assertEquals(3, resourceThree.getId());
            assertEquals(5, resourceThree.getCost());
            assertEquals(1000, resourceThree.getQuantity());
            assertEquals("silver", resourceThree.getName());

        assertEquals(gold, marketplace.getUserInventory("User Two").get(3));
            assertEquals(4, resourceFour.getId());
            assertEquals(10, resourceFour.getCost());
            assertEquals(1000, resourceFour.getQuantity());
            assertEquals("gold", resourceFour.getName());

    }

    @Test
    void givenValidThenCalculateTotal() {

        assertEquals(100, marketplace.calculateTotal(100, 1));
    }

    @Test
    void givenInvalidIDThenCalculateTotalReturnsError() {

        assertEquals(-1, marketplace.calculateTotal(100, 5));
    }

    @Test
    void givenInvalidQuantityThenCalculateTotalReturnsError() {

        assertEquals(-1, marketplace.calculateTotal(-100, 1));
    }

    @Test
    void givenValidThenNotifyUserResource() {

        assertEquals(true, marketplace.notifyUserResource("User One", 1));
        String string = String.format("Your 1$ is now 2$}.", marketplace.marketResources.get(1).getName(), userList.get(0).userResources.get(1).getQuantity());
    }

    @Test
    void givenInvalidUserThenNotifyUserResourceReturnFalse() {

        assertEquals(false, marketplace.notifyUserResource("User Five", 1));

    }

    @Test
    void givenInvalidResourceThenNotifyUserResourceReturnError() {

        assertEquals(false, marketplace.notifyUserResource("User One", 11));

    }

    @Test
    void givenValidThenNotifyUserCurrency() {

        assertEquals(true, marketplace.notifyUserCurrency("User One"));
        String string = String.format("Your 1$ is now 2$}.", marketplace.marketResources.get(1).getName(), userList.get(0).userResources.get(1).getQuantity());
    }

    @Test
    void givenInvalidUserThenNotifyUserCurrencyReturnFalse() {

        assertEquals(false, marketplace.notifyUserCurrency("User Five"));

    }

}