package application;

import framework.Autowired;
import framework.FWContext;
import framework.Runnable;
import framework.Service;

@Service
public class Application implements Runnable {

    @Autowired
    OrderService orderService;
    public static void main(String[] args) {
        FWContext.run(Application.class);
    }

    @Override
    public void run() {
        orderService.printOrderDao();
    }
}
