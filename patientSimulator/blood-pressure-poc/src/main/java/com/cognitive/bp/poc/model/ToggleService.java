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
package com.cognitive.bp.poc.model;

/**
 *
 * @author esteban
 */
public class ToggleService {
    
    public static enum SERVICE{
        ALERTS,
        GENOME
    }
    
    private final SERVICE service;
    private final boolean on;

    public ToggleService(SERVICE service, boolean on) {
        this.service = service;
        this.on = on;
    }

    public SERVICE getService() {
        return service;
    }

    public boolean isOn() {
        return on;
    }
    
}
