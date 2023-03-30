package edu.ncsu.csc.CoffeeMaker.models;

public class CoffeemakerStaff extends CoffeemakerUser {

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
