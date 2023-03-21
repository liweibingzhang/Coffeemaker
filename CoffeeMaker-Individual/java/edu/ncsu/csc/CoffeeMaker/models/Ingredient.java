package edu.ncsu.csc.CoffeeMaker.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.Min;

/**
 * Holds the amount of an ingredient of type IngredientType. Only the amount is
 * adjustable.
 *
 * @author Osama Albahrani (osalbahr)
 */
@Entity
public class Ingredient extends DomainObject {
    /** Recipe id */
    @Id
    @GeneratedValue
    private Long    id;

    /** Ingredient type, represented as a string. */
    private String  name;

    /** Ingredient amount. */
    @Min ( 0 )
    private Integer amount;

    /**
     * Empty constructor for Hibernate
     */
    public Ingredient () {
        // Intentionally empty so that Hibernate can instantiate
        // Inventory object.
    }

    /**
     * Instantiates a new ingredient with the specified amount.
     *
     * @param type
     *            the type of the ingredient
     * @param amount
     *            the amount of the ingredient
     */
    public Ingredient ( final String type, final Integer amount ) {
        this.name = type;
        // setAmount( amount );
        this.amount = amount;
    }

    /**
     * Instantiates a new ingredient with an amount of 0.
     *
     * @param type
     *            the type of ingredient
     */
    public Ingredient ( final String type ) {
        this( type, 0 );
    }

    /**
     * Gets the ingredient's type
     *
     * @return the type
     */
    public String getName () {
        return name;
    }

    /**
     * Gets the ingredient's amount
     *
     * @return the amount
     */
    public Integer getAmount () {
        return amount;
    }

    /**
     * Sets the ingredient's amount.
     *
     * @param amount
     *            the new amount
     * @throws IllegalArgumentException
     *             if the amount is negative
     */
    public void setAmount ( final Integer amount ) {
        if ( amount < 0 ) {
            throw new IllegalArgumentException( "Ingredient amount must be non-negative" );
        }
        this.amount = amount;
    }

    /**
     * Get the ID of the Ingredient
     *
     * @return the ID
     */
    @Override
    public Long getId () {
        return this.id;
    }

    @Override
    public String toString () {
        return this.getName() + "=" + this.getAmount();
    }
}
