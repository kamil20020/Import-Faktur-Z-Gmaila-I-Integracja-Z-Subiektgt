package org.example;

import sun.misc.Unsafe;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;

public interface TestUtils {

    static void updatePrivateStaticField(Class<?> type, String fieldName, Object newValue) {

        try {
            Field authHeaderField = type.getDeclaredField(fieldName);
            authHeaderField.setAccessible(true);

            Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
            unsafeField.setAccessible(true);
            Unsafe unsafe = (Unsafe) unsafeField.get(null);

            Object staticFieldBase = unsafe.staticFieldBase(authHeaderField);
            long staticFieldOffset = unsafe.staticFieldOffset(authHeaderField);

            unsafe.putObject(staticFieldBase, staticFieldOffset, newValue);
        }
        catch (Exception e) {

            e.printStackTrace();
        }
    }

    static<T> void updatePrivateInstanceField(Class<T> type, String fieldName, T instance, Object newValue){

        try {
            Field authHeaderField = type.getDeclaredField(fieldName);
            authHeaderField.setAccessible(true);

            authHeaderField.set(instance, newValue);

        }
        catch (Exception e){

            e.printStackTrace();
        }
    }

    static<T> T getPrivateStaticField(Class<?> type, String fieldName, Class<T> returnType){

        return getPrivateInstanceField(type, fieldName, null, returnType);
    }

    static<T, K> K getPrivateInstanceField(Class<T> type, String fieldName, T instance, Class<K> returnType){

        try {
            Field authHeaderField = type.getDeclaredField(fieldName);
            authHeaderField.setAccessible(true);

            Object gotValue = authHeaderField.get(instance);

            return returnType.cast(gotValue);

        }
        catch (Exception e){

            e.printStackTrace();
        }

        return null;
    }

    static String removeWhiteSpace(String value){

        if(value == null){
            return null;
        }

        return value.replaceAll("\\s", "");
    }

}
