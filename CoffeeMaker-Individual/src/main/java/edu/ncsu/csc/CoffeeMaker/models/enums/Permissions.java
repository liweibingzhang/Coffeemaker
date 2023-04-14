package edu.ncsu.csc.CoffeeMaker.models.enums;

/**
 * Permission Enum for users. Add more to add more permissions.
 *
 * @author finnt
 *
 */
public enum Permissions {
    /** Can make changes to the inventory. */
    CanModifyInventory,
    /** Can make changes to recipes. */
    CanEditRecipe,
    /** Can use a recipe to make an order */
    CanCreateRecipe,
    /** Can place an order */
    CanOrderRecipe,
    /** Can create a new recipe */
    CanMakeRecipe,
    /** Can fulfill an order */
    CanFulfillOrder,
    /** Can pickup an order that is made */
    CanPickupOrder
}
