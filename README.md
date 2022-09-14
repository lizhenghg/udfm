udfm：数据库统一orm（非orm）框架管理，实现类似mybatis的功能，但比mybatis更高级。执行同一条sql，可以同时满足在mysql、mongodb、postgreSql数据库运行。封装了基于xml配置的pojo


技术原理：
基于java的反射 + 动态代理 + 零copy + 连接池 + 自定义xml pojo，实现基本实用的orm(非orm)框架功能


运行环境：
jdk1.8