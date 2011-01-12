/*
 *  soapUI, copyright (C) 2004-2010 eviware.com 
 *
 *  soapUI is free software; you can redistribute it and/or modify it under the 
 *  terms of version 2.1 of the GNU Lesser General Public License as published by 
 *  the Free Software Foundation.
 *
 *  soapUI is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without 
 *  even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 *  See the GNU Lesser General Public License for more details at gnu.org.
 */

package com.eviware.soapui.impl.wsdl.submit.transports.jms;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.Topic;

import com.eviware.soapui.SoapUI;
import com.eviware.soapui.model.iface.Request;
import com.eviware.soapui.model.iface.Response;
import com.eviware.soapui.model.iface.SubmitContext;

public class HermesJmsRequestPublishTransport extends HermesJmsRequestTransport
{

	public Response execute( SubmitContext submitContext, Request request, long timeStarted ) throws Exception
	{
		Session topicSession = null;
		JMSConnectionHolder jmsConnectionHolder = null;
		try
		{
			init( submitContext, request );
			jmsConnectionHolder = new JMSConnectionHolder( jmsEndpoint, hermes, true, clientID, username, password );

			// session
			topicSession = jmsConnectionHolder.getSession();

			// destination
			Topic topicPublish = jmsConnectionHolder.getTopic( jmsConnectionHolder.getJmsEndpoint().getSend() );

			Message messagePublish = messagePublish( submitContext, request, topicSession,
					jmsConnectionHolder.getHermes(), topicPublish );

			return makeEmptyResponse( submitContext, request, timeStarted, messagePublish );
		}
		catch( JMSException jmse )
		{
			return errorResponse( submitContext, request, timeStarted, jmse );
		}
		catch( Throwable t )
		{
			SoapUI.logError( t );
		}
		finally
		{
			jmsConnectionHolder.closeAll();
			closeSessionAndConnection( jmsConnectionHolder != null ? jmsConnectionHolder.getConnection() : null,
					topicSession );
		}
		return null;
	}

}
