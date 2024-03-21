package application;

import framework.Autowired;
import framework.FWContext;
import framework.Service;
import framework.Runnable;



@Service
public class Application implements Runnable{

	@Autowired
	OrderService orderService;
	public static void main(String[] args) {
		FWContext.run(Application.class);
	}

	@Override
	public void run() {

		orderService.printOrderDao();
		orderService.printGreeting();
	}
}
