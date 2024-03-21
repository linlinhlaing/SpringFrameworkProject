package application;

import framework.Service;

@Service
public class GreetingTwo implements Greeting{

    @Override
    public void greet() {
        System.out.println("application.Greeting Two");
    }
}
