package application;

import framework.Profile;
import framework.Service;

@Service
@Profile("One")
public class GreetingOne implements Greeting{
    @Override
    public void greet() {
        System.out.println("Greeting One !!");
    }
}
