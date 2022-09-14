package com.cracker.udfm.core;

import com.cracker.udfm.common.AbstractDatabase;
import com.cracker.udfm.common.BaseClusterClient;

/**
 * 抽象的物理存储数据库类
 *
 * @author lizh<2486479615@qq.com>
 * <br/>========================================
 * <br/>公司：myself
 * <br/>开发时间：2022-07-04
 * <br/>版本：1.1.0
 * <br/>JDK版本：1.8
 * <br/>========================================
 */
public abstract class AbstractPhysicDatabase extends AbstractDatabase {

    protected BaseClusterClient client;

    public BaseClusterClient getClient() {
        return client;
    }

    public void setClient(BaseClusterClient client) {
        this.client = client;
    }
}
