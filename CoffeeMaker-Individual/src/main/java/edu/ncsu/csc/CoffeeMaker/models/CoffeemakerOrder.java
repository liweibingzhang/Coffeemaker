package edu.ncsu.csc.CoffeeMaker.models;

import java.io.Serializable;
import java.util.Map;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class CoffeemakerOrder extends DomainObject {

    @Id
    @GeneratedValue
    private Long                 id;

    private String               name;

    @ElementCollection ( fetch = FetchType.EAGER )
    private Map<String, Integer> recipes;

    private boolean              fullfilled;

    private boolean              pickedUp;

    private long                 creationTime;

    public CoffeemakerOrder () {

    }

    public CoffeemakerOrder ( final Map<String, Integer> recipes ) {
        this.recipes = recipes;
    }

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

    public String getUsername () {
        return name;
    }

    public Map<String, Integer> getRecipes () {
        return recipes;
    }

    public boolean isFullfilled () {
        return fullfilled;
    }

    public boolean isPickedUp () {
        return pickedUp;
    }

    public void create ( final String username ) {
        name = username;
        fullfilled = false;
        pickedUp = false;
        creationTime = System.currentTimeMillis();
    }

    public boolean fullfil () {
        if ( fullfilled ) {
            return fullfilled;
        }

        else {
            fullfilled = true;
            return fullfilled;
        }

    }

    public boolean pickup () {
        if ( pickedUp ) {
            return pickedUp;
        }
        else {
            pickedUp = true;
            return pickedUp;
        }

    }

    public long getCreationTime () {
        return creationTime;
    }
}
