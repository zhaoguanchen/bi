package com.yiche.bigdata.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class EnumUtils {

    public static Map valueMap = new HashMap<>();
    /**
     * 根据valee获取
     * @param <T>
     * @param clazz
     * @param value
     * @return
     */
    public static <T extends Enum<T>> T valueOf(Class<T> clazz, Object value) {
        if(valueMap.get(value + clazz.getName()) == null){
            Object[] enums = clazz.getEnumConstants();
            for (Object object : enums) {
                try {
                    Method method = clazz.getMethod("value");
                    valueMap.put(method.invoke(object, null) + clazz.getName() , object);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
        return (T) valueMap.get(value + clazz.getName());
    }

    /**
     * 根据value获取
     * @param <T>
     * @param clazz
     * @param name
     * @return
     */
    public static <T extends Enum<T>> T nameOf(Class<T> clazz, String name) {
        if(valueMap.get(name + clazz.getName()) == null){
            Object[] enums = clazz.getEnumConstants();
            for (Object object : enums) {
                try {
                    Method method = clazz.getMethod("getName");
                    valueMap.put(method.invoke(object, null) + clazz.getName() , object);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
        return (T) valueMap.get(name + clazz.getName());
    }

}
