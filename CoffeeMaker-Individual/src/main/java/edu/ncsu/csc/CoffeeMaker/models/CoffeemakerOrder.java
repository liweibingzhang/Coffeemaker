package edu.ncsu.csc.CoffeeMaker.models;

import java.io.Serializable;
import java.util.Map;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Holds information about an order, including multiple recipes, status, and
 * creation time.
 *
 * @author finnt
 *
 */
@Entity
public class CoffeemakerOrder extends DomainObject {

    /**
     * ID of the object for hibernate.
     */
    @Id
    @GeneratedValue
    private Long                 id;

    /**
     * Username associated with the order.
     */
    private String               name;

    /**
     * Recipe names with corresponding quantities.
     */
    @ElementCollection ( fetch = FetchType.EAGER )
    private Map<String, Integer> recipes;

    /**
     * If the order is fulfilled yet.
     */
    private boolean              fullfilled;

    /**
     * If the order is picked up yet.
     */
    private boolean              pickedUp;

    /**
     * Milliseconds since Jan 1 1970 when the order was created.
     */
    private long                 creationTime;

    /**
     * Empty constructor for hibernate.
     */
    public CoffeemakerOrder () {

    }

    /**
     * Constuctor to build a new order with some recipes and quantities in it.
     *
     * @param recipes
     *            Map from recipe name to amount of the recipe in the order
     */
    public CoffeemakerOrder ( final Map<String, Integer> recipes ) {
        this.recipes = recipes;
    }

    /**
     * Hibernate getID
     *
     * @return ID for hibernate, or identification of this order.
     */
    @Override
    public Serializable getId () {
        // TODO Auto-generated method stub
        return id;
    }

    /**
     * Set the ID of the CoffeemakerOrder (Used by Hibernate)
     *
     * @param id
     *            the ID
     */
    public void setId ( final Long id ) {
        this.id = id;
    }

    /**
     * Gets the username associated with this order.
     *
     * @return Username associated with this order
     */
    public String getUsername () {
        return name;
    }

    /**
     * Gets the recipes and respective quantities in this order
     *
     * @return Map from recipe name to amount of the recipe in the order
     */
    public Map<String, Integer> getRecipes () {
        return recipes;
    }

    /**
     * Check if the order is fulfilled
     *
     * @return True if the order is fulfilled.
     */
    public boolean isFullfilled () {
        return fullfilled;
    }

    /**
     * Check if the order has been picked up.
     *
     * @return True if the order has been picked up
     */
    public boolean isPickedUp () {
        return pickedUp;
    }

    /**
     * Finalize creation of an order, by associating a user and initializing
     * creation time.
     *
     * @param username
     *            Username of the user this order belongs to
     */
    public void create ( final String username ) {
        name = username;
        fullfilled = false;
        pickedUp = false;
        creationTime = System.currentTimeMillis();
    }

    /**
     * Fulfills the order if possible
     *
     * @return Always true
     */
    public boolean fullfil () {
        if ( fullfilled ) {
            return fullfilled;
        }

        else {
            fullfilled = true;
            return fullfilled;
        }

    }

    /**
     * Picks up the order if possible
     *
     * @return Always true
     */
    public boolean pickup () {
        if ( pickedUp ) {
            return pickedUp;
        }
        else {
            pickedUp = true;
            return pickedUp;
        }

    }

    /**
     * Gets the creation time of this order.
     *
     * @return Milliseconds since Jan 1 1970 when the order was created.
     */
    public long getCreationTime () {
        return creationTime;
    }
}
