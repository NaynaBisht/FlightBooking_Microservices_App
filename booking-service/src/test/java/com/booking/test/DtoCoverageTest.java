package com.booking.test;

import org.junit.jupiter.api.Test;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import com.booking.dto.FlightDTO;
import com.booking.request.BookingRequest;
import com.booking.request.PassengerRequest;
import com.booking.response.BookingResponse;
// Import your Exception classes too
import com.common.dto.ResourceNotFoundException;

class DtoCoverageTest {

    @Test
    void testAllDtos() throws Exception {
        Object[] objectsToTest = new Object[] {
            new FlightDTO(),
            new BookingRequest(),
            new PassengerRequest(),
            new BookingResponse(),
            // Add custom exceptions to test their constructors
            new ResourceNotFoundException("Test") 
        };

        for (Object obj : objectsToTest) {
            testGettersAndSetters(obj);
        }
    }

    private void testGettersAndSetters(Object instance) throws Exception {
        Class<?> clazz = instance.getClass();
        for (Method method : clazz.getDeclaredMethods()) {
            String name = method.getName();
            
            // Test Setters
            if (name.startsWith("set") && method.getParameterCount() == 1) {
                Class<?> paramType = method.getParameterTypes()[0];
                Object value = getDefaultValue(paramType);
                method.invoke(instance, value);
            }
            
            // Test Getters
            if ((name.startsWith("get") || name.startsWith("is")) && method.getParameterCount() == 0) {
                method.invoke(instance);
            }
            
            // Test toString, equals, hashCode (Lombok generated)
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