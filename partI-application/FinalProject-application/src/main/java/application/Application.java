package application;

import framework.Autowired;
import framework.FWContext;
import framework.Runnable;
import framework.Service;

import java.lang.reflect.InvocationTargetException;

@Service
public class Application implements Runnable {

	@Autowired
	OrderService orderService;

	@Autowired
	CustomerService customerService;

	public static void main(String[] args) {
		FWContext.run(Application.class);
	}

	@Override
	public void run() {

		orderService.printOrderDao();
		orderService.printGreeting();

		try {
			customerService.addCustomer();
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}

	}
}
