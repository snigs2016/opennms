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
package org.opennms.reporting.svclayer;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opennms.netmgt.dao.OnmsReportConfigDao;
import org.opennms.reporting.availability.AvailabilityCalculator;
import org.opennms.reporting.core.svclayer.ParameterConversionService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

@RunWith(SpringJUnit4ClassRunner.class)
@TestExecutionListeners({
    DependencyInjectionTestExecutionListener.class
})
@ContextConfiguration(locations={
        "classpath:org/opennms/reporting/availability/svclayer/AvailabilityReportServiceTest.xml"
})
public class AvailabilityReportServiceTest implements InitializingBean {
    
    @Autowired
    @Qualifier("mockClassicCalculator")
    AvailabilityCalculator m_classicCalculator;
    @Autowired
    @Qualifier("mockCalendarCalculator")
    AvailabilityCalculator m_calendarCalculator;
    @Autowired
    OnmsReportConfigDao m_configDao;
    @Autowired
    ParameterConversionService m_parameterConversionService;
    
    @Override
    public void afterPropertiesSet() {
        Assert.assertNotNull(m_classicCalculator);
        Assert.assertNotNull(m_calendarCalculator);
        Assert.assertNotNull(m_configDao);
        Assert.assertNotNull(m_parameterConversionService);
    }
    
    /**
     * TODO: Write a test
     */
    @Test
    public void testMe() {
        // Write some tests
    }

}
