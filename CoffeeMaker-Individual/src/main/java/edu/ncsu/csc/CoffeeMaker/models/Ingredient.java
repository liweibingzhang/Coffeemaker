package edu.ncsu.csc.CoffeeMaker.models;

import java.util.Objects;

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
     * @param name
     *            the type of the ingredient
     * @param amount
     *            the amount of the ingredient
     *
     * @throws IllegalArgumentException
     *             if the name or amount is invalid (see setters)
     */
    public Ingredient ( final String name, final Integer amount ) {
        setName( name );
        setAmount( amount );
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
     * UNSAFE. Instantiates a new ingredient with the specified amount.
     *
     * Use this only to test that error-checking occurs in the service methods.
     * This does not check that
     *
     * @param name
     *            the type of the ingredient
     * @param amount
     *            the amount of the ingredient
     * @param unsafe
     *            must be true to indicate knowledge that this method is unsafe
     *
     * @throws IllegalArgumentException
     *             if the unsafe parameter is not set
     */
    public Ingredient ( final String name, final Integer amount, final boolean unsafe ) {
        if ( !unsafe ) {
            throw new IllegalArgumentException( "Use the other safe constructor" );
        }

        this.name = name;
        this.amount = amount;
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
     * Sets ingredient's name.
     *
     * @param name
     *            the new name
     *
     * @throws IllegalArgumentException
     *             if the name is null or empty
     */
    public void setName ( final String name ) {
        if ( name == null || "".equals( name ) ) {
            throw new IllegalArgumentException( "Ingredient name must be provided" );
        }
        this.name = name;
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

    @Override
    public int hashCode () {
        return Objects.hash( amount, name );
    }

    @Override
    public boolean equals ( final Object obj ) {
        if ( this == obj ) {
            return true;
        }
        if ( obj instanceof Ingredient ) {
            final Ingredient other = (Ingredient) obj;
            return Objects.equals( amount, other.amount ) && Objects.equals( name, other.name );
        }
        return false;
    }

}
