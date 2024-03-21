package application;

import framework.Autowired;
import framework.Service;
import framework.Value;

@Service
public class OrderDAO {

    @Value("smtp.server")
    private String outgoingMailServer;

    @Value("${hello}")
    private String hello;
    public void print() {
        System.out.println("orderDAO " + hello + " "+outgoingMailServer);
    }
}
