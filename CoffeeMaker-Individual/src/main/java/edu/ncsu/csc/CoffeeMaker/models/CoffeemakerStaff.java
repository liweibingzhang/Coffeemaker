package edu.ncsu.csc.CoffeeMaker.models;

import edu.ncsu.csc.CoffeeMaker.models.enums.Permissions;

public class CoffeemakerStaff extends CoffeemakerUser {

    public CoffeemakerStaff ( final String name, final String password ) {
        setName( name );
        setPasswordHash( password );
    }

    @Override
    public boolean hasPermission ( final Permissions permission ) {
        switch ( permission ) {
            case CanModifyInventory:
                return true;
            case CanEditRecipe:
                return true;
            case CanCreateRecipe:
                return true;
            case CanOrderRecipe:
                return true;
            case CanMakeRecipe:
                return true;
            case CanFulfillOrder:
                return true;
            case CanPickupOrder:
                return true;
            default:
                return false;
        }
    }
}
