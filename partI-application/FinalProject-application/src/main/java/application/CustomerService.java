package application;

import framework.ApplicationEventPublisher;
import framework.Autowired;
import framework.Service;

import java.lang.reflect.InvocationTargetException;
@Service
public class CustomerService {
    @Autowired
    private ApplicationEventPublisher publisher;
    public void addCustomer() throws InvocationTargetException, IllegalAccessException {
        publisher.publishEvent(new AddCustomerEvent("New customer is added"));

    }
}
