package edu.ncsu.csc.CoffeeMaker.models;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 * Queue and history of Coffeemaker orders. Behaves as a singleton that tracks
 * all orders placed into coffeemaker.
 *
 * @author finnt
 *
 */
@Entity
public class CoffeemakerOrderQueue extends DomainObject {
    /**
     * ID for hibernate
     */
    @Id
    @GeneratedValue
    private Long           id;

    /**
     * List of orders that have been created.
     */
    @OneToMany ( cascade = CascadeType.MERGE, fetch = FetchType.EAGER )
    List<CoffeemakerOrder> orderQueue;

    /**
     * Empty constructor for hibernate.
     */
    public CoffeemakerOrderQueue () {

    }

    /**
     * Constructor that takes an initialized array, may be empty
     *
     * @param list
     *            Array to be used to store order history.
     */
    public CoffeemakerOrderQueue ( final List<CoffeemakerOrder> list ) {
        orderQueue = list;
    }

    /**
     * Returns the ID of the entry in the DB
     *
     * @return long
     */
    @Override
    public Serializable getId () {
        return id;
    }

    /**
     * Set the ID of the CoffeemakerOrderQueue (Used by Hibernate)
     *
     * @param id
     *            the ID
     */
    public void setId ( final Long id ) {
        this.id = id;
    }

    /**
     * Adds a new order to the queue.
     *
     * @param order
     *            New order to add.
     */
    public void placeOrder ( final CoffeemakerOrder order ) {
        orderQueue.add( order );
    }

    /**
     * Gets all the orders currently in the history.
     *
     * @return List of orders in the order history.
     */
    public List<CoffeemakerOrder> getOrders () {
        return orderQueue;
    }
}
