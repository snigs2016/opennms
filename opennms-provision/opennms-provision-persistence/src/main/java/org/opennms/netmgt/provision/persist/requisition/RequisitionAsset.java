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
//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0.3-b01-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2009.01.29 at 01:15:48 PM EST 
//


package org.opennms.netmgt.provision.persist.requisition;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * <p>RequisitionAsset class.</p>
 *
 * @author ranger
 * @version $Id: $
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="")
@XmlRootElement(name="asset")
public class RequisitionAsset implements Comparable<RequisitionAsset> {

    @XmlAttribute(name="name", required=true)
    protected String m_name;

    @XmlAttribute(name="value", required=true)
    protected String m_value;

    /**
     * <p>Constructor for RequisitionAsset.</p>
     */
    public RequisitionAsset() {
    }

    /**
     * <p>Constructor for RequisitionAsset.</p>
     *
     * @param name a {@link java.lang.String} object.
     * @param value a {@link java.lang.String} object.
     */
    public RequisitionAsset(String name, String value) {
        m_name = name;
        m_value = value;
    }

    /**
     * <p>getName</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getName() {
        return m_name;
    }

    /**
     * <p>setName</p>
     *
     * @param value a {@link java.lang.String} object.
     */
    public void setName(String value) {
        m_name = value;
    }

    /**
     * <p>getValue</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getValue() {
        return m_value;
    }

    /**
     * <p>setValue</p>
     *
     * @param value a {@link java.lang.String} object.
     */
    public void setValue(String value) {
        m_value = value;
    }
    
    @Override
    public boolean equals(Object o) {
        if (o instanceof RequisitionAsset) {
            return this.compareTo((RequisitionAsset)o) == 0;
        } else return false;
    }

    public int compareTo(RequisitionAsset o) {
        return m_name.compareTo(o.getName());
    }

    public String toString() {
    	return new ToStringBuilder(this)
    		.append("name", m_name)
    		.append("value", m_value)
    		.toString();
    }
}
