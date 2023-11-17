import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SimpleClientTest {

    SimpleClient client = new SimpleClient();
    ArrayList<Resource> resourceList = new ArrayList<Resource>();
    Resource wood = new Resource(1, 1, 1000, "wood",1);
    Resource iron = new Resource(2, 2, 1000, "iron",2);
    Resource silver = new Resource(3, 5, 1000, "silver",5);
    Resource gold = new Resource(4, 10, 1000, "gold",10);

    User user1 = new User("User One", "password", resourceList, 10000);

    @Before
    public void setUp() throws Exception {
        resourceList.add(wood);
        resourceList.add(iron);
        resourceList.add(silver);
        resourceList.add(gold);
    }

    @Test
    public void getUserInventory() {
        assertEquals(1, user1.getUserInventory().get(0).getCost());
        assertEquals(2, user1.getUserInventory().get(1).getCost());
        assertEquals(5, user1.getUserInventory().get(2).getCost());
        assertEquals(10, user1.getUserInventory().get(3).getCost());
    }

}