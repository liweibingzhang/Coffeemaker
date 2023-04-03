package edu.ncsu.csc.CoffeeMaker.models;

import edu.ncsu.csc.CoffeeMaker.models.enums.Permissions;

public class CoffeemakerCustomer extends CoffeemakerUser {

    public CoffeemakerCustomer ( final String name, final String password ) {
        setName( name );
        setPasswordHash( password );
    }

    @Override
    public boolean hasPermission ( final Permissions permission ) {
        switch ( permission ) {
            case CanModifyInventory:
                return false;
            case CanEditRecipe:
                return false;
            case CanCreateRecipe:
                return false;
            case CanOrderRecipe:
                return true;
            case CanMakeRecipe:
                return false;
            case CanFulfillOrder:
                return false;
            case CanPickupOrder:
                return true;
            default:
                return false;
        }
    }
}
