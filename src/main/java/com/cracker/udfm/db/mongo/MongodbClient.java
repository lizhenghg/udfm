package com.cracker.udfm.db.mongo;

import com.cracker.udfm.common.BaseClusterClient;

/**
 * mongodb客户端
 *
 * @author lizh<2486479615@qq.com>
 * <br/>========================================
 * <br/>公司：myself
 * <br/>开发时间：2022-07-02
 * <br/>版本：1.1.0
 * <br/>JDK版本：1.8
 * <br/>========================================
 */
public class MongodbClient extends BaseClusterClient {
    
    @Override
    public int toBeReady() {
        return 0;
    }

    @Override
    public int close() {
        return 0;
    }
}
