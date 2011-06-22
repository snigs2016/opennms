/*******************************************************************************
 * This file is part of the OpenNMS(R) Application.
 *
 * OpenNMS(R) is Copyright (C) 1999-2011 The OpenNMS Group, Inc.  All rights reserved.
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * OpenNMS(R) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 *     along with OpenNMS(R).  If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information contact: 
 *     OpenNMS(R) Licensing <license@opennms.org>
 *     http://www.opennms.org/
 *     http://www.opennms.com/
 *******************************************************************************/
package org.opennms.netmgt.ticketer.otrs;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.opennms.core.utils.LogUtils;

/**
 * <p>DefaultOtrsConfigDao class.</p>
 *
 * @author ranger
 * @version $Id: $
 */
public class DefaultOtrsConfigDao {

	/**
	 * Retrieves the properties defined in the otrs.properties file.
	 * 
	 * @param otrsTicketerPlugin
	 * @return a
	 *         <code>java.util.Properties object containing otrs plugin defined properties
	 */
	
	private Configuration getProperties() {
		
		String propsFile = new String(System.getProperty("opennms.home") + "/etc/otrs.properties");
		
		LogUtils.debugf(this, "loading properties from: %s", propsFile);
		
		Configuration config = null;
		
		try {
			config = new PropertiesConfiguration(propsFile);
		} catch (final ConfigurationException e) {
		    LogUtils.debugf(this, e, "Unable to load properties from %s", propsFile);
		}
	
		return config;
	
	}
	
	/**
	 * <p>getUserName</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getUserName() {
		return getProperties().getString("otrs.username");
	}

	
	String getPassword() {
		return getProperties().getString("otrs.password");
	}
	
	String getEndpoint() {
		return getProperties().getString("otrs.endpoint");
	}
	
	String getState() {
		return getProperties().getString("otrs.state");
	}
	
	Integer getOwnerID() {
		return getProperties().getInteger("otrs.ownerid", 1);
	}
	
	String getPriority() {
		return getProperties().getString("otrs.priority");
	}
	
	String getLock() {
		return getProperties().getString("otrs.lock");
	}
	
	String getQueue() {
		return getProperties().getString("otrs.queue");
	}
	
	String getArticleFrom() {
		return getProperties().getString("otrs.articlefrom");
	}
	
	String getArticleType() {
		return getProperties().getString("otrs.articletype");
	}
	
	String getArticleSenderType() {
		return getProperties().getString("otrs.articlesendertype");
	}
	
	String getArticleContentType() {
		return getProperties().getString("otrs.articlecontenttype");
	}
	
	String getArticleHistoryComment() {
		return getProperties().getString("otrs.articlehistorycomment");
	}
	
	String getArticleHistoryType() {
		return getProperties().getString("otrs.articlehistorytype");
	}
	
	@SuppressWarnings("unchecked")
	List<Integer> getValidClosedStateId() {
		
		List<String> closedStateId = getProperties().getList("otrs.validclosedstateid");
		return stringToInt(closedStateId);
		
	}
	
	@SuppressWarnings("unchecked")
	List<Integer> getValidOpenStateId() {
		
		List<String> openStateId = getProperties().getList("otrs.validopenstateid");
		return stringToInt(openStateId);
		
	}
	
	@SuppressWarnings("unchecked")
	List<Integer> getValidCancelledStateId() {
		
		List<String> cancelledStateId = getProperties().getList("otrs.validcancelledstateid");
		return stringToInt(cancelledStateId);
		
	}
	
	Integer getOpenStateId() {
		return getProperties().getInteger("otrs.openstateid", 1);
	}
	
	Integer getClosedStateId() {
		LogUtils.debugf(this, "getting closed state ID: %d", getProperties().getInteger("otrs.closedstateid", 2)); 
		return getProperties().getInteger("otrs.closedstateid", 2);
	}
	
	Integer getCancelledStateId() {
		return getProperties().getInteger("otrs.cancelledstateid", 5);
	}
	
	String getDefaultUser() {
		return getProperties().getString("otrs.defaultuser");
	}
	
	private List<Integer> stringToInt(List<String> strings) {
		
		List<Integer> intList = new ArrayList<Integer>();
		
		for (String string : strings) {
			intList.add( Integer.parseInt(string));
		}
		
		return intList;
	}

	String getTicketOpenedMessage() {
		return getProperties().getString("otrs.ticketopenedmessage");
	}

	String getTicketClosedMessage() {
		return getProperties().getString("otrs.ticketclosedmessage");
	}

	String getTicketCancelledMessage() {
		return getProperties().getString("otrs.ticketcancelledmessage");
	}

	String getTicketUpdatedMessage() {
		return getProperties().getString("otrs.ticketupdatedmessage");
	}

	String getArticleUpdateSubject() {
		return getProperties().getString("otrs.articleupdatesubject");
	}
}
