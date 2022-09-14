package com.cracker.udfm;


import com.cracker.udfm.common.BaseClusterClient;
import com.cracker.udfm.utils.Assert;
import com.cracker.udfm.utils.Logger;
import com.cracker.udfm.common.AbstractDatabase;
import com.cracker.udfm.core.AbstractPhysicDatabase;
import com.cracker.udfm.core.DataSet;
import com.cracker.udfm.core.DbServer;
import com.cracker.udfm.core.Entity;
import com.cracker.udfm.exception.DalException;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.*;
import java.util.Map.Entry;

/**
 * 类: DAL数据访问层管理
 *
 * @author lizh<2486479615@qq.com>
 * <br/>========================================
 * <br/>公司：myself
 * <br/>开发时间：2022-07-02
 * <br/>版本：1.1.0
 * <br/>JDK版本：1.8
 * <br/>========================================
 */
public class DalManager {

    /**
     * 类: 配置文件
     * 简单的不能再简单的开发模式：使用静态内部类，简化很多类结构和关系
     * 使用范围：多参数赋值
     */
    private static class Config {
        private static final String ROOT = "configs";
        private static final String CLUSTER_CLIENT_CONFS = "clusterclientconfs";
        private final static String CLUSTER_CLIENT_CONF = "clusterclientconf";
        private final static String DATABASE_CONFS = "databaseconfs";
        final private static String DATABASE_CONF = "databaseconf";
        final private static String DATASET_CONFS = "datasetconfs";
        final private static String DATASET_CONF = "datasetconf";
    }

    /**
     * 类: 数据库配置
     * 使用范围：多参数赋值
     */
    private static class DatabaseConfig {
        private static final String ROOT = "databases";
        private static final String DATABASE = "database";
        private static final String DATABASE_NAME = "name";
        private static final String DATABASE_ISVIRTUAL = "isvirtual";
        final private static String DATABASE_CLASS = "class";
        final private static String DATABASE_CLIENT = "client";
    }

    /**
     * 类: 集群配置
     * 使用范围：多参数赋值
     */
    private static class ClusterConfig {
        private static final String ROOT = "clusterclients";
        private final static String CLUSTER_CLIENT = "clusterclient";
        private static final String NAME = "name";
        private static final String TYPE = "type";
        private static final String CLASS = "class";
        private static final String CLIENT_SERVERS = "servers";
        private static final String CLIENT_SERVER = "server";
        private static final String SERVER_NAME = "name";
        private static final String SERVER_ID = "id";
        private static final String SERVER_AUTH = "auth";
        private static final String SERVER_POLICY = "policy";
        private static final String SERVER_FSYNC = "fsync";
        private static final String SERVER_HOST = "host";
        private static final String SERVER_PORT = "port";
        private static final String SERVER_URL = "url";
        private static final String SERVER_DRIVER = "driver";
        private static final String SERVER_USER = "user";
        private static final String SERVER_PASS = "pass";
        private static final String SERVER_SCHEMA = "schema";
        private static final String SERVER_TIMEOUT = "timeout";
    }

    /**
     * 类: 结果集配置
     * 使用范围：多参数赋值
     */
    private static class DatasetConfig {
        private static final String ROOT = "packages";
        private static final String PACKAGE = "package";
        private static final String PACKAGE_NAME = "name";
        private static final String DATASET = "dataset";
        private static final String DS_DB = "db";
        private static final String DS_DATACLASS = "dataclass";
        private static final String DS_NAME = "name";
        private static final String DS_ISCACHE = "iscache";
        private static final String DS_CLASS = "class";
        private static final String COMTEMPLATES = "comtemplates";
        private static final String COMTEMPLATE = "comtemplate";
        private static final String COMTEMPLATE_NAME = "name";
        private static final String COMTEMPLATE_TYPE = "type";
        private static final String FORMAT = "format";
        private static final String FORMATNAME = "name";
        private static final String FORMATPARANUM = "paranum";
    }

    /**
     * 各个不同数据库所表示的type标识
     */
    private static class DBType {
        private static final String MYSQL_TYPE = "1";
        private static final String MONGODB_TYPE = "2";
        private static final String POSTGRESQL_TYPE = "3";
    }

    /**
     * 服务器集群客户端容器
     */
    private Map<String, BaseClusterClient> clients = new LinkedHashMap<>();
    /**
     * 数据库集群容器
     */
    private Map<String, AbstractDatabase> databases = new LinkedHashMap<>();
    /**
     * 数据结果集容器
     */
    private Map<String, DataSet> dataSets = new LinkedHashMap<>();
    /**
     * 数据结果集对象容器
     */
    private Map<String, Entity> dataClasses = Collections.synchronizedMap(new LinkedHashMap<>());
    /**
     * 数据访问层管理单例
     */
    private static volatile DalManager dalManager = null;

    private static final Object SYNC_OBJECT = new Object();

    private static volatile boolean bInit = false;

    /**
     * 单例: 懒汉模式
     * @return DalManager
     */
    public static DalManager getInstance() {
        if (dalManager == null) {
            synchronized (SYNC_OBJECT) {
                if (dalManager == null) {
                    dalManager = new DalManager();
                }
            }
        }
        return dalManager;
    }

    private DalManager() {}


    public static void init(String configFile) {
        init(configFile, false);
    }

    public static void init(String configFile, boolean isAbsolute) {
        init(configFile, isAbsolute, null);
    }

    public static void init(String configFile, boolean isAbsolute, Class<?> clazz) {
        init(configFile, isAbsolute, null, clazz);
    }

    /**
     * 初始化数据访问储存服务，从配置文件中加载所有实例化的数据库和结果集对象
     * @param configFile 配置文件
     * @param isAbsolutePath 是否绝对路径
     * @param basePath 文件基本路径
     * @param loader 已加载的类
     */
    public synchronized static void init(String configFile, boolean isAbsolutePath, String basePath, Class<?> loader) {

        if (bInit) {
            return;
        }

        if (Assert.isEmpty(configFile)) {
            throw new DalException("configFile must not be null");
        }

        String fileDir = "";
        if (isAbsolutePath) {
            if (Assert.isEmpty(basePath)) {
                File file = new File(configFile);
                fileDir = file.getParent();
            } else {
                fileDir = basePath;
            }
        }

        InputStream in;
        try {
            in = locateFromClasspath(configFile, isAbsolutePath, loader);
        } catch (IOException e) {
            throw new DalException(e.getMessage(), e);
        }

        try {
            // 读取主配置文件加载所有子配置文件
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder db;
            db = factory.newDocumentBuilder();
            Document doc;
            doc = db.parse(in);
            doc.normalize();

            // 获取XML根节点列表
            NodeList rootNodes = doc.getElementsByTagName(Config.ROOT);

            if (rootNodes.getLength() != 1) {
                throw new DalException("config must has its rootNode, pls check it");
            }

            // 获取根节点的子节点列表
            NodeList confNodes = rootNodes.item(0).getChildNodes();

            List<String> clientConfs = new ArrayList<>(12);
            List<String> databaseConfs = new ArrayList<>(12);
            List<String> dataSetConfs = new ArrayList<>(32);

            Node confNode;
            String confNodeName;
            NodeList childNodes;
            Node confChildNode;

            for (int n = 0; n < confNodes.getLength(); n++) {
                confNode = confNodes.item(n);
                if (confNode.getNodeType() != Node.ELEMENT_NODE) {
                    continue;
                }
                confNodeName = confNode.getNodeName();
                childNodes = confNode.getChildNodes();

                switch (confNodeName) {
                    case Config.CLUSTER_CLIENT_CONFS:
                        for (int i = 0; i < childNodes.getLength(); i++) {
                            confChildNode = childNodes.item(i);
                            if (confChildNode.getNodeType() != Node.ELEMENT_NODE) {
                                continue;
                            }
                            if (Config.CLUSTER_CLIENT_CONF.equals(confChildNode.getNodeName())) {
                                clientConfs.add(confChildNode.getTextContent());
                            } else {
                                System.out.printf("configure file has no supported node %s \r\n", confChildNode.getNodeName());
                                throw new DalException("configure file has no supported node %s \r\n" + confChildNode.getNodeName());
                            }
                        }
                        break;
                    case Config.DATABASE_CONFS:
                        for (int i = 0; i < childNodes.getLength(); i++) {
                            confChildNode = childNodes.item(i);
                            if (confChildNode.getNodeType() != Node.ELEMENT_NODE) {
                                continue;
                            }
                            if (Config.DATABASE_CONF.equals(confChildNode.getNodeName())) {
                                databaseConfs.add(confChildNode.getTextContent());
                            } else {
                                System.out.printf("configure file has no supported node %s \r\n", confChildNode.getNodeName());
                                throw new DalException("configure file has no supported node %s \r\n" + confChildNode.getNodeName());
                            }
                        }
                        break;
                    case Config.DATASET_CONFS:
                        for (int i = 0; i < childNodes.getLength(); i++) {
                            confChildNode = childNodes.item(i);
                            if (confChildNode.getNodeType() != Node.ELEMENT_NODE) {
                                continue;
                            }
                            if (Config.DATASET_CONF.equals(confChildNode.getNodeName())) {
                                dataSetConfs.add(confChildNode.getTextContent());
                            } else {
                                System.out.printf("configure file has no supported node %s \r\n", confChildNode.getNodeName());
                                throw new DalException("configure file has no supported node %s \r\n" + confChildNode.getNodeName());
                            }

                        }
                        break;
                    default:
                        System.out.printf("configure file has no supported node %s \r\n", confNodeName);
                        throw new DalException("configure file has no supported node %s \r\n" + confNodeName);
                }
            }

            // 创建数据存储管理实例
            dalManager = DalManager.getInstance();
            // 加载客户集群
            for (String conf : clientConfs) {
                if (isAbsolutePath) {
                    conf = String.format("%s/%s", fileDir, conf);
                }
                if (dalManager.loadClients(conf, isAbsolutePath, loader) != 0) {
                    Logger.error("loadClients failed, configure file has no supported node");
                    throw new DalException("loadClients failed, configure file has no supported node");
                }
            }
            // 加载每一个配置文件下的database
            for (String conf : databaseConfs) {
                if (isAbsolutePath) {
                    conf = String.format("%s/%s", fileDir, conf);
                }
                if (dalManager.loadDataBases(conf, isAbsolutePath, loader) != 0) {
                    Logger.error("loadDataBases failed, configure file has no supported node");
                    throw new DalException("loadDataBases failed, configure file has no supported node");
                }
            }
            // 加载所有的数据集配置文件
            for (String conf : dataSetConfs) {
                if (isAbsolutePath) {
                    conf = String.format("%s/%s", fileDir, conf);
                }
                if (dalManager.loadDataSets(conf, isAbsolutePath, loader) != 0) {
                    Logger.error("loadDataSets failed, configure file has no supported node");
                    throw new DalException("loadDataSets failed, configure file has no supported node");
                }
            }
            // 客户端准备工作
            dalManager.toBeReady();
            bInit = true;
        } catch (ParserConfigurationException | SAXException | IOException e) {
            Logger.error("DalManager load instance from configs Error, [Error msg]: %s", e);
            throw new DalException("DalManager load instance from configs Error, [Error msg]: %s", e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        }
    }

    /**
     * 获取资源，返回字节流
     * @param resourceName 资源名称
     * @param isAbsolutePath 是否绝对路径
     * @param loader Class
     * @return InputStream 字节流
     * @throws IOException IO异常
     * 注解: 抑制所有类型的警告
     */
    @SuppressWarnings("all")
    private static InputStream locateFromClasspath(String resourceName, boolean isAbsolutePath, Class<?> loader)
            throws IOException {
        InputStream input = null;
        if (isAbsolutePath) {
            try (FileInputStream fis = new FileInputStream(resourceName)) {
                long size = fis.getChannel().size();
                byte[] buffer = new byte[(int) size];
                fis.read(buffer, 0, (int) size);
                input = new ByteArrayInputStream(buffer);
            } catch (Exception ex) {
                Logger.error("locateFromClasspath, something wrong has happened: %s", ex.getMessage(), ex);
                throw new IOException("IOException has happened ... ");
            }
        } else {
            if (loader != null) {
                input = loader.getResourceAsStream(resourceName);
            }
            if (input == null) {
                ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
                input = classLoader.getResourceAsStream(resourceName);
            }
            if (input == null) {
                input = ClassLoader.getSystemResourceAsStream(resourceName);
            }
            if (input == null) {
                throw new FileNotFoundException(resourceName + " cannot be opened because it does not exist");
            }
        }
        return input;
    }

    /**
     * 加载客户端集群器
     * @param configFilePath 配置文件路径
     * @param isAbsolutePath 是否绝对路径
     * @param loader Class类
     * @return 整型，0表示成功
     */
    private int loadClients(String configFilePath, boolean isAbsolutePath, Class<?> loader) {
        assert !Assert.isEmpty(configFilePath);
        try {
            InputStream input = locateFromClasspath(configFilePath, isAbsolutePath, loader);
            assert input != null;
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(input);
            doc.normalize();

            NodeList rootNodes = doc.getElementsByTagName(ClusterConfig.ROOT);
            if (rootNodes == null || rootNodes.getLength() != 1) {
                Logger.error("rootNodes is null or more than one, please check your clusterClient config file");
                return 1;
            }

            // 多个clusterClient集合
            NodeList confNodes = rootNodes.item(0).getChildNodes();
            if (confNodes == null || confNodes.getLength() == 0) {
                Logger.error("clusterClient config file has no element 'clusterclient', please check your clusterClient config file");
                return 1;
            }

            int length = 0;
            // 单个clusterClient
            Node confNode;
            // 单个clusterClient的元素名称，value is: clusterClient
            String confNodeName;
            // 多个servers元素集合，实际上只能存在1个
            NodeList childNodes;
            // 单个servers元素
            Node confChildNode;
            // 多个server元素集合
            NodeList subNodes;
            // 单个server
            Node confSubNode;

            for (; length < confNodes.getLength(); ) {
                confNode = confNodes.item(length++);
                if (confNode.getNodeType() != Node.ELEMENT_NODE) {
                    continue;
                }
                confNodeName = confNode.getNodeName();
                childNodes = confNode.getChildNodes();

                if (ClusterConfig.CLUSTER_CLIENT.equals(confNodeName)) {
                    NamedNodeMap attributes = confNode.getAttributes();
                    if (attributes == null || attributes.getLength() == 0) {
                        Logger.error("element clusterClient has no attributes, pls check it");
                        return 1;
                    }
                    if (childNodes == null || childNodes.getLength() != 1) {
                        Logger.error("serversNodes is null or more than one, please check your clusterClient config file");
                        return 1;
                    }

                    Node clientNameNode;
                    Node clientTypeNode;
                    Node clientClassNode;
                    String clientName;
                    String clientType;
                    String clientClass;


                    BaseClusterClient client;

                    if ((clientNameNode = attributes.getNamedItem(ClusterConfig.NAME)) == null
                            || (clientTypeNode = attributes.getNamedItem(ClusterConfig.TYPE)) == null
                            || (clientClassNode = attributes.getNamedItem(ClusterConfig.CLASS)) == null
                            || Assert.isEmpty(clientName = clientNameNode.getNodeValue())
                            || Assert.isEmpty(clientType = clientTypeNode.getNodeValue())
                            || Assert.isEmpty(clientClass = clientClassNode.getNodeValue())) {
                        Logger.error("element clusterClient must has name、type and class attribute");
                        return 1;
                    }
                    try {
                        client = (BaseClusterClient) Class.forName(String.format("%s%s%s", DalManager.class.getPackage().getName(), ".", clientClass)).newInstance();
                    } catch (ClassNotFoundException | IllegalAccessException | InstantiationException ex) {
                        Logger.error("cannot initialize BaseClusterClient instance, exception: %s", ex.getMessage(), ex);
                        return 1;
                    }
                    client.setName(clientName);
                    client.setType(clientType);
                    client.setClientClass(clientClass);
                    addClient(client);

                    if (this.addClient(client) != 0) {
                        Logger.error("cannot put cluster client into container, the name has existed: %s", clientName);
                        return 1;
                    }

                    // 1个servers元素，且不为空
                    confChildNode = childNodes.item(0);
                    if (confChildNode.getNodeType() != Node.ELEMENT_NODE) {
                        continue;
                    }
                    if (ClusterConfig.CLIENT_SERVERS.equals(confChildNode.getNodeName())) {
                        if ((subNodes = confChildNode.getChildNodes()) == null
                                || subNodes.getLength() == 0) {
                            Logger.warn("couldn't find any server, it means no cluster client will be created");
                            continue;
                        }

                        int len = 0,totalLength = subNodes.getLength();

                        NamedNodeMap serverAttrMap;
                        Node serverNameNode;
                        Node serverIdNode;
                        Node serverPolicyNode;
                        Node serverFsyncNode;
                        Node serverHostNode;
                        Node serverPortNode;

                        Node serverUrlNode;
                        Node serverDriverNode;
                        Node serverUserNode;
                        Node serverSchemaNode;
                        Node serverPassNode;

                        Node serverAuthNode;
                        Node serverTimeoutNode;

                        String serverName;
                        String serverId;
                        String serverPolicy;
                        String serverFsync;
                        String serverHost;
                        String serverPort;

                        String serverUrl;
                        String serverDriver;
                        String serverUser;
                        String serverSchema;
                        String serverPass;

                        while (len < totalLength) {
                            confSubNode = subNodes.item(len++);
                            if (confSubNode == null || confSubNode.getNodeType() != Node.ELEMENT_NODE) {
                                continue;
                            }
                            if (ClusterConfig.CLIENT_SERVER.equals(confSubNode.getNodeName())) {
                                serverAttrMap = confSubNode.getAttributes();
                                if (serverAttrMap == null || serverAttrMap.getLength() == 0) {
                                    continue;
                                }
                                // 当type = 1时，表示关系型数据库
                                if (DBType.MYSQL_TYPE.equals(clientType)) {
                                    if ((serverIdNode = serverAttrMap.getNamedItem(ClusterConfig.SERVER_ID)) != null
                                            && !Assert.isEmpty(serverId = serverIdNode.getNodeValue())
                                            && (serverUrlNode = serverAttrMap.getNamedItem(ClusterConfig.SERVER_URL)) != null
                                            && !Assert.isEmpty(serverUrl = serverUrlNode.getNodeValue())
                                            && (serverDriverNode = serverAttrMap.getNamedItem(ClusterConfig.SERVER_DRIVER)) != null
                                            && !Assert.isEmpty(serverDriver = serverDriverNode.getNodeValue())
                                            && (serverUserNode = serverAttrMap.getNamedItem(ClusterConfig.SERVER_USER)) != null
                                            && !Assert.isEmpty(serverUser = serverUserNode.getNodeValue())
                                            && (serverPassNode = serverAttrMap.getNamedItem(ClusterConfig.SERVER_PASS)) != null
                                            && !Assert.isEmpty(serverPass = serverPassNode.getNodeValue())) {
                                        DbServer.MysqlServer build = new DbServer.MysqlServer(serverUrl, serverDriver, serverUser, serverPass);
                                        DbServer dbServer = build.withSid(Integer.parseInt(serverId)).build();
                                        client.addServer(dbServer);
                                    } else {
                                        System.out.printf("Attribute lost in node %s \r\n", confNodeName);
                                        Logger.error("Attribute lost in node %s \r\n", confNodeName);
                                        return 1;
                                    }
                                } else {
                                    // 非关系型数据库
                                    if (((serverNameNode = serverAttrMap.getNamedItem(ClusterConfig.SERVER_NAME)) != null)
                                            && ((serverHostNode = serverAttrMap.getNamedItem(ClusterConfig.SERVER_HOST)) != null)
                                            && ((serverPortNode = serverAttrMap.getNamedItem(ClusterConfig.SERVER_PORT)) != null)
                                            && ((serverName = serverNameNode.getNodeValue()) != null)
                                            && ((serverHost = serverHostNode.getNodeValue()) != null)
                                            && ((serverPort = serverPortNode.getNodeValue()) != null)) {

                                        DbServer dbServer;
                                        DbServer.NosqlServer build = new DbServer.NosqlServer(serverHost, Integer.parseInt(serverPort), serverName);

                                        if(((serverIdNode = serverAttrMap.getNamedItem(ClusterConfig.SERVER_ID))   != null)
                                                && ((serverPolicyNode = serverAttrMap.getNamedItem(ClusterConfig.SERVER_POLICY))  != null)
                                                && ((serverFsyncNode = serverAttrMap.getNamedItem(ClusterConfig.SERVER_FSYNC))  != null)
                                                && ((serverUserNode = serverAttrMap.getNamedItem(ClusterConfig.SERVER_USER)) != null)
                                                && ((serverSchemaNode = serverAttrMap.getNamedItem(ClusterConfig.SERVER_SCHEMA)) != null)
                                                && ((serverPassNode = serverAttrMap.getNamedItem(ClusterConfig.SERVER_PASS)) != null)
                                                && ((serverId = serverIdNode.getNodeValue())   != null)
                                                && ((serverPolicy = serverPolicyNode.getNodeValue())!= null)
                                                && ((serverFsync = serverFsyncNode.getNodeValue())!= null)
                                                && ((serverUser = serverUserNode.getNodeValue()) != null)
                                                && ((serverSchema = serverSchemaNode.getNodeValue()) != null)
                                                && ((serverPass = serverPassNode.getNodeValue()) != null)) {

                                            dbServer = build.withSid(Integer.parseInt(serverId))
                                                    .withPolicy(Integer.parseInt(serverPolicy))
                                                    .withFsync(Boolean.parseBoolean(serverFsync))
                                                    .withUser(serverUser)
                                                    .withSchema(serverSchema)
                                                    .withPass(serverPass)
                                                    .build();

                                        } else if (((serverIdNode   = serverAttrMap.getNamedItem(ClusterConfig.SERVER_ID))   != null)
                                                && ((serverPolicyNode  = serverAttrMap.getNamedItem(ClusterConfig.SERVER_POLICY))  != null)
                                                && ((serverFsyncNode  = serverAttrMap.getNamedItem(ClusterConfig.SERVER_FSYNC))  != null)
                                                && ((serverId   = serverIdNode.getNodeValue())   != null)
                                                && ((serverPolicy = serverPolicyNode.getNodeValue())!= null)
                                                && ((serverFsync = serverFsyncNode.getNodeValue())!= null)) {

                                            dbServer = build.withSid(Integer.parseInt(serverId))
                                                    .withPolicy(Integer.parseInt(serverPolicy))
                                                    .withFsync(Boolean.parseBoolean(serverFsync))
                                                    .build();
                                        } else {
                                            dbServer = build.build();
                                        }
                                        client.addServer(dbServer);
                                    } else {
                                        System.out.printf("Attribute lost in node %s \r\n", confNodeName);
                                        Logger.error("Attribute lost in node %s", confNodeName);
                                        return 1;
                                    }
                                }
                            }
                        }
                    } else {
                        System.out.printf("configure file has no supported node %s \r\n", confChildNode.getNodeName());
                        Logger.error("configure file has no supported node %s", confChildNode.getNodeName());
                        return 1;
                    }
                } else {
                    System.out.printf("configure file has no supported node %s \r\n", confNodeName);
                    Logger.error("configure file has no supported node %s", confNodeName);
                    return 1;
                }
            }
            return 0;
        } catch (ParserConfigurationException | SAXException | IOException e) {
            Logger.error("loadClients, something has unexpected, errorMsg: %s", e.getMessage(), e);
            return 1;
        }
    }

    /**
     * 加载数据库
     * @param configFilePath 数据库配置文件路径
     * @param isAbsolutePath 是否绝对路径
     * @param classLoader Class 类
     * @return 整型 0表示OK
     */
    private int loadDataBases(String configFilePath, boolean isAbsolutePath, Class<?> classLoader) {
        assert !StringUtils.isBlank(configFilePath);
        try {
            InputStream in = locateFromClasspath(configFilePath, isAbsolutePath, classLoader);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder db;
            db = factory.newDocumentBuilder();
            Document doc = db.parse(in);
            doc.normalize();

            NodeList rootNodes = doc.getElementsByTagName(DatabaseConfig.ROOT);
            if (rootNodes.getLength() != 1) {
                return 1;
            }

            NodeList confNodes = rootNodes.item(0).getChildNodes();

            Node confNode;
            String confNodeName;

            for (int n = 0; n < confNodes.getLength(); n++) {
                confNode = confNodes.item(n);
                if (confNode.getNodeType() != Node.ELEMENT_NODE) {
                    continue;
                }

                confNodeName = confNode.getNodeName();

                if (DatabaseConfig.DATABASE.equals(confNodeName)) {
                    NamedNodeMap dbAttrMap = confNode.getAttributes();
                    Node dbNameNode;
                    Node dbClassNode;
                    Node isVirtualNode;
                    Node clientNode;
                    String dbName;
                    String dbClass;
                    String clientName;
                    String isVirtual;
                    AbstractDatabase database;

                    if (dbAttrMap != null
                            && (dbNameNode = dbAttrMap.getNamedItem(DatabaseConfig.DATABASE_NAME)) != null
                            && (dbClassNode = dbAttrMap.getNamedItem(DatabaseConfig.DATABASE_CLASS)) != null
                            && (isVirtualNode = dbAttrMap.getNamedItem(DatabaseConfig.DATABASE_ISVIRTUAL)) != null
                            && (clientNode = dbAttrMap.getNamedItem(DatabaseConfig.DATABASE_CLIENT)) != null
                            && ((dbName = dbNameNode.getNodeValue()) != null)
                            && ((dbClass = dbClassNode.getNodeValue()) != null)
                            && ((clientName = clientNode.getNodeValue()) != null)
                            && ((isVirtual = isVirtualNode.getNodeValue()) != null)) {
                        try {
                            database = (AbstractDatabase) Class.forName(DalManager.class.getPackage().getName() + "." + dbClass).newInstance();
                            database.setName(dbName);
                            this.addDatabase(database);
                        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException classNotFoundException) {
                            Logger.error("could not new instance for %s", dbClass);
                            return 1;
                        }

                        if ("0".equals(isVirtual) || "false".equals(isVirtual)) {
                            BaseClusterClient client = this.getClient(clientName);
                            if (client == null || !(database instanceof AbstractPhysicDatabase)) {
                                System.out.printf("Error in DB(%s) configure, failed to find client %s \r\n", dbName, clientName);
                                Logger.error("Error in DB(%s) configure, failed to find client %s \r\n", dbName, clientName);
                                return 1;
                            }
                            ((AbstractPhysicDatabase) database).setClient(client);
                        }

                    } else {
                        System.out.printf("configure file has no supported node %s \r\n", confNodeName);
                        Logger.error("Configure file has no supported node %s", confNodeName);
                    }
                } else {
                    System.out.printf("configure file has no supported node %s \r\n", confNodeName);
                    Logger.error("Configure file has no supported node %s", confNodeName);
                    return 1;
                }
            }
            return 0;
        } catch (IOException | ParserConfigurationException | SAXException exception) {
            Logger.error("Fail to load database, errorMsg: %s", exception.getMessage(), exception);
            return 1;
        }
    }


    /**
     * 加载数据集
     * @param configFilePath 数据集配置文件路径
     * @param isAbsolutePath 是否绝对路径
     * @param classLoader Class 类
     * @return 0 means ok
     */
    private int loadDataSets(String configFilePath, boolean isAbsolutePath, Class<?> classLoader) {
        assert !StringUtils.isBlank(configFilePath);
        try {
            InputStream in = locateFromClasspath(configFilePath, isAbsolutePath, classLoader);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder db;
            db = factory.newDocumentBuilder();
            Document doc;
            doc = db.parse(in);
            doc.normalize();

            NodeList rootNodes = doc.getElementsByTagName(DatasetConfig.ROOT);

            if (rootNodes.getLength() != 1) {
                return 1;
            }

            NodeList confNodes = rootNodes.item(0).getChildNodes();

            Node confNode;
            String confNodeName;
            NodeList childNodes;
            Node confChildNode;
            NodeList subNodes;
            Node confsubNode;
            NodeList sub2Nodes;
            Node confsub2Node;
            NodeList sub3Nodes;
            Node confsub3Node;

            for (int n =0; n < confNodes.getLength(); n++) {
                confNode = confNodes.item(n);
                if (confNode.getNodeType() != Node.ELEMENT_NODE) {
                    continue;
                }

                confNodeName = confNode.getNodeName();
                childNodes = confNode.getChildNodes();

                if (DatasetConfig.PACKAGE.equals(confNodeName)) {
                    NamedNodeMap packageAttrMap = confNode.getAttributes();
                    Node packageNameNode;
                    String packageName;
                    if ((packageAttrMap != null)
                            && (packageNameNode = packageAttrMap.getNamedItem(DatasetConfig.PACKAGE_NAME)) != null
                            && (packageName = packageNameNode.getNodeValue()) != null) {
                        for (int i = 0; i < childNodes.getLength(); i++) {
                            confChildNode = childNodes.item(i);
                            if (confChildNode.getNodeType() != Node.ELEMENT_NODE) {
                                continue;
                            }
                            if (DatasetConfig.DATASET.equals(confChildNode.getNodeName())) {
                                NamedNodeMap datasetAttrMap = confChildNode.getAttributes();
                                Node dsDbNode;
                                Node dsDataClassNode;
                                Node dsClassNode;
                                Node dsNameNode;
                                Node dsIsCacheNode;
                                String dsName;
                                String dsIsCache;
                                String dsDb;
                                String dsDataClass;
                                String dsClass;
                                DataSet dataSet;
                                AbstractDatabase abstractDatabase;
                                Entity dataClass;

                                if ((datasetAttrMap != null)
                                        && ((dsDbNode = datasetAttrMap.getNamedItem(DatasetConfig.DS_DB)) != null)
                                        && ((dsDataClassNode = datasetAttrMap.getNamedItem(DatasetConfig.DS_DATACLASS)) != null)
                                        && ((dsClassNode = datasetAttrMap.getNamedItem(DatasetConfig.DS_CLASS)) != null)
                                        && ((dsNameNode = datasetAttrMap.getNamedItem(DatasetConfig.DS_NAME)) != null)
                                        && ((dsIsCacheNode = datasetAttrMap.getNamedItem(DatasetConfig.DS_ISCACHE)) != null)
                                        && ((dsDb = dsDbNode.getNodeValue()) != null)
                                        && ((dsDataClass = dsDataClassNode.getNodeValue()) != null)
                                        && ((dsClass = dsClassNode.getNodeValue()) != null)
                                        && ((dsName = dsNameNode.getNodeValue()) != null)
                                        && ((dsIsCache = dsIsCacheNode.getNodeValue()) != null)) {
                                    abstractDatabase = this.getDataBase(dsDb);
                                    if (abstractDatabase == null) {
                                        System.out.printf("Fail to find db(%s) in dataset(%s) configure \r\n", dsDb, dsDataClass);
                                        return 1;
                                    }
                                    dataClass = this.getDataClass(packageName + dsDataClass);
                                    if (dataClass == null) {
                                        // 测试可删
                                        dataClass = new Entity();
                                        if (dataClass.loadClass(packageName, dsDataClass) != 0) {

                                        }
                                    }
                                }

                            } else {
                                System.out.printf("configure file has no supported node %s \r\n", confNodeName);
                                Logger.error("Configure file has no supported node %s", confNodeName);
                                return 1;
                            }
                        }
                    } else {
                        System.out.printf("Attribute lost in node %s \r\n", confNodeName);
                        return 1;
                    }

                } else {
                    System.out.printf("configure file has no supported node %s \r\n", confNodeName);
                    Logger.error("Configure file has no supported node %s", confNodeName);
                    return 1;
                }
            }


        } catch (IOException | ParserConfigurationException | SAXException ioException) {
            ioException.printStackTrace();
        }

        // 测试可删
        return 0;
    }



    /**
     * 添加集群客户端
     * @param client BaseClusterClient
     * @return int，0表示插入成功;
     */
    public int addClient(BaseClusterClient client) {
        if (this.clients.get(client.getName()) == null) {
            this.clients.put(client.getName(), client);
            return 0;
        }
        return 1;
    }

    /**
     * 根据指定的集群客户端名称，获取集群客户端
     * @param clientName 客户端名
     * @return 集群客户端抽象类
     */
    public BaseClusterClient getClient(String clientName) {
        return this.clients.get(clientName);
    }

    /**
     * 添加集群数据库
     * @param database 抽象数据库对象
     * @return 0表示成功；1表示失败
     */
    public int addDatabase(AbstractDatabase database) {
        if (this.databases.get(database.getName()) == null) {
            this.databases.put(database.getName(), database);
            return 0;
        }
        return 1;
    }

    /**
     * 根据数据库名称获取指定的数据库
     * @param dbName 数据库名称
     * @return 数据库对象
     */
    public AbstractDatabase getDataBase(String dbName) {
        return this.databases.get(dbName);
    }


    /**
     * 添加数据类对象到缓存中
     * @param dataClass 数据类对象
     * @return 成功返回0，反之：1
     */
    public int addDataClass(Entity dataClass) {
        String name = dataClass.getFullName();
        if (dataClasses.get(name) == null) {
            dataClasses.put(name, dataClass);
            return 0;
        }
        return 1;
    }

    /**
     * 移除数据类对象
     * @param dataClass 数据类对象
     * @return succeed means zero, otherwise one
     */
    public int removeDataClass(Entity dataClass) {
        if (dataClass != null) {
            String dataClassName = dataClass.getFullName();
            if (dataClassName != null) {
                dataClasses.remove(dataClassName);
                return 0;
            }
        }
        return 1;
    }

    /**
     * 根据类文件完整名称获取数据类对象
     * @param classFullName 类文件完整路径名称
     * @return 数据类文件对象
     */
    public Entity getDataClass(String classFullName) {
        return this.dataClasses.get(classFullName);
    }

    /**
     * 客户端准备工作
     */
    private void toBeReady() {
        for (Entry<String, BaseClusterClient> entry : this.clients.entrySet()) {
            BaseClusterClient client = entry.getValue();
            if (client.toBeReady() != 0) {
                Logger.error("Client %s cannot be ready \r\n", client.getName());
                throw new DalException("Client %s cannot be ready \r\n" + client.getName());
            }
        }
    }
}
