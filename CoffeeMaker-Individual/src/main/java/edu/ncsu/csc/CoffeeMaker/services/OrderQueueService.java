package edu.ncsu.csc.CoffeeMaker.services;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import edu.ncsu.csc.CoffeeMaker.models.CoffeemakerOrder;
import edu.ncsu.csc.CoffeeMaker.models.CoffeemakerOrderQueue;
import edu.ncsu.csc.CoffeeMaker.repositories.OrderQueueRepository;

/**
 * The OrderService is used to handle CRUD operations on the Order model. In
 * addition to all functionality in `Service`, we also manage the Order
 * singleton.
 *
 * @author finnt
 *
 */
@Component
@Transactional
public class OrderQueueService extends Service<CoffeemakerOrderQueue, Long> {

    /**
     * OrderRepository, to be autowired in by Spring and provide CRUD operations
     * on Inventory model.
     */
    @Autowired
    private OrderQueueRepository orderQueueRepository;

    @Override
    protected JpaRepository<CoffeemakerOrderQueue, Long> getRepository () {
        return orderQueueRepository;
    }

    /**
     * Retrieves the singleton Order instance from the database, creating it if
     * it does not exist.
     *
     * @return the Order, either new or fetched
     */
    public synchronized CoffeemakerOrderQueue getOrderQueue () {
        final List<CoffeemakerOrderQueue> queueList = findAll();
        if ( queueList != null && queueList.size() == 1 ) {
            return queueList.get( 0 );
        }
        else {
            // initialize the inventory with 0 of everything
            final CoffeemakerOrderQueue q = new CoffeemakerOrderQueue( new ArrayList<CoffeemakerOrder>() );
            save( q );
            return q;
        }
    }

}
