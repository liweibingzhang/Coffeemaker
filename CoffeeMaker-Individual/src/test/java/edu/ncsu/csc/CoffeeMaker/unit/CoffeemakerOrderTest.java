/**
 *
 */
package edu.ncsu.csc.CoffeeMaker.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import edu.ncsu.csc.CoffeeMaker.TestConfig;
import edu.ncsu.csc.CoffeeMaker.models.CoffeemakerOrder;
import edu.ncsu.csc.CoffeeMaker.models.CoffeemakerOrderQueue;

/**
 * Tests CoffeemakerUser.java methods
 *
 * @author Finn Bacheldor
 *
 */
@ExtendWith ( SpringExtension.class )
@EnableAutoConfiguration
@SpringBootTest ( classes = TestConfig.class )
class CoffeemakerOrderTest {

    @Test
    public void orderCreationTest () {
        final CoffeemakerOrder order = new CoffeemakerOrder( new TreeMap<String, Integer>() );
        order.create( "testUser" );
        assertEquals( "testUser", order.getUsername() );
        assertFalse( order.isFullfilled() );
        assertFalse( order.isPickedUp() );
    }

    @Test
    public void testFulfillPickup () {
        final CoffeemakerOrder order = new CoffeemakerOrder( new TreeMap<String, Integer>() );
        order.create( "testUser" );
        assertTrue( order.fullfil() );
        assertTrue( order.pickup() );
        assertFalse( order.fullfil() );
        assertFalse( order.pickup() );
    }

    @Test
    public void testPlaceOrders () {
        final List<CoffeemakerOrder> orderList = new ArrayList<CoffeemakerOrder>();
        final CoffeemakerOrderQueue cmoq = new CoffeemakerOrderQueue( orderList );
        final CoffeemakerOrder order = new CoffeemakerOrder( new TreeMap<String, Integer>() );
        order.create( "testUser" );
        assertEquals( 0, cmoq.getOrders().size() );
        cmoq.placeOrder( order );
        assertEquals( 1, cmoq.getOrders().size() );
    }

    @Test
    public void testSetIds () {
        final List<CoffeemakerOrder> orderList = new ArrayList<CoffeemakerOrder>();
        final CoffeemakerOrderQueue cmoq = new CoffeemakerOrderQueue( orderList );
        final CoffeemakerOrder order = new CoffeemakerOrder( new TreeMap<String, Integer>() );
        cmoq.setId( (long) 7 );
        assertEquals( 7, Long.valueOf( cmoq.getId().toString() ) );
        order.setId( (long) 8 );
        assertEquals( 8, Long.valueOf( order.getId().toString() ) );
    }
}
