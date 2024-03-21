package application;

import framework.Autowired;
import framework.Qualifier;
import framework.Scheduled;
import framework.Service;

@Service
public class OrderService {

    //setterInjection
    static OrderLineDAO orderLineDAO;
    @Autowired
    public void setOrderLine(OrderLineDAO orderLineDAO) {
        this.orderLineDAO = orderLineDAO;
    }
    @Autowired
    @Qualifier(value = "greetingOne")
    static Greeting greeting;

    OrderDAO orderDAO;
    @Autowired
    public OrderService(OrderDAO orderDAO) {
        this.orderDAO = orderDAO;
    }
    public void printOrderservice() {
        System.out.println("orderService");
    }
    @Scheduled(fixedRate = 5000)
    public void printOrderLine() {
        orderLineDAO.print();
    }

//    @Scheduled(cron = "5 0")
    public void printGreeting() {
        System.out.println("cron ");
        System.out.println("greeting from orderservice");
        greeting.greet();
    }
    public void printOrderDao() {
        System.out.println("this is orderdao from orderdao");
        orderDAO.print();
    }


}
