/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
