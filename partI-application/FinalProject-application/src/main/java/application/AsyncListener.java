package application;

import framework.Async;
import framework.EnableAsync;
import framework.EventListener;
import framework.Service;

@Service
@EnableAsync
public class AsyncListener {
    @Async
    @EventListener
    public void onEvent(AddCustomerEvent event) {
        System.out.println("====async");
        System.out.println("received event asynchronous:" + event.getMessage());;
    }
}
