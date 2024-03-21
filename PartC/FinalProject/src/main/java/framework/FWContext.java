package framework;


import application.GreetingOne;
import org.reflections.Reflections;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;



public class FWContext {
    private static List<Object> serviceObjectList = new ArrayList<>();
    private Properties properties;
    public void start(Class<?> clazz) {
        try {
            loadProperties(clazz);
            Reflections reflections = new Reflections(clazz.getPackageName());
            scannAndInstatiateServiceClasses(reflections);
            performDI();

            for (Object theClass : serviceObjectList) {
                for(Method method :theClass.getClass().getDeclaredMethods()) {
                    if(!method.isAnnotationPresent(Autowired.class)) {
                        method.invoke(theClass);
                    }
                }
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    void loadProperties(Class<?> clazz) {

        properties = new Properties();
        try (InputStream inputStream = clazz.getClassLoader().getResourceAsStream("application.properties")) {
            if(inputStream != null) {
                properties.load(inputStream);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    private void performDI() {
        try {
            for (Object theServiceClass : serviceObjectList) {
                setterInjection(theServiceClass);
                fieldInjection(theServiceClass);
                fieldInjectionByQualifier(theServiceClass);
                valueInjection(theServiceClass);

            }
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void valueInjection(Object theServiceClass) throws IllegalAccessException {
        for(Field field : theServiceClass.getClass().getDeclaredFields()) {
            if(field.isAnnotationPresent(Value.class)) {
                Annotation[] annotations = field.getAnnotations();
                for(Annotation annotation: annotations) {
                    if(annotation.annotationType().getName().equals(Value.class.getName())) {
                            Value value = (Value) annotation;
                            String defaultValue = value.value();
                            if(defaultValue.startsWith("${") && defaultValue.endsWith("}")) {
                                String propertyName = defaultValue.substring(2, defaultValue.length() - 1);
                                String propertyValue = properties.getProperty(propertyName);
                                field.setAccessible(true);
                                field.set(theServiceClass,propertyValue);
                            }
                            else {
                                field.setAccessible(true);
                                field.set(theServiceClass,defaultValue);
                            }

                    }
                }
            }
        }
    }

    private void constructorInjection(Class<?> theServiceClass) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        Constructor[] constructors = theServiceClass.getConstructors();

        for (Constructor constructor : constructors) {
            if(constructor.isAnnotationPresent(Autowired.class)) {
                Class<?> [] theConstructorParameterType = constructor.getParameterTypes();
                Object instance = getServiceBeanOftype(theConstructorParameterType[0]);
                serviceObjectList.add(constructor.newInstance(instance));
            }
        }

    }

    private void fieldInjectionByQualifier(Object theServiceClass) throws IllegalAccessException, NoSuchFieldException, ClassNotFoundException, InvocationTargetException, InstantiationException, NoSuchMethodException {
        for(Field field:theServiceClass.getClass().getDeclaredFields()) {
        if(field.isAnnotationPresent(Qualifier.class)) {
            Class<?> interfaceClass = field.getType();
            Annotation[] annotations = field.getAnnotations();
            String qualifierValue = "";
            for(Annotation annotation: annotations) {
                if(annotation.annotationType().getName().contentEquals(Qualifier.class.getName())) {
                    qualifierValue = ((Qualifier)annotation).value();
                    }
                for (Object theClass : serviceObjectList) {
                    if(interfaceClass.isAssignableFrom(theClass.getClass())){
                        String concreteClassName = theClass.getClass().getSimpleName().toLowerCase();
                        if(concreteClassName.contentEquals(qualifierValue.toLowerCase())) {
                            Object instance = getServiceBeanOftype(theClass.getClass());
                            field.setAccessible(true);
                            field.set(theServiceClass,instance);
                        }
                    }
                }
                }
            }
        }
    }
    private void setterInjection(Object theServiceClass) throws InvocationTargetException, IllegalAccessException {
        for(Method method : theServiceClass.getClass().getDeclaredMethods()) {
            if(method.isAnnotationPresent(Autowired.class)) {
                Class<?>[] theMethodParameterType = method.getParameterTypes();
                //get the object instance of this type
                Object instance = getServiceBeanOftype(theMethodParameterType[0]);
                //do the injection
                method.invoke(theServiceClass,instance);
            }
        }
    }
    private void fieldInjection(Object theServiceClass) throws IllegalAccessException {
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
            //if constructor shouldn't auto wrie then
            if(shouldCreateInstance(serviceClass)) {
                serviceObjectList.add((Object) serviceClass.getDeclaredConstructor().newInstance());
            }
            //end
        }
        for (Class<?> serviceClass : servicetypes) {
            if(!shouldCreateInstance(serviceClass)) {
                constructorInjection(serviceClass);
            }
        }

    }

    private boolean shouldCreateInstance(Class<?> serviceClass) {

        Constructor[] constructors = serviceClass.getConstructors();
        for (Constructor constructor:constructors) {
            if (constructor.isAnnotationPresent(Autowired.class)) {
                return false;
            }
        }
        return true;
    }
}
