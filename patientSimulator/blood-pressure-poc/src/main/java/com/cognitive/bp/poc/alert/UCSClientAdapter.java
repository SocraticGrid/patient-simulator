/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitive.bp.poc.alert;

import org.socraticgrid.hl7.services.uc.exceptions.BadBodyException;
import org.socraticgrid.hl7.services.uc.exceptions.FeatureNotSupportedException;
import org.socraticgrid.hl7.services.uc.exceptions.InvalidContentException;
import org.socraticgrid.hl7.services.uc.exceptions.InvalidMessageException;
import org.socraticgrid.hl7.services.uc.exceptions.MissingBodyTypeException;
import org.socraticgrid.hl7.services.uc.exceptions.ProcessingException;
import org.socraticgrid.hl7.services.uc.exceptions.ServiceAdapterFaultException;
import org.socraticgrid.hl7.services.uc.exceptions.UndeliverableMessageException;
import org.socraticgrid.hl7.services.uc.interfaces.UCSClientIntf;
import org.socraticgrid.hl7.services.uc.model.Conversation;
import org.socraticgrid.hl7.services.uc.model.DeliveryAddress;
import org.socraticgrid.hl7.services.uc.model.Message;
import org.socraticgrid.hl7.services.uc.model.MessageModel;

/**
 *
 * @author esteban
 */
public class UCSClientAdapter implements UCSClientIntf{

    @Override
    public boolean callReady(Conversation c, String string, String string1) {
        return false;
    }

    @Override
    public <T extends Message> boolean handleException(MessageModel<T> mm, DeliveryAddress da, DeliveryAddress da1, ProcessingException pe, String string) {
        return false;
    }

    @Override
    public <T extends Message> boolean handleNotification(MessageModel<T> mm, String string) {
        return false;
    }

    @Override
    public <T extends Message> MessageModel<T> handleResponse(MessageModel<T> mm, String string) throws InvalidMessageException, InvalidContentException, MissingBodyTypeException, BadBodyException, ServiceAdapterFaultException, UndeliverableMessageException, FeatureNotSupportedException {
        return null;
    }

    @Override
    public <T extends Message> boolean receiveMessage(MessageModel<T> mm, String string) {
        return false;
    }
    
}
