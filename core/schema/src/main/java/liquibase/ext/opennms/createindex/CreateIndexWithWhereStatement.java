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
package liquibase.ext.opennms.createindex;

import liquibase.statement.SqlStatement;
import liquibase.statement.core.CreateIndexStatement;

public class CreateIndexWithWhereStatement extends CreateIndexStatement
		implements SqlStatement {

	private String m_where;

	public CreateIndexWithWhereStatement(final String indexName, final String tableSchemaName, final String tableName, final Boolean isUnique, final String associatedWith, final String... columns) {
		super(indexName, tableSchemaName, tableName, isUnique, associatedWith, columns);
	}

	public CreateIndexWithWhereStatement(final CreateIndexStatement statement, final String where) {
		this(statement.getIndexName(), statement.getTableSchemaName(), statement.getTableName(), statement.isUnique(), statement.getAssociatedWith(), statement.getColumns());
		m_where = where;
	}

	public String getWhere() {
		return m_where;
	}
	
	public CreateIndexWithWhereStatement setWhere(final String where) {
		m_where = where;
		return this;
	}
}
