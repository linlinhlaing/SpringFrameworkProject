package application;

import framework.Profile;
import framework.Service;

@Service
@Profile("Two")
public class GreetingTwo implements Greeting{

    @Override
    public void greet() {
        System.out.println("Greeting Two !!");
    }
}
