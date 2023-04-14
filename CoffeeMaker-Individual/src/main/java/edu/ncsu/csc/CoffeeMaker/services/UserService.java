package edu.ncsu.csc.CoffeeMaker.services;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import edu.ncsu.csc.CoffeeMaker.models.CoffeemakerUser;
import edu.ncsu.csc.CoffeeMaker.repositories.UserRepository;

/**
 * The User Service is used to handle CRUD operations on the user models. In
 * addition to all functionality from `Service`, we also have functionality for
 * retrieving a single User by name.
 *
 * @author Finn Bacheldor
 *
 */
@Component
@Transactional
public class UserService extends Service<CoffeemakerUser, Long> {

    /**
     * UserRepository, to be autowired in by Spring and provide CRUD operations
     * on User model(s).
     */
    @Autowired
    private UserRepository userRepository;

    @Override
    protected JpaRepository<CoffeemakerUser, Long> getRepository () {
        return userRepository;
    }

    /**
     * Find a user with the provided name
     *
     * @param name
     *            Name of the user to find
     * @return found user, null if none
     */
    public CoffeemakerUser findByName ( final String name ) {
        return userRepository.findByName( name );
    }

}
