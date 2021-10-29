/*
Copyright 2021 Bernhard Radke
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
    http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package de.datalab.logpoint.logpoint;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Stack;
import java.util.function.Supplier;

public class LogPoint {
    private static final ThreadLocal<Stack<ILogEntry>> threadLocal = ThreadLocal.withInitial(Stack::new);

    private static Map<Class, LogPoint> logPointMap = new HashMap<>();

    private static Optional<ILogger> optionalLogger = Optional.empty();

    private final Class clazz;
    private final String name;

    LogPoint(Class clazz, String name)
    {
        this.clazz = clazz;
        this.name = name;
    }

    @Override
    public String toString() {
        return "LogPoint{" +
                "clazz=" + clazz +
                ", name='" + name + '\'' +
                '}';
    }

    public static LogPoint register(Class clazz, String name)
    {
        // if (logPointMap.get(clazz) != null) throw new IllegalArgumentException( "Class already registred");
        LogPoint logPoint = new LogPoint(clazz, name);
        // logPointMap.put(clazz, logPoint);
        return logPoint;
    }

    public static void register(ILogger newLogger)
    {
        optionalLogger.ifPresent(logger -> {throw new IllegalArgumentException();});
        optionalLogger = Optional.of(newLogger);
    }

    public static <A, R> R enter(LogPoint logPoint, Supplier<R> supplier, A argument)
    {
        Stack<ILogEntry> stack = threadLocal.get();

        ILogEntry logEntry = optionalLogger.map(iLogger -> iLogger.entered(logPoint, argument, stack.empty() ? null : stack.peek())).orElse(null);
        stack.push(logEntry);
        R r;
        try
        {
            r = supplier.get();
        }
        catch (Exception exception)
        {
            optionalLogger.ifPresent(logger -> logger.leftExceptionally(logEntry, exception));
            throw exception;
        }
        finally
        {
            stack.pop();
            if (stack.empty()) threadLocal.remove();
        }
        optionalLogger.ifPresent(logger -> logger.leftRegularly(logEntry, r));
        return r;
    }

}
