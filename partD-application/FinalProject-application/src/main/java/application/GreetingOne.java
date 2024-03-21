package application;

import framework.Service;

@Service
public class GreetingOne implements Greeting{
    @Override
    public void greet() {
        System.out.println("application.Greeting One !!");
    }
}
