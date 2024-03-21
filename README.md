## Custom Spring Framework

### Part A
Implemented a lightweight version of the Spring Framework capable of instantiating classes annotated with `@Service` and applying dependency injection to fields annotated with `@Autowired`. Included an example application demonstrating the framework's functionality by retrieving a bean from the context and invoking a method on it.

### Part B
Enhanced the framework to support field injection by name using the `@Qualifier` annotation, setter injection by placing `@Autowired` on the setter method, and constructor injection by placing `@Autowired` on the constructor. Demonstrated the functionality in an application that uses the framework.

### Part C
Extended the framework to support value injection using the `@Value` annotation on a field, with values specified in the `application.properties` file. Showcased functionality in an application.

### Part D
Restructured the framework to resemble Spring Boot's Application class with a main method, packaged it into a separate JAR file, and demonstrated its usage in an application that includes the framework JAR as a dependency.

### Part E
Augmented the framework to support profiles similar to Spring Boot, showcasing its functionality in an application.

### Part F
Implemented support for simple scheduling using the `@Scheduled` annotation with the help of `java.util.Timer`. Demonstrated scheduling functionality in an application.

### Part G
Expanded scheduling support to include cron expressions in the `@Scheduled` annotation. Showcased correct functionality for various cron expressions in an application.

### Part H
Enhanced the framework to support events (publish-subscribe) similar to Spring Boot. Demonstrated event handling in an application.

### Part I
Updated the framework to support asynchronous methods using the `@Async` annotation and `CompletableFuture.runAsync()`. Showed correct functioning in an application.
