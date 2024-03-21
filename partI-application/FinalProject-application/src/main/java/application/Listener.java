package application;

import framework.EventListener;
import framework.Service;

@Service
public class Listener {
    @EventListener
    public void onEvent(AddCustomerEvent event) {
        System.out.println("received event :" + event.getMessage());;
    }
}
