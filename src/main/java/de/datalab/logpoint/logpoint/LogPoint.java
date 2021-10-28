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
            if (stack.empty())
            {
                threadLocal.remove();
            }
        }
        optionalLogger.ifPresent(logger -> logger.leftRegularly(logEntry, r));
        return r;
    }

}
