package framework;

import framework.EventListener;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ApplicationEventPublisher {
    private List<Object> serviceObjectList;

    public void setServiceObjectList(List<Object> serviceObjectList) {
        this.serviceObjectList = serviceObjectList;
    }

    public void publishEvent(Object event) throws InvocationTargetException, IllegalAccessException {
        for(Object serviceObject : serviceObjectList) {
            Method[] methods = serviceObject.getClass().getDeclaredMethods();
            for(Method method : methods) {
                Class<?>[] parameterTypes = method.getParameterTypes();
                if (parameterTypes.length == 1) {
                    Class theClass = parameterTypes[0];
                    if (method.isAnnotationPresent(EventListener.class) && theClass.getName().contentEquals(event.getClass().getName())) {
                        method.invoke(serviceObject, event);
                    }
                }
            }
        }

    }

}
