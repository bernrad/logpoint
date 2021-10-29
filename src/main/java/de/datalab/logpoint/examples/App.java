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
package de.datalab.logpoint.examples;

import de.datalab.logpoint.logpoint.ILogEntry;
import de.datalab.logpoint.logpoint.ILogger;
import de.datalab.logpoint.logpoint.LogPoint;

public class App 
{
    static LogPoint logPoint1 = LogPoint.register(App.class, "f1");
    static LogPoint logPoint2 = LogPoint.register(App.class, "f2");

    static LogPoint logPointFib = LogPoint.register(App.class, "fib");


    static class LogEntry implements ILogEntry
    {
        private final LogPoint logPoint;
        private final LogEntry previous;
        public final Object arg;

        LogEntry(LogPoint logPoint, ILogEntry previous, Object arg)
        {
            this.logPoint = logPoint;
            this.previous = (LogEntry) previous;
            this.arg = arg;

        }

        public LogPoint getLogPoint() {
            return logPoint;
        }

        @Override
        public String toString() {
            return "LogEntry{" +
                    "logPoint=" + logPoint +
                    ", previous=" + previous +
                    ", arg=" + arg +
                    '}';
        }
    }

    static class Logger implements ILogger
    {
        @Override
        public <A> ILogEntry entered(LogPoint logPoint, A argument, ILogEntry previous) {

            LogEntry logEntry = new LogEntry(logPoint, previous, argument);
            System.out.println(logEntry.toString() + " entered");
            return logEntry;
        }

        @Override
        public <R> void leftRegularly(ILogEntry logEntry, R result) {
            System.out.println(((LogEntry)logEntry).logPoint.toString() + " leftRegularly");
        }

        @Override
        public void leftExceptionally(ILogEntry logEntry, Exception exception) {
            System.out.println(((LogEntry)logEntry).logPoint.toString() + " leftExceptionally");
        }
    }

    public static String f2(String arg)
    {
        return "Hello " + arg;
    }

    public static String f1(String arg)
    {
        return LogPoint.enter(logPoint2, () -> f2(arg), arg);
    }

    public static int fib1(int i)
    {
        if (i == 1 || i == 2) return 1;
        return fib(i-1) + fib(i-2);
    }

    public static int fib(int i)
    {
        return LogPoint.enter(logPointFib, () -> fib1(i), i);
    }

    public static void main( String[] args )
    {
        Logger logger = new Logger();
        LogPoint.register(logger);
        String arg = "world";
        String result = LogPoint.enter(logPoint1, () -> f1(arg), arg);
        System.out.println(result);
        result = LogPoint.enter(logPoint1, () -> f1(arg), arg);
        System.out.println(result);
        fib(5);

    }
}
