package edu.ncsu.csc.CoffeeMaker.models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.Min;

/**
 * Recipe for the coffee maker. Recipe is tied to the database using Hibernate
 * libraries. See RecipeRepository and RecipeService for the other two pieces
 * used for database support.
 *
 * @author Kai Presler-Marshall
 * @author Osama Albahrani (osalbahr)
 */
@Entity
public class Recipe extends DomainObject {

    /** Recipe id */
    @Id
    @GeneratedValue
    private Long             id;

    /** Recipe name */
    private String           name;

    /** Recipe price */
    @Min ( 0 )
    private Integer          price;

    /** Recipe ingredients */
    @OneToMany ( cascade = CascadeType.ALL, fetch = FetchType.EAGER )
    private List<Ingredient> ingredients;

    /**
     * Creates a default recipe for the coffee maker.
     */
    public Recipe () {
        // Intentionally empty so that Hibernate can instantiate
    }

    /**
     * Constructor for recipe
     *
     * @param name
     *            - the name of the recipe
     * @param ingredients
     *            - list of ingredients
     * @param price
     *            - the price of the recipe
     */
    public Recipe ( final String name, final List<Ingredient> ingredients, final Integer price ) {
        this.name = name;
        this.ingredients = ingredients;
        this.price = price;
    }

    /**
     * Check if all ingredient fields in the recipe are 0
     *
     * @return true if all ingredient fields are 0, otherwise return false
     */
    public boolean checkRecipe () {
        for ( final Ingredient ing : ingredients ) {
            if ( ing.getAmount() != 0 ) {
                return false;
            }
        }

        return true;
    }

    /**
     * Get the ID of the Recipe
     *
     * @return the ID
     */
    @Override
    public Long getId () {
        return id;
    }

    /**
     * Set the ID of the Recipe (Used by Hibernate)
     *
     * @param id
     *            the ID
     */
    @SuppressWarnings ( "unused" )
    private void setId ( final Long id ) {
        this.id = id;
    }

    /**
     * Returns name of the recipe.
     *
     * @return Returns the name.
     */
    public String getName () {
        return name;
    }

    /**
     * Sets the recipe name.
     *
     * @param name
     *            The name to set.
     */
    public void setName ( final String name ) {
        this.name = name;
    }

    /**
     * Returns the price of the recipe.
     *
     * @return Returns the price.
     */
    public Integer getPrice () {
        return price;
    }

    /**
     * Sets the recipe price.
     *
     * @param price
     *            The price to set.
     */
    public void setPrice ( final Integer price ) {
        this.price = price;
    }

    /**
     * Gets the list of ingredients.
     *
     * @return the list of ingredients
     */
    public List<Ingredient> getIngredients () {
        return ingredients;
    }

    /**
     * Get a specific ingredient
     *
     * @param name
     *
     * @return Ingredient - the ingredient of the given name
     * @throws IllegalArgumentException
     *             if the String is null or the ingredient is not there
     */
    public Ingredient getIngredient ( final String name ) {
        if ( name == null ) {
            throw new IllegalArgumentException( "Invalid Inputs" );
        }

        for ( final Ingredient i : ingredients ) {
            if ( i.getName().equals( name ) ) {
                return i;
            }
        }

        throw new IllegalArgumentException( "Ingredient can't be found due to being missing" );

    }

    /**
     * Set a specific ingredient amount
     *
     * @param name
     *            - the name of the ingredient
     * @param amount
     *            - the amount to set to
     * @throws IllegalArgumentException
     *             if the ingredient is null or having duplicated
     */
    public void setIngredientAmount ( final String name, final int amount ) {
        if ( amount < 0 || "".equals( name ) ) {
            throw new IllegalArgumentException( "Invalid Inputs" );
        }

        for ( final Ingredient i : ingredients ) {
            if ( i.getName().equals( name ) ) {
                i.setAmount( amount );
                return;
            }
        }

        throw new IllegalArgumentException( "Ingredient can't be added due to being missing" );
    }

    /**
     * Delegates to the List's add method.
     *
     * @param ing
     *            - the ingredient to be added
     * @return true if successfully added, false otherwise
     * @throws IllegalArgumentException
     *             if the ingredient is null or having duplicated
     */
    public boolean addIngredient ( final Ingredient ing ) {
        if ( ing == null ) {
            throw new IllegalArgumentException( "Ingredient can't be null" );
        }

        for ( final Ingredient i : ingredients ) {
            if ( i.getName().equals( ing.getName() ) ) {
                throw new IllegalArgumentException( "Ingredient can't be added due to being a duplicated" );
            }
        }

        return ingredients.add( ing );
    }

    /**
     * Delegates to the List's remove method.
     *
     * @param ing
     *            the ingredient to be added
     * @return true if successfully added, false otherwise
     * @throws IllegalArgumentException
     *             if the ingredient is not there or null
     */
    public boolean removeIngredient ( final Ingredient ing ) {
        // reminder to make this implementation better
        if ( ing == null ) {
            throw new IllegalArgumentException( "Ingredient can't be null" );
        }

        for ( final Ingredient i : ingredients ) {
            if ( i.getName().equals( ing.getName() ) ) {
                return ingredients.remove( i );
            }
        }

        throw new IllegalArgumentException( "Ingredient can't be removed due to being missing" );
    }

    /**
     * Update Recipe
     *
     * @param r2
     *            - recipe that we updated to
     * @throws IllegalArgumentException
     *             - recipe is null
     */
    public void updateRecipe ( final Recipe r2 ) {
        if ( r2 == null ) {
            throw new IllegalArgumentException( "Invalid Units" );
        }

        this.name = r2.getName();
        this.ingredients = r2.getIngredients();
        this.price = r2.getPrice();
    }

    /**
     * Returns the name of the recipe.
     *
     * @return String
     */
    @Override
    public String toString () {
        return name + " " + ingredients;
    }

    @Override
    public int hashCode () {
        final int prime = 31;
        Integer result = 1;
        result = prime * result + ( ( name == null ) ? 0 : name.hashCode() );
        return result;
    }

    @Override
    public boolean equals ( final Object obj ) {
        if ( this == obj ) {
            return true;
        }
        if ( obj == null ) {
            return false;
        }
        if ( getClass() != obj.getClass() ) {
            return false;
        }
        final Recipe other = (Recipe) obj;
        if ( name == null ) {
            if ( other.name != null ) {
                return false;
            }
        }
        else if ( !name.equals( other.name ) ) {
            return false;
        }
        return true;
    }

}
