/**
 *
 */
package edu.ncsu.csc.CoffeeMaker.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import edu.ncsu.csc.CoffeeMaker.models.enums.Permissions;

/**
 * Tests CoffeemakerUser.java methods
 *
 * @author Finn Bacheldor
 *
 */
class CoffeemakerUserTest {

    /**
     * Test method for
     * {@link edu.ncsu.csc.CoffeeMaker.models.Inventory#setId(java.lang.Long)}.
     */
    @Test
    void testPermssions () {
        final CoffeemakerUser customer = new CoffeemakerUser( "user", "pass", "Customer" );
        final CoffeemakerUser staff = new CoffeemakerUser( "user2", "pass", "Staff" );

        assertTrue( !customer.hasPermission( Permissions.CanCreateRecipe ) );
        assertTrue( !customer.hasPermission( Permissions.CanEditRecipe ) );
        assertTrue( !customer.hasPermission( Permissions.CanFulfillOrder ) );
        assertTrue( !customer.hasPermission( Permissions.CanMakeRecipe ) );
        assertTrue( !customer.hasPermission( Permissions.CanModifyInventory ) );
        assertTrue( customer.hasPermission( Permissions.CanOrderRecipe ) );
        assertTrue( customer.hasPermission( Permissions.CanPickupOrder ) );

        assertTrue( staff.hasPermission( Permissions.CanCreateRecipe ) );
        assertTrue( staff.hasPermission( Permissions.CanEditRecipe ) );
        assertTrue( staff.hasPermission( Permissions.CanFulfillOrder ) );
        assertTrue( staff.hasPermission( Permissions.CanMakeRecipe ) );
        assertTrue( staff.hasPermission( Permissions.CanModifyInventory ) );
        assertTrue( staff.hasPermission( Permissions.CanOrderRecipe ) );
        assertTrue( staff.hasPermission( Permissions.CanPickupOrder ) );
    }

    @Test
    void testName () {
        final CoffeemakerUser customer = new CoffeemakerUser( "user", "pass", "Customer" );
        assertEquals( "user", customer.getName() );
        customer.setName( "nameo" );
        assertEquals( "nameo", customer.getName() );
    }

    @Test
    void testPassword () {
        final CoffeemakerUser customer = new CoffeemakerUser( "user", "pass", "Customer" );
        assertTrue( customer.compareHash( "pass" ) );
        assertTrue( !customer.compareHash( "pasz" ) );
        customer.setPasswordHash( "pasz" );
        assertTrue( customer.compareHash( "pasz" ) );
        assertTrue( !customer.compareHash( "pass" ) );
    }

    @Test
    void testLoginLogout () {
        final CoffeemakerUser customer = new CoffeemakerUser( "user", "pass", "Customer" );
        final String id = customer.login();
        assertTrue( customer.compareSessionId( id ) );
        customer.logout();
        assertTrue( !customer.compareSessionId( id ) );
        final String id2 = customer.login();
        assertTrue( !customer.compareSessionId( id ) );
        assertTrue( customer.compareSessionId( id2 ) );
    }
}
