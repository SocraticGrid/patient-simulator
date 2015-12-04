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
 * The <code>ServiceManager</code> provides static methods to lookup JNLP services. This class
 * is abstract and final and cannot be instantiated.
 * <p>
 * Requests are delegated to a <code>ServiceManagerStub</code>
 * object. This object must be set by the JNLP Client on startup using the
 * <code>setServiceManagerStub</code> method.
 *
 * @since 1.0
 *
 * @see ServiceManagerStub
 */
public final class ServiceManager {
  static private ServiceManagerStub _stub = null;
     
  /** Private constructor in order to prevent instantiation */
  private ServiceManager() { /* dummy */ }

  /**
   * Asks the JNLP Client for a service with a given name. The lookup
   * must be idempotent, that is return the same object for each invocation
   * with the same name.
   *
   * @param name Name of service to lookup.
   *
   * @return An object implementing the service. <code>null</code>
   * will never be returned. Instead an exception will be thrown.
   *
   * @exception <code>UnavailableServiceException</code> if the service is not available, or if <code>name</code> is null.
   */
  public static Object lookup(String name) throws UnavailableServiceException {
    if (_stub != null) {
      return _stub.lookup(name);
    } else {
      throw new UnavailableServiceException("uninitialized");
    }
  }
     
  /**
   * Returns the names of all services implemented by the JNLP Client.
   */
  public static String[] getServiceNames() {
    if (_stub != null) {
      return _stub.getServiceNames();
    } else {
      return null;
    }
  }
     
  /**
   * Sets the object that all <code>lookup</code> and <code>getServiceNames</code>
   * requests are delegated to. The <code>setServiceManagerStub</code> call is ignored
   * if the stub has already been set.
   * <p>
   * This method should be called exactly once by the JNLP Client, and never be
   * called by a launched application.
   *
   * @param stub The ServiceManagerStub object to delegate to
   */
  public static synchronized void setServiceManagerStub(ServiceManagerStub stub) {
    if (_stub == null) {
      _stub = stub;
    }
  }
}
