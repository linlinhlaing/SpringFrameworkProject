package application;

import framework.Autowired;
import framework.FWContext;
import framework.Qualifier;
import framework.Service;

public class Application {
	public static void main(String[] args) {
		FWContext fWContext = new FWContext();
		fWContext.start(Application.class);
//		orderService.print();
//		orderDAO.print();
//		orderLineDAO.print();
//		greeting.greet();

	}

}
