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

public interface ILogger {
    <A> ILogEntry entered(LogPoint logPoint, A argument, ILogEntry previous);
    <R> void leftRegularly(ILogEntry logEntry, R result);
    void leftExceptionally(ILogEntry logEntry, Exception exception);
}
