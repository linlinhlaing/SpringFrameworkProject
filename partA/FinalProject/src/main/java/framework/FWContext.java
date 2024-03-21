package framework;

import org.reflections.Reflections;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;



public class FWContext {
    private static List<Object> serviceObjectList = new ArrayList<>();
    public void start(Class<?> clazz) {
        try {
            Reflections reflections = new Reflections(clazz.getPackageName());
            scannAndInstatiateServiceClasses(reflections);
            performDI();
            for (Object theClass : serviceObjectList) {
                for(Method method :theClass.getClass().getDeclaredMethods()) {
                    method.invoke(theClass);
                }
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void performDI() {
        try {
            for (Object theServiceClass : serviceObjectList) {
                // find annotated fields
                for(Field field : theServiceClass.getClass().getDeclaredFields()) {
                    if(field.isAnnotationPresent(Autowired.class)) {
                        // get the type of the field
                        Class<?> theFieldType =field.getType();
                        //get the object instance of this type
                        Object instance = getServiceBeanOftype(theFieldType);
                        //do the injection
                        field.setAccessible(true);
                        field.set(theServiceClass, instance);
                    }
                }
            }
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private Object getServiceBeanOftype(Class<?> theFieldClass) {
        Object service = null;
        try{
            for(Object theClass:serviceObjectList) {
                if(theClass.getClass().getName().contentEquals(theFieldClass.getName())) {
                    service = theClass;
                }
                else {
                    Class<?>[] interfaces = theClass.getClass().getInterfaces();
                    for (Class<?> theInterface : interfaces) {
                        if (theInterface.getName().contentEquals(theFieldClass.getName()))
                            service = theClass;
                }
                }
            }

        }catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return service;

    }

    private void scannAndInstatiateServiceClasses(Reflections reflections) throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Set<Class<?>> servicetypes =reflections.getTypesAnnotatedWith(Service.class);
        // find and instantiate all classes annotated with the @Service annotation
        for (Class<?> serviceClass : servicetypes) {
            serviceObjectList.add((Object) serviceClass.getDeclaredConstructor().newInstance());
        }

    }
}
