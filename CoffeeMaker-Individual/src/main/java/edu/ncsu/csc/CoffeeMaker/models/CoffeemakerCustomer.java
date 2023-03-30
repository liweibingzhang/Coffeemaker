package edu.ncsu.csc.CoffeeMaker.models;

public class CoffeemakerCustomer extends CoffeemakerUser {

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
