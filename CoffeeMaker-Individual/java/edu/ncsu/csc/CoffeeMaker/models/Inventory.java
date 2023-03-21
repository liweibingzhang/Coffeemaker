package edu.ncsu.csc.CoffeeMaker.models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 * Inventory for the coffee maker. Inventory is tied to the database using
 * Hibernate libraries. See InventoryRepository and InventoryService for the
 * other two pieces used for database support.
 *
 * @author Kai Presler-Marshall
 * @author Osama Albahrani (osalbahr)
 */
@Entity
public class Inventory extends DomainObject {

    /** id for inventory entry */
    @Id
    @GeneratedValue
    private Long             id;

    /** Inventory ingredients */
    @OneToMany ( cascade = CascadeType.ALL, fetch = FetchType.EAGER )
    private List<Ingredient> ingredients;

    /**
     * Empty constructor for Hibernate
     */
    public Inventory () {
        // Intentionally empty so that Hibernate can instantiate
        // Inventory object.
    }

    /**
     * Use this to create inventory with specified amts.
     *
     * @param ingredients
     *            a list of ingredients
     */
    public Inventory ( final List<Ingredient> ingredients ) {
        setIngredients( ingredients );
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
     * Set the ID of the Inventory (Used by Hibernate)
     *
     * @param id
     *            the ID
     */
    public void setId ( final Long id ) {
        this.id = id;
    }

    /**
     * Gets the ingredients list
     *
     * @return the ingredients list
     */
    public List<Ingredient> getIngredients () {
        return ingredients;
    }

    /**
     * Sets the ingredients to the given list
     *
     * @param ingredients
     *            the new ingredients
     */
    public void setIngredients ( final List<Ingredient> ingredients ) {
        this.ingredients = ingredients;
    }

    /**
     * Sets the number of the specified ingredient to the specified amount.
     *
     * @param name
     *            the ingredient name
     * @param amt
     *            amount of the ingredient to set
     * @throws IllegalArgumentException
     *             if the name is null or the amount is negative
     */
    public void setIngredient ( final String name, final Integer amt ) throws IllegalArgumentException {
        if ( name == null ) {
            throw new IllegalArgumentException( "Ingredient name cannot be null" );
        }

        if ( amt < 0 ) {
            throw new IllegalArgumentException( "Units of [" + name + "] must be a positive integer" );
        }

        final Ingredient ing = findIngredient( name );
        if ( ing == null ) {
            ingredients.add( new Ingredient( name, amt ) );
        }
        else {
            setIngredientByReference( ing, amt );
        }
    }

    /**
     * Sets the number of units of the specified ingredient to the specified
     * amount.
     *
     * @param ing
     *            the ingredient
     * @param amt
     *            the amount
     */
    private void setIngredientByReference ( final Ingredient ing, final Integer amt ) {
        ing.setAmount( amt );
    }

    /**
     * Checks if the given string is a valid ingredient amount
     *
     * @param strAmount
     *            amount as a string
     * @return checked amount
     * @throws IllegalArgumentException
     *             if the parameter isn't a positive integer
     */
    public Integer checkIngredient ( final String strAmount ) throws IllegalArgumentException {
        Integer amt = 0;
        try {
            amt = Integer.parseInt( strAmount );
        }
        catch ( final NumberFormatException e ) {
            throw new IllegalArgumentException( "Units of ingredient must be a positive integer" );
        }
        if ( amt < 0 ) {
            throw new IllegalArgumentException( "Units of ingredient must be a positive integer" );
        }

        return amt;
    }

    /**
     * Returns the amount of the specified ingredient in the inventory.
     *
     * @param name
     *            - the name of the ingredient
     * @return the amount, or null if not found
     * @throws IllegalArgumentException
     *             if the name is null
     */
    public Integer getIngredient ( final String name ) throws IllegalArgumentException {
        if ( name == null ) {
            throw new IllegalArgumentException( "Ingredient name cannot be null" );
        }

        final Ingredient ing = findIngredient( name );
        return ing == null ? null : ing.getAmount();

    }

    /**
     * Returns true if there are enough ingredients to make the beverage.
     *
     * @param r
     *            recipe to check if there are enough ingredients
     * @return true if enough ingredients to make the beverage
     */
    public boolean enoughIngredients ( final Recipe r ) {
        for ( final Ingredient i : ingredients ) {
            for ( final Ingredient current : r.getIngredients() ) {
                if ( i.getName().equals( current.getName() ) ) {
                    if ( i.getAmount() < current.getAmount() ) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Removes the ingredients used to make the specified recipe. Assumes that
     * the user has checked that there are enough ingredients to make
     *
     * @param r
     *            recipe to make
     * @return true if recipe is made.
     */
    public boolean useIngredients ( final Recipe r ) {
        // Store the ingredients in a HashMap, getting O(n) for
        // useIngredients
        final Map<String, Integer> recipeMap = new HashMap<>();
        final List<Ingredient> recipeIngredients = r.getIngredients();
        for ( final Ingredient ing : recipeIngredients ) {
            recipeMap.put( ing.getName(), ing.getAmount() );
        }

        if ( enoughIngredients( r ) ) {
            for ( final Ingredient ing : this.getIngredients() ) {
                final Integer amt = recipeMap.get( ing.getName() );

                // Skip irrelevant ingredients
                if ( amt == null ) {
                    continue;
                }

                setIngredientByReference( ing, ing.getAmount() - amt );
            }

            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Adds ingredients to the inventory
     *
     * @param ingredients
     *            the list of ingredients
     *
     * @return true if successful, false if not
     */
    public boolean addIngredients ( final List<Ingredient> ingredients ) {
        if ( !validIngredients( ingredients ) ) {
            return false;
        }

        for ( final Ingredient ing : ingredients ) {
            addIngredient( ing.getName(), ing.getAmount() );
        }

        return true;
    }

    /**
     * Adds the amount to the ingredient with the given name. If the ingredient
     * cannot be found, it is newly created.
     *
     * The name and amount are assumed to be valid.
     *
     * @param name
     *            - the name of the ingredient
     * @param amount
     *            - the amount of the ingredient
     */
    public void addIngredient ( final String name, final Integer amount ) {
        final Ingredient ing = findIngredient( name );
        if ( ing != null ) {
            setIngredient( name, amount + ing.getAmount() );

        }
        else {
            setIngredient( name, amount );
        }

    }

    /**
     * The ingredients list is valid if all ingredients have a non-null name,
     * non-negative amound.
     *
     * @param ingredients
     *            - the list of ingredients to validate
     * @return true if the list is valid, false if not
     */
    private boolean validIngredients ( final List<Ingredient> ingredients ) {
        for ( final Ingredient ing : ingredients ) {
            if ( ing == null || ing.getName() == null || ing.getAmount() < 0 ) {
                return false;
            }
        }

        return true;
    }

    /**
     * Returns an Ingredient with the specified name
     *
     * @param name
     *            the name of the Ingredient
     * @return the Ingredient, or null if not found
     */
    private Ingredient findIngredient ( final String name ) {
        for ( final Ingredient ing : this.getIngredients() ) {
            if ( ing.getName().equals( name ) ) {
                return ing;
            }
        }

        return null;
    }

    /**
     * Returns a string describing the current contents of the inventory.
     *
     * @return String
     */
    @Override
    public String toString () {
        final StringBuffer buf = new StringBuffer();

        final List<Ingredient> ings = this.getIngredients();

        if ( ingredients.isEmpty() ) {
            return "Empty inventory";
        }

        for ( final Ingredient ing : ings ) {
            buf.append( ing.getName() );
            buf.append( ": " );
            buf.append( ing.getAmount() );
            buf.append( "\n" );
        }

        return buf.toString();
    }

}
