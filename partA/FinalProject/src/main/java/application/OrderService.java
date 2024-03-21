package application;

import framework.Autowired;
import framework.Service;

@Service
public class OrderService {
    @Autowired
    OrderDAO orderDAO;
    public void showOrder() {
        orderDAO.print();
    }
}
