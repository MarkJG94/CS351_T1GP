//import org.junit.Before;
//import org.junit.Test;
//import org.junit.jupiter.api.BeforeEach;
//
//import java.util.ArrayList;
//
//import static org.junit.Assert.*;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//public class UserTest {
//
//    ArrayList<Resource> resourceList = new ArrayList<Resource>();
//    Resource wood = new Resource(1, 1, 1000, "wood",1);
//    Resource iron = new Resource(2, 2, 1000, "iron",2);
//    Resource silver = new Resource(3, 5, 1000, "silver",5);
//    Resource gold = new Resource(4, 10, 1000, "gold",10);
//
//    User user1 = new User("User One", "password", resourceList, 10000);
//
//    @Before
//    public void setUp() throws Exception {
//        resourceList.add(wood);
//        resourceList.add(iron);
//        resourceList.add(silver);
//        resourceList.add(gold);
//    }
//
//    @Test
//    public void givenValidUserNameAndAmountThenAddFunds() {
//
//        assertEquals(10100, user1.addFunds("User One", 100));
//    }
//
//    @Test
//    public void givenValidUserNameAndInvalidAmountThenAddFundsReturnsFalse() {
//
//        assertEquals(-1, user1.addFunds("User One", -100));
//    }
//
//    @Test
//    public void givenInvalidUserNameAndValidAmountThenAddFundsReturnsFalse() {
//
//        assertEquals(-1, user1.addFunds("User Five", 100));
//    }
//
//    @Test
//    public void givenInvalidUserNameAndAmountThenAddFundsReturnsFalse() {
//
//        assertEquals(-1, user1.addFunds("User Five", -100));
//    }
//
//    @Test
//    public void givenValidUserNameAndAmountThenDeductFunds() {
//
//        assertEquals(9900, user1.deductFunds("User One", 100));
//    }
//
//    @Test
//    public void givenValidUserNameAndInvalidAmountThenDeductFundsReturnsFalse() {
//
//        assertEquals(-1, user1.deductFunds("User One", -100));
//    }
//
//    @Test
//    public void givenInvalidUserNameAndValidAmountThenDeductFundsReturnsFalse() {
//
//        assertEquals(-1, user1.deductFunds("User Five", 100));
//    }
//
//    @Test
//    public void givenInvalidUserNameAndAmountThenDeductFundsReturnsFalse() {
//
//        assertEquals(-1, user1.deductFunds("User Five", -100));
//    }
//
//
//    @Test
//    public void givenValidInformationThenAddResource() {
//
//        user1.addResource(1, 100, "User One");
//
//        assertEquals(1100, user1.userResources.get(0).getQuantity());
//    }
//
//    @Test
//    public void givenInvalidUserNameThenAddResourceReturnsFalse() {
//
//        assertEquals(false, user1.addResource(1, 100, "User Two"));
//        assertEquals(1000, user1.userResources.get(0).getQuantity());
//    }
//
//    @Test
//    public void givenInvalidResourceIdThenAddResourceReturnsFalse() {
//
//        assertEquals(false, user1.addResource(11, 100, "User Two"));
//        assertEquals(1000, user1.userResources.get(0).getQuantity());
//    }
//
//    @Test
//    public void givenInvalidQuantityThenAddResourceReturnsFalse() {
//
//        assertEquals(false, user1.addResource(1, -100, "User Two"));
//        assertEquals(1000, user1.userResources.get(0).getQuantity());
//    }
//
//    @Test
//    public void givenValidInformationThenRemoveResource() {
//
//        user1.addResource(1, 100, "User One");
//
//        assertEquals(1100, user1.userResources.get(0).getQuantity());
//    }
//
//    @Test
//    public void givenInvalidUserNameThenRemoveResourceReturnsFalse() {
//
//        assertEquals(false, user1.addResource(1, 100, "User Two"));
//        assertEquals(1000, user1.userResources.get(0).getQuantity());
//    }
//
//    @Test
//    public void givenInvalidResourceIdThenRemoveResourceReturnsFalse() {
//
//        assertEquals(false, user1.addResource(11, 100, "User Two"));
//        assertEquals(1000, user1.userResources.get(0).getQuantity());
//    }
//
//    @Test
//    public void givenInvalidQuantityThenRemoveResourceReturnsFalse() {
//
//        assertEquals(false, user1.addResource(1, -100, "User Two"));
//        assertEquals(1000, user1.userResources.get(0).getQuantity());
//    }
//
//
//
//
//
//
//
//
//    @Test
//    public void givenCorrectAmountThenValidateCurrency() {
//
//        assertEquals(true, user1.validateCurrency(5000));
//
//    }
//
//    @Test
//    public void givenInvalidAmountThenValidateCurrencyReturnFalse() {
//
//        assertEquals(false, user1.validateCurrency(50000));
//
//    }
//
//    @Test
//    public void getFunds() {
//
//        assertEquals(10000, user1.getFunds());
//
//    }
//
//    @Test
//    public void getUserName() {
//
//        assertEquals("User One", user1.getUsername());
//
//    }
//
//    @Test
//    public void getResourceQuantity() {
//
//        assertEquals(1000, user1.getResourceQuantity(1));
//        assertEquals(1000, user1.getResourceQuantity(1));
//        assertEquals(1000, user1.getResourceQuantity(2));
//        assertEquals(1000, user1.getResourceQuantity(3));
//
//    }
//
//    @Test
//    public void getUserInventory() {
//
//        assertEquals("wood", user1.userResources.get(0).getName());
//        assertEquals("iron", user1.userResources.get(1).getName());
//        assertEquals("silver", user1.userResources.get(2).getName());
//        assertEquals("gold", user1.userResources.get(3).getName());
//
//        assertEquals(1000, user1.userResources.get(0).getQuantity());
//        assertEquals(1000, user1.userResources.get(1).getQuantity());
//        assertEquals(1000, user1.userResources.get(2).getQuantity());
//        assertEquals(1000, user1.userResources.get(3).getQuantity());
//
//        assertEquals(1, user1.userResources.get(0).getCost());
//        assertEquals(2, user1.userResources.get(1).getCost());
//        assertEquals(5, user1.userResources.get(2).getCost());
//        assertEquals(10, user1.userResources.get(3).getCost());
//
//    }
//
//}
//
//
//
//
////
////
////
////    @Test
////    void getUserInventory() {
////    }
////
////    @Test
////    void getResourceIndex() {
////    }
////
////    @Test
////    void getResourceQuantity() {
////    }
////
////
////
//
////
////    @Test
////    void deductFunds() {
////    }
////
////    @Test
////    void validateCurrency() {
////    }
////
////    @Test
////    void getFunds() {
////    }
////
////    @Test
////    void getUsername() {
////    }
////
////    @Test
////    void addResource() {
////    }
////
////    @Test
////    void removeResource() {
////    }
