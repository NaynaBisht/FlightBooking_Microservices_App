package com.flight.test;

import org.junit.jupiter.api.Test;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import com.flight.entity.Flight;
import com.flight.request.AddFlightRequest;
import com.flight.request.FlightSearchRequest;
import com.flight.request.PassengerCount;
import com.flight.response.FlightSearchResponse;
import com.flight.response.FlightSearchResponse.FlightInfo;

class FlightDtoCoverageTest {

    @Test
    void testAllDtos() throws Exception {
        Object[] objectsToTest = new Object[] {
            new Flight(),
            // FlightDTO removed
            new AddFlightRequest(),
            new FlightSearchRequest(),
            new PassengerCount(),
            new FlightSearchResponse(),
            new FlightInfo()
        };

        for (Object obj : objectsToTest) {
            testGettersAndSetters(obj);
        }
    }

    private void testGettersAndSetters(Object instance) throws Exception {
        Class<?> clazz = instance.getClass();
        for (Method method : clazz.getDeclaredMethods()) {
            String name = method.getName();
            
            if (name.startsWith("set") && method.getParameterCount() == 1) {
                Class<?> paramType = method.getParameterTypes()[0];
                Object value = getDefaultValue(paramType);
                method.invoke(instance, value);
            }
            
            if ((name.startsWith("get") || name.startsWith("is")) && method.getParameterCount() == 0) {
                method.invoke(instance);
            }
            
            if (name.equals("toString")) method.invoke(instance);
            if (name.equals("hashCode")) method.invoke(instance);
            if (name.equals("equals")) method.invoke(instance, instance);
        }
    }

    private Object getDefaultValue(Class<?> type) {
        if (type == String.class) return "test";
        if (type == int.class || type == Integer.class) return 1;
        if (type == float.class || type == Float.class) return 1.0f;
        if (type == double.class || type == Double.class) return 1.0d;
        if (type == boolean.class || type == Boolean.class) return true;
        if (type == List.class) return Collections.emptyList();
        if (type == LocalDateTime.class) return LocalDateTime.now();
        if (type == LocalDate.class) return LocalDate.now();
        return null;
    }
}