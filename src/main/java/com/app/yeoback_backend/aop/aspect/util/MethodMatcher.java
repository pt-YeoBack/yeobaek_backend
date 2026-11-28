package com.app.yeoback_backend.aop.aspect.util;

import java.lang.reflect.Method;

public interface MethodMatcher {
    boolean matches(Method method);
}
