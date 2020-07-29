/*
 *  Copyright 2019 Qameta Software OÜ
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package io.qameta.allure.junit5assert;

import io.qameta.allure.Allure;
import io.qameta.allure.AllureLifecycle;
import io.qameta.allure.model.Status;
import io.qameta.allure.model.StepResult;
import io.qameta.allure.util.ObjectUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static io.qameta.allure.util.ResultsUtils.getStatus;

/**
 * @author legionivo (Andrey Konovka).
 */
@SuppressWarnings("all")
@Aspect
public class AllureJunit5Assert2 {

    private static final Logger LOGGER = LoggerFactory.getLogger(AllureJunit5Assert2.class);
    private StepResult stepResult;


    private static InheritableThreadLocal<AllureLifecycle> lifecycle = new InheritableThreadLocal<AllureLifecycle>() {
        @Override
        protected AllureLifecycle initialValue() {
            return Allure.getLifecycle();
        }
    };

    @Pointcut("call(void org.junit.jupiter.api.Assertions.*(..)) || throwable()")
    public void anyAssert() {

    }

    @Pointcut("call(Throwable org.junit.jupiter.api.Assertions.*(..))")
    public void throwable() {

    }

    @Before("anyAssert()")
    public void stepStart(final JoinPoint joinPoint) {
        final Object[] args = joinPoint.getArgs();
        if (args.length > 0) {
            final String uuid = UUID.randomUUID().toString();
            final String assertName = joinPoint.getSignature().getName();
            //System.out.println(assertName);
            String name = "";
            List<String> onlyActual = Arrays.asList("assertTrue", "assertFalse", "assertNull", "assertNotNull");
            if (onlyActual.stream().anyMatch(s -> s.equalsIgnoreCase(assertName))) {
                final String actual = ObjectUtils.toString(args[0]);
                name = String.format(assertName + " \'%s\'", actual);
                if (args.length > 1) {
                    final String message = ObjectUtils.toString(args[1]);
                    name = String.format(name + " [%s]", message);
                }
            } else if (assertName.equalsIgnoreCase("assertAll")) {
                name = "assertAll";
                if (args.length > 1)
                    name = String.format(name + " in " + "\'%s\'", args[0].toString());
            } else if (args.length > 1) {
                final String expected = ObjectUtils.toString(args[0]);
                final String actual = ObjectUtils.toString(args[1]);
                final List<String> assertArray = Arrays.asList(assertName.split("(?=[A-Z])"));
                name = String.format(assertArray.get(0) + " \'%s\'", expected)
                        + " " + String.format(assertArray.stream().skip(1)
                        .collect(Collectors.joining("")) + " \'%s\'", actual);
                if (args.length > 2) {
                    final String message = ObjectUtils.toString(args[2]);
                    name = String.format(name + " [%s]", message);
                }
            }
            final StepResult result = new StepResult()
                    .setName(name)
                    .setStatus(Status.PASSED);
            getLifecycle().startStep(uuid, result);
        }
    }

    @AfterThrowing(pointcut = "anyAssert()", throwing = "e")
    public void stepFailed(final Throwable e) {
        getLifecycle().updateStep(s -> s
                .setStatus(getStatus(e).orElse(Status.BROKEN)));
        getLifecycle().stopStep();
    }

    @AfterReturning(pointcut = "anyAssert()")
    public void stepStop() {
        getLifecycle().updateStep(s -> s.setStatus(Status.PASSED));
        getLifecycle().stopStep();
    }

    /**
     * For tests only.
     *
     * @param allure allure lifecycle to set.
     */
    public static void setLifecycle(final AllureLifecycle allure) {
        lifecycle.set(allure);
    }

    public static AllureLifecycle getLifecycle() {
        return lifecycle.get();
    }

}
