package framework;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.lang.Runnable;

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

                    if(method.isAnnotationPresent(Async.class) && theClass.getName().contentEquals(event.getClass().getName())) {
                        CompletableFuture.runAsync(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    method.invoke(serviceObject, event);
                                } catch (IllegalAccessException e) {
                                    throw new RuntimeException(e);
                                } catch (InvocationTargetException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        });
                    }
                    else if (method.isAnnotationPresent(EventListener.class) && theClass.getName().contentEquals(event.getClass().getName())) {
                        method.invoke(serviceObject, event);
                    }

                }
            }
        }

    }

}
