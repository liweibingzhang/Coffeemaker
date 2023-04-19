package edu.ncsu.csc.CoffeeMaker.models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class CoffeemakerOrderQueue extends DomainObject {
    /** User id */
    @Id
    @GeneratedValue
    private Long           id;

    @OneToMany ( cascade = CascadeType.MERGE, fetch = FetchType.EAGER )
    List<CoffeemakerOrder> orderQueue;

    public CoffeemakerOrderQueue () {

    }

    public CoffeemakerOrderQueue ( final List<CoffeemakerOrder> list ) {
        orderQueue = list;
    }

    /**
     * Returns the ID of the entry in the DB
     *
     * @return long
     */
    @Override
    public Long getId () {
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

    public void placeOrder ( final CoffeemakerOrder order ) {
        orderQueue.add( order );
    }

    public List<CoffeemakerOrder> getOrders () {
        return orderQueue;
    }
}
