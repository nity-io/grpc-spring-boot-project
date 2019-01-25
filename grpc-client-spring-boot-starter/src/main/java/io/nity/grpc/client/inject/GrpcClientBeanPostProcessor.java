/*
 * Copyright 2019 The nity.io gRPC Spring Boot Project Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.nity.grpc.client.inject;

import static java.util.Objects.requireNonNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.List;

import io.nity.grpc.client.channel.factory.GrpcChannelFactory;
import io.nity.grpc.client.channel.factory.GrpcChannelFactoryFacede;
import org.springframework.beans.BeanInstantiationException;
import org.springframework.beans.BeansException;
import org.springframework.beans.InvalidPropertyException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

import com.google.common.collect.Lists;

import io.grpc.Channel;
import io.grpc.ClientInterceptor;
import io.grpc.stub.AbstractStub;

public class GrpcClientBeanPostProcessor implements BeanPostProcessor {

    private final ApplicationContext applicationContext;

    private GrpcChannelFactory channelFactory = null;

    public GrpcClientBeanPostProcessor(final ApplicationContext applicationContext) {
        this.applicationContext = requireNonNull(applicationContext, "applicationContext");
    }

    @Override
    public Object postProcessBeforeInitialization(final Object bean, final String beanName) throws BeansException {
        Class<?> clazz = bean.getClass();
        do {
            for (final Field field : clazz.getDeclaredFields()) {
                GrpcClient annotation = AnnotationUtils.findAnnotation(field, GrpcClient.class);
                if (annotation != null) {
                    ReflectionUtils.makeAccessible(field);
                    Object value = processInjectionPoint(field, field.getType(), annotation);
                    ReflectionUtils.setField(field, bean, value);
                }
            }
            for (final Method method : clazz.getDeclaredMethods()) {
                GrpcClient annotation = AnnotationUtils.findAnnotation(method, GrpcClient.class);
                if (annotation != null) {
                    Class<?>[] paramTypes = method.getParameterTypes();
                    if (paramTypes.length != 1) {
                        throw new BeanDefinitionStoreException("Method " + method + " doesn't have exactly one parameter.");
                    }
                    ReflectionUtils.makeAccessible(method);
                    Object value = processInjectionPoint(method, paramTypes[0], annotation);
                    ReflectionUtils.invokeMethod(method, bean, value);
                }
            }
            clazz = clazz.getSuperclass();
        } while (clazz != null);

        return bean;
    }

    /**
     * Processes the given injection point and computes the appropriate value for the injection.
     *
     * @param <T>             The type of the value to be injected.
     * @param injectionTarget The target of the injection.
     * @param injectionType   The class that will be used to compute injection.
     * @param annotation      The annotation on the target with the metadata for the injection.
     * @return The value to be injected for the given injection point.
     */
    protected <T> T processInjectionPoint(Member injectionTarget, Class<T> injectionType, GrpcClient annotation) {
        final List<ClientInterceptor> interceptors = interceptorsFromAnnotation(annotation);
        final String name = annotation.value();
        final Channel channel = getChannelFactory().createChannel(name, interceptors);

        if (channel == null) {
            throw new IllegalStateException("Channel factory created a null channel");
        }

        final T value = valueForMember(injectionTarget, injectionType, channel);
        if (value == null) {
            throw new IllegalStateException("Injection value is null unexpectedly");
        }
        return value;
    }

    /**
     * Lazy getter for the {@link GrpcChannelFactory}.
     *
     * @return The grpc channel factory to use.
     */
    private GrpcChannelFactory getChannelFactory() {
        if (this.channelFactory == null) {
            final GrpcChannelFactory factory = this.applicationContext.getBean(GrpcChannelFactoryFacede.class);
            this.channelFactory = factory;
        }
        return this.channelFactory;
    }

    protected List<ClientInterceptor> interceptorsFromAnnotation(final GrpcClient annotation) throws BeansException {
        final List<ClientInterceptor> list = Lists.newArrayList();

        for (final Class<? extends ClientInterceptor> interceptorClass : annotation.interceptors()) {
            final ClientInterceptor clientInterceptor;
            if (this.applicationContext.getBeanNamesForType(ClientInterceptor.class).length > 0) {
                clientInterceptor = this.applicationContext.getBean(interceptorClass);
            } else {
                try {
                    clientInterceptor = interceptorClass.getConstructor().newInstance();
                } catch (final Exception e) {
                    throw new BeanCreationException("Failed to create interceptor instance", e);
                }
            }
            list.add(clientInterceptor);
        }

        for (final String interceptorName : annotation.interceptorNames()) {
            list.add(this.applicationContext.getBean(interceptorName, ClientInterceptor.class));
        }

        return list;
    }

    /**
     * Creates the instance to be injected for the given member.
     *
     * @param <T>             The type of the instance to be injected.
     * @param injectionTarget The target member for the injection.
     * @param injectionType   The class that should injected.
     * @param channel         The channel that should be used to create the instance.
     * @return The value that matches the type of the given field.
     * @throws BeansException If the value of the field could not be created or the type of the field is unsupported.
     */
    protected <T> T valueForMember(Member injectionTarget, Class<T> injectionType, final Channel channel) throws BeansException {
        if (Channel.class.equals(injectionType)) {
            return injectionType.cast(channel);
        }

        if (AbstractStub.class.isAssignableFrom(injectionType)) {
            try {
                final Class<? extends AbstractStub<?>> stubClass = (Class<? extends AbstractStub<?>>) injectionType.asSubclass(AbstractStub.class);
                final Constructor<? extends AbstractStub<?>> constructor = ReflectionUtils.accessibleConstructor(stubClass, Channel.class);
                AbstractStub<?> stub = constructor.newInstance(channel);
                return injectionType.cast(stub);
            } catch (final NoSuchMethodException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                throw new BeanInstantiationException(injectionType, "Failed to create gRPC client for : " + injectionTarget, e);
            }
        }

        throw new InvalidPropertyException(injectionTarget.getDeclaringClass(), injectionTarget.getName(), "Unsupported type " + injectionType.getName());
    }

}