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
        // Create test customer
        final CoffeemakerUser customer = new CoffeemakerUser( "user", "pass", "Customer" );
        // Create test staff
        final CoffeemakerUser staff = new CoffeemakerUser( "user2", "pass", "Staff" );

        // Verify that customers are initialized with the correct permissions
        assertTrue( !customer.hasPermission( Permissions.CanCreateRecipe ) );
        assertTrue( !customer.hasPermission( Permissions.CanEditRecipe ) );
        assertTrue( !customer.hasPermission( Permissions.CanFulfillOrder ) );
        assertTrue( !customer.hasPermission( Permissions.CanMakeRecipe ) );
        assertTrue( !customer.hasPermission( Permissions.CanModifyInventory ) );
        assertTrue( customer.hasPermission( Permissions.CanOrderRecipe ) );
        assertTrue( customer.hasPermission( Permissions.CanPickupOrder ) );

        // Verify that customers are initialized with the correct permissions
        assertTrue( staff.hasPermission( Permissions.CanCreateRecipe ) );
        assertTrue( staff.hasPermission( Permissions.CanEditRecipe ) );
        assertTrue( staff.hasPermission( Permissions.CanFulfillOrder ) );
        assertTrue( staff.hasPermission( Permissions.CanMakeRecipe ) );
        assertTrue( staff.hasPermission( Permissions.CanModifyInventory ) );
        assertTrue( staff.hasPermission( Permissions.CanOrderRecipe ) );
        assertTrue( staff.hasPermission( Permissions.CanPickupOrder ) );
    }

    /**
     * This test method tests the setting of usernames for users
     */
    @Test
    void testName () {
        // Make test customer
        final CoffeemakerUser customer = new CoffeemakerUser( "user", "pass", "Customer" );
        // Check the username is set correctly
        assertEquals( "user", customer.getName() );
        // Set username to something else
        customer.setName( "nameo" );
        // Verify changes were persistant
        assertEquals( "nameo", customer.getName() );
    }

    /**
     * This test method tests the setting and hashing of passwords for users
     */
    @Test
    void testPassword () {
        // Create test customer
        final CoffeemakerUser customer = new CoffeemakerUser( "user", "pass", "Customer" );
        // Check that hash worked properly
        assertTrue( customer.compareHash( "pass" ) );
        // Check invalid password would not work
        assertTrue( !customer.compareHash( "pasz" ) );
        // Set password
        customer.setPasswordHash( "pasz" );
        // Check that hash updated
        assertTrue( customer.compareHash( "pasz" ) );
        // Make sure former hash is no longer valid
        assertTrue( !customer.compareHash( "pass" ) );
    }

    /**
     * This test method tests the changing of session id values based on logging
     * in and logging out
     */
    @Test
    void testLoginLogout () {
        // Create test customer
        final CoffeemakerUser customer = new CoffeemakerUser( "user", "pass", "Customer" );
        // Login and save session id
        final String id = customer.login();
        // Make sure session id is correct
        assertTrue( customer.compareSessionId( id ) );
        // Logout user
        customer.logout();
        // Make sure customer's current id cannot be used
        assertTrue( !customer.compareSessionId( id ) );
        // Create new session id after logging in again
        final String id2 = customer.login();
        // verify old session id no longer works
        assertTrue( !customer.compareSessionId( id ) );
        // Make sure current session id works
        assertTrue( customer.compareSessionId( id2 ) );
    }
}
