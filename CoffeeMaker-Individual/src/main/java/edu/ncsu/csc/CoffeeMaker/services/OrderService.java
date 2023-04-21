package edu.ncsu.csc.CoffeeMaker.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import edu.ncsu.csc.CoffeeMaker.models.CoffeemakerOrder;
import edu.ncsu.csc.CoffeeMaker.repositories.OrderRepository;

/**
 *
 * The OrderService is used to handle CRUD operations on the Order model.
 *
 * @author finnt
 *
 *
 *
 */
@Component
@Transactional
public class OrderService extends Service<CoffeemakerOrder, Long> {
    /**
     * OrderService, to be autowired in by Spring and provide CRUD operations on
     * Order model.
     */
    @Autowired
    private OrderRepository orderRepository;

    /**
     * Returns the order repository
     *
     * @return Returns the order repository
     */
    @Override
    protected JpaRepository<CoffeemakerOrder, Long> getRepository () {
        return orderRepository;
    }

    /**
     * Find a Order with the provided name
     *
     * @param name
     *            Name of the Order to find
     * @return found Order, null if none
     */
    public CoffeemakerOrder findByName ( final String name ) {
        return orderRepository.findByName( name );
    }

    /**
     * Find a Order with the provided name
     *
     * @param id
     *            Id of the Order to find
     * @return found Order, null if none
     */
    @Override
    public CoffeemakerOrder findById ( final Long id ) {
        Optional<CoffeemakerOrder> optionalOrder = null;
        try {
            optionalOrder = orderRepository.findById( id );
            return optionalOrder.get();
        }
        catch ( final Exception ex ) {
            return null;
        }

    }
}
