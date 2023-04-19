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

    public String getUser () {
        return name;
    }

    public Map<String, Integer> getRecipes () {
        return recipes;
    }

    public void create ( final String username ) {
        name = username;
        fullfilled = false;
        pickedUp = false;
        creationTime = System.currentTimeMillis();
    }

    public boolean fullfil () {
        if ( fullfilled ) {
            return false;
        }
        else {
            return fullfilled = true;
        }

    }

    public boolean pickup () {
        if ( pickedUp ) {
            return false;
        }
        else {
            return pickedUp = true;
        }

    }
}
