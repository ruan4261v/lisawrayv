/**
 * Copyright © 2018 organization baomidou
 * <pre>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * <pre/>
 */
package com.baomidou.dynamic.datasource.ds;

import com.baomidou.dynamic.datasource.tx.ConnectionFactory;
import com.baomidou.dynamic.datasource.tx.ConnectionProxy;
import com.baomidou.dynamic.datasource.tx.TransactionContext;
import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import com.baomidou.dynamic.datasource.toolkit.StringUtils;
import org.springframework.jdbc.datasource.AbstractDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 抽象动态获取数据源
 *
 * @author TaoYu
 * @since 2.2.0
 */
public abstract class AbstractRoutingDataSource extends AbstractDataSource {

    protected abstract DataSource determineDataSource();

    @Override
    public Connection getConnection() throws SQLException {
        Connection connection =
                StringUtils.isNotBlank(TransactionContext.getXID()) ? ConnectionFactory.getConnection() : null;
        return getConnectionProxy(connection != null ? connection : determineDataSource().getConnection());
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        Connection connection =
                StringUtils.isNotBlank(TransactionContext.getXID()) ? ConnectionFactory.getConnection() : null;
        return getConnectionProxy(
                connection != null ? connection : determineDataSource().getConnection(username, password));
    }

    public Connection getConnectionProxy(Connection connection) {
        if (StringUtils.isBlank(TransactionContext.getXID()) || connection instanceof ConnectionProxy) {
            return connection;
        }
        ConnectionProxy connectionProxy = new ConnectionProxy(connection, DynamicDataSourceContextHolder.peek());
        ConnectionFactory.putConnection(TransactionContext.getXID(), connectionProxy);
        return connectionProxy;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T unwrap(Class<T> iface) throws SQLException {
        if (iface.isInstance(this)) {
            return (T) this;
        }
        return determineDataSource().unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return (iface.isInstance(this) || determineDataSource().isWrapperFor(iface));
    }
}
