/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitive.bp.poc.alert.mock;

import com.cognitive.bp.poc.alert.UCSNotifier;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.socraticgrid.hl7.services.uc.exceptions.BadBodyException;
import org.socraticgrid.hl7.services.uc.exceptions.DeliveryException;
import org.socraticgrid.hl7.services.uc.exceptions.FeatureNotSupportedException;
import org.socraticgrid.hl7.services.uc.exceptions.InvalidAddressException;
import org.socraticgrid.hl7.services.uc.exceptions.InvalidContentException;
import org.socraticgrid.hl7.services.uc.exceptions.InvalidMessageException;
import org.socraticgrid.hl7.services.uc.exceptions.InvalidQueryException;
import org.socraticgrid.hl7.services.uc.exceptions.MessageDeliveryTimeoutException;
import org.socraticgrid.hl7.services.uc.exceptions.MissingBodyTypeException;
import org.socraticgrid.hl7.services.uc.exceptions.ReadOnlyException;
import org.socraticgrid.hl7.services.uc.exceptions.ServiceAdapterFaultException;
import org.socraticgrid.hl7.services.uc.exceptions.ServiceOfflineException;
import org.socraticgrid.hl7.services.uc.exceptions.UndeliverableMessageException;
import org.socraticgrid.hl7.services.uc.exceptions.UnknownServiceException;
import org.socraticgrid.hl7.services.uc.exceptions.UnknownUserException;
import org.socraticgrid.hl7.services.uc.exceptions.UpdateException;
import org.socraticgrid.hl7.services.uc.interfaces.ClientIntf;
import org.socraticgrid.hl7.services.uc.model.CommunicationsPreferences;
import org.socraticgrid.hl7.services.uc.model.Message;
import org.socraticgrid.hl7.services.uc.model.MessageModel;
import org.socraticgrid.hl7.services.uc.model.MessageSummary;
import org.socraticgrid.hl7.services.uc.model.QueryScope;
import org.socraticgrid.hl7.services.uc.model.Recipient;
import org.socraticgrid.hl7.services.uc.model.UserContactInfo;

/**
 *
 * @author esteban
 */
public class MockUCSClient implements ClientIntf {
    
    private static Logger LOGGER = LoggerFactory.getLogger(MockUCSClient.class);
    private final static String PREFIX = "[MockUCSClient] ";

    @Override
    public boolean assertPresence(String string, String string1, String string2) throws FeatureNotSupportedException, UnknownUserException {
        LOGGER.debug("{} assertPresence({}, {}, {}) invoked", PREFIX, string, string1, string2);
        return false;
    }

    @Override
    public boolean cancelMessage(String string, boolean bln) throws InvalidMessageException, FeatureNotSupportedException, ServiceOfflineException, ReadOnlyException {
        LOGGER.debug("{} assertPresence({}, {}) invoked", PREFIX, string, bln);
        return false;
    }

    @Override
    public <T extends Message> MessageModel<T> createMessage(MessageModel<T> mm) {
        LOGGER.debug("{} createMessage({}) invoked", PREFIX, mm);
        return null;
    }

    @Override
    public List<MessageSummary> queryMessage(String string) throws InvalidQueryException {
        LOGGER.debug("{} queryMessage({}) invoked", PREFIX, string);
        return null;
    }

    @Override
    public List<String> queryUsers(String string) throws InvalidQueryException {
        LOGGER.debug("{} queryUsers({}) invoked", PREFIX, string);
        return null;
    }

    @Override
    public <T extends Message> MessageModel<T> retrieveMessage(String string) throws InvalidMessageException {
        LOGGER.debug("{} retrieveMessage({}) invoked", PREFIX, string);
        return null;
    }

    @Override
    public UserContactInfo retrieveUser(String string) throws UnknownUserException {
        LOGGER.debug("{} retrieveUser({}) invoked", PREFIX, string);
        return null;
    }

    @Override
    public <T extends Message> String sendMessage(MessageModel<T> mm) throws InvalidMessageException, InvalidContentException, MissingBodyTypeException, BadBodyException, InvalidAddressException, UnknownServiceException, DeliveryException, MessageDeliveryTimeoutException, ServiceAdapterFaultException, UndeliverableMessageException, FeatureNotSupportedException, ServiceOfflineException, UpdateException, ReadOnlyException {
        LOGGER.debug("{} sendMessage({}) invoked", PREFIX, mm);
        return null;
    }

    @Override
    public void sendMessageById(String string) throws InvalidMessageException, InvalidContentException, MissingBodyTypeException, BadBodyException, InvalidAddressException, UnknownServiceException, DeliveryException, MessageDeliveryTimeoutException, ServiceAdapterFaultException, UndeliverableMessageException, FeatureNotSupportedException, ServiceOfflineException, UpdateException, ReadOnlyException {
        LOGGER.debug("{} sendMessageById({}) invoked", PREFIX, string);
    }

    @Override
    public boolean updateCommunicationsPreferences(String string, CommunicationsPreferences cp) throws UnknownUserException, UpdateException {
        LOGGER.debug("{} updateCommunicationsPreferences({}, {}) invoked", PREFIX, string, cp);
        return false;
    }

    @Override
    public <T extends Message> boolean updateMessage(String string, MessageModel<T> mm, boolean bln) throws InvalidMessageException, InvalidContentException, MissingBodyTypeException, BadBodyException, InvalidAddressException, UnknownServiceException, FeatureNotSupportedException, UpdateException, ReadOnlyException {
        LOGGER.debug("{} updateMessage({}, {}, {}) invoked", PREFIX, string, mm, bln);
        return false;
    }

    @Override
    public List<String> findSupportedContent(List<Recipient> list, QueryScope qs) throws InvalidAddressException, UnknownServiceException, FeatureNotSupportedException {
        LOGGER.debug("{} findSupportedContent({}, {}) invoked", PREFIX, list, qs);
        return null;
    }
    
}
