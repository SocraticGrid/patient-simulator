/* 
 * Copyright 2015 Cognitive Medical Systems, Inc (http://www.cognitivemedicine.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package javax.jnlp;

/**
 * The <code>UnavailableServiceException</code> is thrown by the
 * <code>ServiceManager</code> when a non-existing or unavailable service
 * is looked up.
 *
 * @since 1.0
 *
 * @see ServiceManager
 */
public class UnavailableServiceException extends Exception {
     
  /**
   * Constructs an <code>UnavailableServiceException</code> with <code>null</code>
   * as its error detail message.
   */
  public UnavailableServiceException() {
    super();
  }
     
  /**
   * Constructs an <code>UnavailableServiceException</code> with the specified detail
   * message. The error message string <code>s</code> can later be
   * retrieved by the <code>{@link java.lang.Throwable#getMessage}</code>
   * method of class <code>java.lang.Throwable</code>.
   *
   * @param msg the detail message.
   */
  public UnavailableServiceException(String msg) {
    super(msg);
  }
}