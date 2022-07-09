/*
 * Copyright 2013-2022 the HotswapAgent authors.
 *
 * This file is part of HotswapAgent.
 *
 * HotswapAgent is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 2 of the License, or (at your
 * option) any later version.
 *
 * HotswapAgent is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with HotswapAgent. If not, see http://www.gnu.org/licenses/.
 */
package org.hotswap.agent.annotation.handler;

import org.hotswap.agent.annotation.*;
import org.hotswap.agent.config.PluginManager;
import org.hotswap.agent.logging.AgentLogger;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

/**
 * Process annotations on a plugin, register appropriate handlers.
 *
 * @author Jiri Bubnik
 */
public class AnnotationProcessor {
    private static AgentLogger LOGGER = AgentLogger.getLogger(AnnotationProcessor.class);

    protected PluginManager pluginManager;

    public AnnotationProcessor(PluginManager pluginManager) {
        this.pluginManager = pluginManager;
        init(pluginManager);
    }

    protected Map<Class<? extends Annotation>, PluginHandler> handlers =
            new HashMap<Class<? extends Annotation>, PluginHandler>();

    public void init(PluginManager pluginManager) {
        // 这里非常重要，各handler负责执行对应注解的初始化操作，比如
        // 在filed上加@Init注解，那么InitHandler会将对应的示例注入进去，有点像spring的@Autowired
        addAnnotationHandler(Init.class, new InitHandler(pluginManager));
        // OnClassLoadedHandler会负责将Plugin插件注册到HotswapTransformer上，这样在类加载时，plugin中被OnClassLoadedHandler注解的方法就会被回调，来执行一些个性化增强策略
        addAnnotationHandler(OnClassLoadEvent.class, new OnClassLoadedHandler(pluginManager));
        // OnClassFileEvent关注的是class文件的变化，因此WatchHandler会把classPath路径的注册到观测路径上去，当文件发生变化时会回调被这个注解标注的方法
        addAnnotationHandler(OnClassFileEvent.class, new WatchHandler(pluginManager));
        // OnResourceFileEvent关注的是资源文件的变化，比如mybatis的mapper.xml文件，也是通过WatchHandler来注册监听时间，方便文件有变化时进行回调
        addAnnotationHandler(OnResourceFileEvent.class, new WatchHandler(pluginManager));
    }

    public void addAnnotationHandler(Class<? extends Annotation> annotation, PluginHandler handler) {
        handlers.put(annotation, handler);
    }

    /**
     * Process annotations on the plugin class - only static methods, methods to hook plugin initialization.
     *
     * @param processClass class to process annotation
     * @param pluginClass main plugin class (annotated with @Plugin)
     * @return true if success
     */
    public boolean processAnnotations(Class processClass, Class pluginClass) {

        try {
            // 可以看到这里都是处理的static的成员变量和方法
            for (Field field : processClass.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers()))
                    if (!processFieldAnnotations(null, field, pluginClass))
                        return false;

            }

            for (Method method : processClass.getDeclaredMethods()) {
                if (Modifier.isStatic(method.getModifiers()))
                    if (!processMethodAnnotations(null, method, pluginClass))
                        return false;
            }

            // process annotations on all supporting classes in addition to the plugin itself
            for (Annotation annotation : processClass.getDeclaredAnnotations()) {
                if (annotation instanceof Plugin) {
                    for (Class supportClass : ((Plugin) annotation).supportClass()) {
                        processAnnotations(supportClass, pluginClass);
                    }
                }
            }

            // 如果有一些非static的成员变量和方法需要初始化，记得要在static方法里手动来调用org.hotswap.agent.util.PluginManagerInvoker.callInitializePlugin
            // 可以看到最终会走到下面的processAnnotations(Object plugin)，进行非static部分的初始化
            // HotSwapperPlugin就做了这个事情(有一个非static方法被OnClassFileEvent标记，通过上边的方式将其注册到WatchService上)

            return true;
        } catch (Throwable e) {
            LOGGER.error("Unable to process plugin annotations '{}'", e, pluginClass);
            return false;
        }
    }

    /**
     * Process annotations on a plugin - non static fields and methods.
     *
     * @param plugin plugin object
     * @return true if success
     */
    public boolean processAnnotations(Object plugin) {
        LOGGER.debug("Processing annotations for plugin '" + plugin + "'.");

        Class pluginClass = plugin.getClass();

        for (Field field : pluginClass.getDeclaredFields()) {
            if (!Modifier.isStatic(field.getModifiers()))
                if (!processFieldAnnotations(plugin, field, pluginClass))
                    return false;

        }

        for (Method method : pluginClass.getDeclaredMethods()) {
            if (!Modifier.isStatic(method.getModifiers()))
                if (!processMethodAnnotations(plugin, method, pluginClass))
                    return false;

        }

        return true;
    }

    @SuppressWarnings("unchecked")
    private boolean processFieldAnnotations(Object plugin, Field field, Class pluginClass) {
        // for all fields and all handlers
        for (Annotation annotation : field.getDeclaredAnnotations()) {
            for (Class<? extends Annotation> handlerAnnotation : handlers.keySet()) {
                if (annotation.annotationType().equals(handlerAnnotation)) {
                    // initialize
                    PluginAnnotation<?> pluginAnnotation = new PluginAnnotation<>(pluginClass, plugin, annotation, field);
                    if (!handlers.get(handlerAnnotation).initField(pluginAnnotation)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    private boolean processMethodAnnotations(Object plugin, Method method, Class pluginClass) {
        // for all methods and all handlers
        for (Annotation annotation : method.getDeclaredAnnotations()) {
            for (Class<? extends Annotation> handlerAnnotation : handlers.keySet()) {
                if (annotation.annotationType().equals(handlerAnnotation)) {
                    // initialize
                    PluginAnnotation<?> pluginAnnotation = new PluginAnnotation<>(pluginClass, plugin, annotation, method);
                    if (!handlers.get(handlerAnnotation).initMethod(pluginAnnotation)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
