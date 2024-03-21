package framework;

import org.reflections.Reflections;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;


public class FWContext {
    private static List<Object> serviceObjectList = new ArrayList<>();
    private Properties properties;

    public static void run(Class<?> clazz) {
        FWContext fWContext = new FWContext();
		fWContext.start(clazz);
    }
    public void start(Class<?> clazz) {
        try {
            ApplicationEventPublisher publisher = new ApplicationEventPublisher();
            serviceObjectList.add(publisher);
            loadProperties(clazz);
            Reflections reflections = new Reflections(clazz.getPackageName());
            scannAndInstatiateServiceClasses(reflections);
            performDI();

            publisher.setServiceObjectList(serviceObjectList);
            runabble();
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void runabble() {
        for(Object theClass:serviceObjectList) {
            Class<?>[] interfaces = theClass.getClass().getInterfaces();
            for(Class<?> interfaceClass :interfaces) {
                if(interfaceClass.getSimpleName().toLowerCase().contentEquals(Runnable.class.getSimpleName().toLowerCase())) {
                   Runnable runnable = (Runnable) theClass;
                   runnable.run();
                }
            }
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
                scheduleInjection(theServiceClass);
            }
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void scheduleInjection(Object theServiceClass) {
        for(Method method : theServiceClass.getClass().getDeclaredMethods()) {
            if(method.isAnnotationPresent(Scheduled.class)) {
                Annotation[] annotations = method.getAnnotations();
                for(Annotation annotation:annotations) {
                    if(annotation.annotationType().getName().contentEquals(Scheduled.class.getName())) {
                        if(((Scheduled) annotation).cron().isEmpty()) {
                            int period = ((Scheduled) annotation).fixedRate();
                            executeScheduleTask(theServiceClass,method,period);
                        }
                        else {
                            String[] timeData = ((Scheduled) annotation).cron().split(" ");
                            if(timeData.length == 2) {
                                try {
                                    int second = Integer.parseInt(timeData[0]);
                                    int minute = Integer.parseInt(timeData[1]);
                                    if(second < 60 && minute < 60) {
                                        int totalSecond = second + (minute * 60);
                                        int peroid = totalSecond * 1000;
                                        executeScheduleTask(theServiceClass,method,peroid);
                                    }

                                }catch (Exception e){

                                }

                            }

                        }

                    }
                }
            }
        }
    }
    private void executeScheduleTask(Object theServiceClass,Method method , int peroid) {
        Timer timer = new Timer();
        TimerTask task = getTimerTask(theServiceClass, method);
        timer.schedule(task,0,peroid);
    }

    private static TimerTask getTimerTask(Object serviceObject, Method method) {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                try {
                    method.invoke(serviceObject);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        return task;
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
                if(shouldCreateProfileInstance(serviceClass)) {
                    serviceObjectList.add((Object) serviceClass.getDeclaredConstructor().newInstance());
                }
                
            }
 
            //end
        }
        for (Class<?> serviceClass : servicetypes) {
            if(!shouldCreateInstance(serviceClass)) {
                constructorInjection(serviceClass);
            }
        }

    }

    private boolean shouldCreateProfileInstance(Class<?> serviceClass) {
        String profileValue = "";
        if(!serviceClass.isAnnotationPresent(Profile.class)) {
            return true;
        }
        Annotation[] annotations = serviceClass.getAnnotations();
        for(Annotation annotation : annotations) {
            if(annotation.annotationType().getName().contentEquals(Profile.class.getName())) {
                profileValue = ((Profile) annotation).value();
                if(profileValue.contentEquals(properties.getProperty("application.profile.active"))) {
                    return true;
                }
            }
        }
        return false;
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
