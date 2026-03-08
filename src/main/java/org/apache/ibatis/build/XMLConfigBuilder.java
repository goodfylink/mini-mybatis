package org.apache.ibatis.build;


import org.apache.ibatis.datasource.DataSourceFactory;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.parsing.XPathParser;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.transaction.TransactionFactory;

import javax.sql.DataSource;
import java.io.InputStream;
import java.io.Reader;
import java.util.Properties;

/**
 * </p>
 *
 * @author jcyin
 * @since 2026/2/21
 * </p>
 */
public class XMLConfigBuilder extends BaseBuilder {
    private boolean parsed;
    private final XPathParser parser;
    private String environment;

    public XMLConfigBuilder(Reader reader, String environment, Properties props) {
        super(newConfig(Configuration.class));
        this.configuration.setVariables(props);
        this.environment = environment;
        this.parsed = false;
        this.parser = new XPathParser(reader);
    }

    public Configuration parse() {
        if (parsed) {
            throw new BuilderException("Each XMLConfigBuilder can only be used once.");
        }
        parsed = true;
        parseConfiguration(parser.evalNode("/configuration"));
        return configuration;
    }

    private void parseConfiguration(XNode root) {
        try {

            environmentsElement(root.evalNode("environments"));
            mappersElement(root.evalNode("mappers"));
        }catch (Exception e){
            throw new BuilderException("Error parsing SQL Mapper Configuration. Cause: " + e, e);
        }
    }

    private void environmentsElement(XNode context) throws Exception {
        if (context == null) {
            return;
        }
        if (environment == null) {
            environment = context.getStringAttribute("default");
        }
        for (XNode child : context.getChildren()) {
            String id = child.getStringAttribute("id");
            if (isSpecifiedEnvironment(id)) {
                TransactionFactory txFactory = transactionManagerElement(child.evalNode("transactionManager"));
                DataSourceFactory dsFactory = dataSourceElement(child.evalNode("dataSource"));
                DataSource dataSource = dsFactory.getDataSource();
                Environment.Builder environmentBuilder = new Environment.Builder(id).transactionFactory(txFactory)
                        .dataSource(dataSource);
                configuration.setEnvironment(environmentBuilder.build());
                break;
            }
        }
    }

    private void mappersElement(XNode context) throws Exception {
        if (context == null) {
            return;
        }
        for (XNode child : context.getChildren()) {

            if ("package".equals(child.getName())) {
                continue;
            }

            // 2) 只处理 resource / class 两种写法
            String resource = child.getStringAttribute("resource");
            String mapperClass = child.getStringAttribute("class");

            // 情况 A：XML mapper 文件
            if (resource != null && mapperClass == null) {
                try (InputStream inputStream = Resources.getResourceAsStream(resource)) {
                    // 如果你的 XMLMapperBuilder 是 InputStream 构造：
                    XMLMapperBuilder mapperParser = new XMLMapperBuilder(inputStream, configuration, resource);
                    mapperParser.parse();
                }
                continue;
            }

            // 情况 B：直接注册 Mapper 接口（不需要 XML）
            if (resource == null && mapperClass != null) {
                Class<?> mapperInterface = Resources.classForName(mapperClass);
                configuration.addMapper(mapperInterface);
                continue;
            }

            // 其它组合一律认为是配置错误
            throw new BuilderException(
                    "A mapper element must specify either a 'resource' or 'class' attribute, but not both.");
        }
    }

        private TransactionFactory transactionManagerElement(XNode context) throws Exception {
        if (context != null) {
            String type = context.getStringAttribute("type");
            Properties props = context.getChildrenAsProperties();
            TransactionFactory factory = (TransactionFactory) resolveClass(type).getDeclaredConstructor().newInstance();
            factory.setProperties(props);
            return factory;
        }
        throw new BuilderException("Environment declaration requires a TransactionFactory.");
    }

    private DataSourceFactory dataSourceElement(XNode context) throws Exception {
        if (context != null) {
            String type = context.getStringAttribute("type");
            Properties props = context.getChildrenAsProperties();
            DataSourceFactory factory = (DataSourceFactory) resolveClass(type).getDeclaredConstructor().newInstance();
            factory.setProperties(props);
            return factory;
        }
        throw new BuilderException("Environment declaration requires a DataSourceFactory.");
    }


    private boolean isSpecifiedEnvironment(String id) {
        return environment.equals(id);
    }

        private static Configuration newConfig(Class<? extends Configuration> configClass) {
        try {
            return configClass.getDeclaredConstructor().newInstance();
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
