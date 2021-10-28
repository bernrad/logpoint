package de.datalab.logpoint.logpoint;

public interface ILogger {
    <A> ILogEntry entered(LogPoint logPoint, A argument, ILogEntry previous);
    <R> void leftRegularly(ILogEntry logEntry, R result);
    void leftExceptionally(ILogEntry logEntry, Exception exception);
}
