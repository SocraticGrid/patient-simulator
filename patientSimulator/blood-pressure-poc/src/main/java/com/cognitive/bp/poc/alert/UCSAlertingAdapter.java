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
package com.cognitive.bp.poc.alert;

import java.util.List;
import org.socraticgrid.hl7.services.uc.interfaces.UCSAlertingIntf;
import org.socraticgrid.hl7.services.uc.model.Message;
import org.socraticgrid.hl7.services.uc.model.MessageModel;

/**
 *
 * @author esteban
 */
public class UCSAlertingAdapter implements UCSAlertingIntf{

    @Override
    public <T extends Message> boolean receiveAlertMessage(MessageModel<T> mm, List<String> list, String string) {
        return false;
    }

    @Override
    public <T extends Message> boolean updateAlertMessage(MessageModel<T> mm, MessageModel<T> mm1, List<String> list, String string) {
        return false;
    }

    @Override
    public <T extends Message> boolean cancelAlertMessage(MessageModel<T> mm, List<String> list, String string) {
        return false;
    }
    
}
