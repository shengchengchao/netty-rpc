package com.xixi.netty.rpc.server;

import com.google.common.collect.Lists;
import com.xixi.netty.rpc.common.utils.ApplicationContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author shengchengchao
 * @Description
 * @createTime 2021/10/4
 */
@Slf4j
public class BestMethodMatch implements MethodMatch {

    private static BestMethodMatch instance;

    public static synchronized BestMethodMatch getInstance() {
        if (instance == null) {
            instance = new BestMethodMatch();
        }
        return instance;
    }

    private final ConcurrentHashMap<MethodMatchInput, MethodMatchOut> map = new ConcurrentHashMap<>();

    @Override
    public MethodMatchOut selectBestMatchMethod(MethodMatchInput input) {
        if (map.containsKey(input)) {
            return map.get(input);
        }
        MethodMatchOut methodMatchOut = new MethodMatchOut();
        try {
            String interfaceName = input.getInterfaceName();
            String methodName = input.getMethodName();
            //找到需要调用的类
            Class<?> interfaceClass = Class.forName(interfaceName);
            ClassMethodInfo classMethodInfo = getClassInfo(interfaceClass);
            List<Method> targetMethods = Lists.newArrayList();
            List<String> methodArgumentSignatures = input.getMethodArgumentSignatures();
            //根据调用的类得到对应的方法
            ReflectionUtils.doWithMethods(classMethodInfo.getUserClass(), targetMethods::add, method -> {
                String name = method.getName();
                Class<?> declaringClass = method.getDeclaringClass();

                List<Class<?>> list = Lists.newArrayList();
                Optional.ofNullable(methodArgumentSignatures).orElse(Lists.newArrayList())
                        .forEach(x -> list.add(ClassUtils.resolveClassName(x, null)));
                if (!CollectionUtils.isEmpty(methodArgumentSignatures)) {
                    List<Class<?>> parameters = Lists.newArrayList(method.getParameterTypes());
                    // 比较方法名，调用类，以及参数类型
                    return Objects.equals(name, methodName)
                            && Objects.equals(classMethodInfo.getUserClass(), declaringClass)
                            && Objects.equals(parameters, list);
                }

                //参数类型不传入的情况下 ，判断参数长度
                if (Optional.ofNullable(input.getMethodArgsLength()).orElse(0) > 0) {
                    int parameterCount = method.getParameterCount();
                    return Objects.equals(name, methodName)
                            && Objects.equals(classMethodInfo.getUserClass(), declaringClass)
                            && Objects.equals(input.getMethodArgsLength().intValue(), parameterCount);
                }
                //都没有的情况下 判断方法名与类型
                return Objects.equals(name, methodName)
                        && Objects.equals(classMethodInfo.getUserClass(), declaringClass);
            });

            if (CollectionUtils.isEmpty(targetMethods) || targetMethods.size() > 1) {
                log.error(" BestMethodMatch.selectBestMatchMethod :发生异常: 查找到目标方法数量不等于1 interface: {},mathod{}", input.getInterfaceName(), input.getMethodName());
                return null;
            }
            methodMatchOut.setTarget(classMethodInfo.getTarget());
            methodMatchOut.setTargetClass(classMethodInfo.getMethodClass());
            methodMatchOut.setTargetUserClass(classMethodInfo.getUserClass());
            methodMatchOut.setTargetMethod(targetMethods.get(0));
            map.put(input, methodMatchOut);
            return methodMatchOut;
        } catch (ClassNotFoundException e) {
            log.error(" BestMethodMatch.selectBestMatchMethod :发生异常", e);
            return null;
        }

    }

    private ClassMethodInfo getClassInfo(Class<?> interfaceClass) {
        Object bean = ApplicationContextUtil.getBeanByClass(interfaceClass);
        if (bean == null) {
            log.error(" BestMethodMatch.getClassInfo :发生异常");
            return null;
        }
        Class<?> aClass = bean.getClass();
        Class<?> userClass = ClassUtils.getUserClass(aClass);
        ClassMethodInfo classMethodInfo = new ClassMethodInfo();
        classMethodInfo.setMethodClass(aClass);
        classMethodInfo.setTarget(bean);
        classMethodInfo.setUserClass(userClass);
        return classMethodInfo;
    }
}
