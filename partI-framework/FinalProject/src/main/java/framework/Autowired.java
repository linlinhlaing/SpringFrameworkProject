package framework;

import java.lang.annotation.*;
import java.lang.reflect.Constructor;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target({FIELD,METHOD, CONSTRUCTOR})
public @interface Autowired {
}
