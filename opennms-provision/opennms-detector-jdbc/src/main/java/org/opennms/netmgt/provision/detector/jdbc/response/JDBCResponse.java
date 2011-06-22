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
package org.opennms.netmgt.provision.detector.jdbc.response;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.opennms.core.utils.ThreadCategory;

/**
 * <p>JDBCResponse class.</p>
 *
 * @author thedesloge
 * @version $Id: $
 */
public class JDBCResponse {
    
    private ResultSet m_result;
    private boolean m_isValidProcedureCall = false;
    
    /**
     * <p>receive</p>
     *
     * @param conn a {@link java.sql.Connection} object.
     * @throws java.sql.SQLException if any.
     */
    public void receive(Connection conn) throws SQLException {
        
        DatabaseMetaData metadata = conn.getMetaData();
        log().debug("got database metadata");

        m_result = metadata.getCatalogs();
        
    }
    
    /**
     * <p>resultSetNotNull</p>
     *
     * @return a boolean.
     */
    public boolean resultSetNotNull() {
        try {
            while (m_result.next())
            {
                m_result.getString(1);
                if (log().isDebugEnabled()) {
                    log().debug("Metadata catalog: '" + m_result.getString(1) + "'");
                }
            }
            
            m_result.close();
            return true;
        } catch (SQLException e) {
            log().info("Unable to get result set", e);
        }

        return false;
    }
    
    /**
     * <p>validProcedureCall</p>
     *
     * @return a boolean.
     */
    public boolean validProcedureCall(){
        return isValidProcedureCall();
    }

    /**
     * <p>setValidProcedureCall</p>
     *
     * @param isValidProcedureCall a boolean.
     */
    public void setValidProcedureCall(boolean isValidProcedureCall) {
        m_isValidProcedureCall = isValidProcedureCall;
    }

    /**
     * <p>isValidProcedureCall</p>
     *
     * @return a boolean.
     */
    public boolean isValidProcedureCall() {
        return m_isValidProcedureCall;
    }


    /**
     * <p>log</p>
     *
     * @return a {@link org.opennms.core.utils.ThreadCategory} object.
     */
    public ThreadCategory log() {
        return ThreadCategory.getInstance(getClass());
    }
}
