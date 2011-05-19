//
// This file is part of the OpenNMS(R) Application.
//
// OpenNMS(R) is Copyright (C) 2002-2003 The OpenNMS Group, Inc.  All rights reserved.
// OpenNMS(R) is a derivative work, containing both original code, included code and modified
// code that was published under the GNU General Public License. Copyrights for modified 
// and included code are below.
//
// OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
//
// Copyright (C) 1999-2001 Oculan Corp.  All rights reserved.
//
// This program is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.                                                            
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
//       
// For more information contact: 
//      OpenNMS Licensing       <license@opennms.org>
//      http://www.opennms.org/
//      http://www.opennms.com/
//
// Tab Size = 8
//

package org.opennms.netmgt.capsd.plugins;

import java.util.Map;

import org.opennms.core.utils.LogUtils;
import org.opennms.core.utils.ParameterMap;
import org.opennms.netmgt.capsd.AbstractPlugin;
import org.opennms.netmgt.icmp.PingConstants;
import org.opennms.netmgt.icmp.PingerFactory;
import org.opennms.netmgt.model.discovery.IPAddress;

/**
 * This class provides Capsd with the ability to check for ICMP support on new
 * interfaces as them are passed into the system. In order to minimize the
 * number of sockets and threads, this class creates a daemon thread to handle
 * all responses and a single socket for sending echo request to various hosts.
 *
 * @author <A HREF="mailto:weave@oculan.com">Weave </a>
 * @author <A HREF="http://www.opennms.org/">OpenNMS </A>
 */
public final class IcmpPlugin extends AbstractPlugin {
    /**
     * The name of the protocol that is supported by this plugin
     */
    private static final String PROTOCOL_NAME = "ICMP";

    /**
     * Returns the name of the protocol that this plugin checks on the target
     * system for support.
     *
     * @return The protocol name for this plugin.
     */
    public String getProtocolName() {
        return PROTOCOL_NAME;
    }

    /**
     * {@inheritDoc}
     *
     * Returns true if the protocol defined by this plugin is supported. If the
     * protocol is not supported then a false value is returned to the caller.
     */
    public boolean isProtocolSupported(IPAddress ipAddress) {
		try {
	    	Number retval = PingerFactory.getInstance().ping(ipAddress.toInetAddress());
	    	if (retval != null) {
	    		return true;
	    	}
		} catch (final Throwable e) {
			LogUtils.warnf(this, e, "Pinger failed to ping %s", ipAddress);
		}
		return false;
    }

    /**
     * {@inheritDoc}
     *
     * Returns true if the protocol defined by this plugin is supported. If the
     * protocol is not supported then a false value is returned to the caller.
     * The qualifier map passed to the method is used by the plugin to return
     * additional information by key-name. These key-value pairs can be added to
     * service events if needed.
     */
    public boolean isProtocolSupported(IPAddress ipAddress, Map<String, Object> qualifiers) {
    	int retries;
    	long timeout;

    	try {
    		if (qualifiers != null) {
    			retries = ParameterMap.getKeyedInteger(qualifiers, "retry", PingConstants.DEFAULT_RETRIES);
    			timeout = ParameterMap.getKeyedLong(qualifiers, "timeout", PingConstants.DEFAULT_TIMEOUT);
    		} else {
    			retries = PingConstants.DEFAULT_RETRIES;
    			timeout = PingConstants.DEFAULT_TIMEOUT;
    		}
    		final Number retval = PingerFactory.getInstance().ping(ipAddress.toInetAddress(), timeout, retries);
    		if (retval != null) {
    			return true;
    		}
    	} catch (final Throwable e) {
    		LogUtils.warnf(this, e, "Pinger failed to ping %s", ipAddress.toUserString());
        }
    	
    	return false;
    }
}
