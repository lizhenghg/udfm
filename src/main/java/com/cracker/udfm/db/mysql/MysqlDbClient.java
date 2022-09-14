package com.cracker.udfm.db.mysql;

import com.cracker.udfm.common.BaseClusterClient;
import com.cracker.udfm.core.DbServer;
import com.cracker.udfm.utils.DataSourceUtil;
import com.cracker.udfm.utils.Logger;

import javax.sql.DataSource;
import java.util.Iterator;
import java.util.Map.Entry;

/**
 * mysql客户端
 *
 * @author lizh<2486479615@qq.com>
 * <br/>========================================
 * <br/>公司：myself
 * <br/>开发时间：2022-07-02
 * <br/>版本：1.1.0
 * <br/>JDK版本：1.8
 * <br/>========================================
 */
public class MysqlDbClient extends BaseClusterClient {
    
    
    private DataSource dataSource;

    @Override
    public int toBeReady() {

        int ret = 0;
        // 这里mysql默认只有一个vip(virtual ip)，无论是主从还是双主、多主多从，都应该只提供一个vip出来
        for (Entry<Integer, DbServer> entry : this.servers.entrySet()) {
            DbServer server = entry.getValue();
            try {
                this.dataSource = DataSourceUtil.createDruidDataSource(server);
            } catch (Exception e) {
                Logger.error("Fail connect to mysql server %s %d", server.getHost(), server.getPort());
                ret = 1;
            }
        }
        return ret;
    }

    @Override
    public int close() {
        return 0;
    }
}
