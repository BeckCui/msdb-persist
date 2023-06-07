package com.dhl.fin.api.common.mybatisgenerator;

import org.mybatis.generator.config.*;
import org.mybatis.generator.config.xml.MyBatisGeneratorConfigurationParser;
import org.mybatis.generator.exception.XMLParserException;
import org.mybatis.generator.internal.ObjectFactory;
import org.mybatis.generator.internal.util.StringUtility;
import org.mybatis.generator.internal.util.messages.Messages;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * @author CuiJianbo
 * @since 2023/6/7
 */
public class DhlMyBatisGeneratorConfigurationParser {
    private Properties extraProperties;
    private Properties configurationProperties;

    public DhlMyBatisGeneratorConfigurationParser(Properties extraProperties) {
        if (extraProperties == null) {
            this.extraProperties = new Properties();
        } else {
            this.extraProperties = extraProperties;
        }

        this.configurationProperties = new Properties();
    }

    public Configuration parseConfiguration(Element rootNode) throws XMLParserException {
        Configuration configuration = new Configuration();
        NodeList nodeList = rootNode.getChildNodes();

        for(int i = 0; i < nodeList.getLength(); ++i) {
            Node childNode = nodeList.item(i);
            if (childNode.getNodeType() == 1) {
                if ("properties".equals(childNode.getNodeName())) {
                    this.parseProperties(childNode);
                } else if ("classPathEntry".equals(childNode.getNodeName())) {
                    this.parseClassPathEntry(configuration, childNode);
                } else if ("context".equals(childNode.getNodeName())) {
                    this.parseContext(configuration, childNode);
                }
            }
        }

        return configuration;
    }

    protected void parseProperties(Node node) throws XMLParserException {
        Properties attributes = this.parseAttributes(node);
        String resource = attributes.getProperty("resource");
        String url = attributes.getProperty("url");
        if (!StringUtility.stringHasValue(resource) && !StringUtility.stringHasValue(url)) {
            throw new XMLParserException(Messages.getString("RuntimeError.14"));
        } else if (StringUtility.stringHasValue(resource) && StringUtility.stringHasValue(url)) {
            throw new XMLParserException(Messages.getString("RuntimeError.14"));
        } else {
            try {
                URL resourceUrl;
                if (StringUtility.stringHasValue(resource)) {
                    resourceUrl = ObjectFactory.getResource(resource);
                    if (resourceUrl == null) {
                        throw new XMLParserException(Messages.getString("RuntimeError.15", resource));
                    }
                } else {
                    resourceUrl = new URL(url);
                }

                InputStream inputStream = resourceUrl.openConnection().getInputStream();
                this.configurationProperties.load(inputStream);
                inputStream.close();
            } catch (IOException var7) {
                if (StringUtility.stringHasValue(resource)) {
                    throw new XMLParserException(Messages.getString("RuntimeError.16", resource));
                } else {
                    throw new XMLParserException(Messages.getString("RuntimeError.17", url));
                }
            }
        }
    }

    private void parseContext(Configuration configuration, Node node) {
        Properties attributes = this.parseAttributes(node);
        String defaultModelType = attributes.getProperty("defaultModelType");
        String targetRuntime = attributes.getProperty("targetRuntime");
        String introspectedColumnImpl = attributes.getProperty("introspectedColumnImpl");
        String id = attributes.getProperty("id");
        ModelType mt = defaultModelType == null ? null : ModelType.getModelType(defaultModelType);
        Context context = new Context(mt);
        context.setId(id);
        if (StringUtility.stringHasValue(introspectedColumnImpl)) {
            context.setIntrospectedColumnImpl(introspectedColumnImpl);
        }

        if (StringUtility.stringHasValue(targetRuntime)) {
            context.setTargetRuntime(targetRuntime);
        }

        configuration.addContext(context);
        NodeList nodeList = node.getChildNodes();

        for(int i = 0; i < nodeList.getLength(); ++i) {
            Node childNode = nodeList.item(i);
            if (childNode.getNodeType() == 1) {
                if ("property".equals(childNode.getNodeName())) {
                    this.parseProperty(context, childNode);
                } else if ("plugin".equals(childNode.getNodeName())) {
                    this.parsePlugin(context, childNode);
                } else if ("commentGenerator".equals(childNode.getNodeName())) {
                    this.parseCommentGenerator(context, childNode);
                } else if ("jdbcConnection".equals(childNode.getNodeName())) {
                    this.parseJdbcConnection(context, childNode);
                } else if ("connectionFactory".equals(childNode.getNodeName())) {
                    this.parseConnectionFactory(context, childNode);
                } else if ("javaModelGenerator".equals(childNode.getNodeName())) {
                    this.parseJavaModelGenerator(context, childNode);
                } else if ("javaTypeResolver".equals(childNode.getNodeName())) {
                    this.parseJavaTypeResolver(context, childNode);
                } else if ("sqlMapGenerator".equals(childNode.getNodeName())) {
                    this.parseSqlMapGenerator(context, childNode);
                } else if ("javaClientGenerator".equals(childNode.getNodeName())) {
                    this.parseJavaClientGenerator(context, childNode);
                } else if ("table".equals(childNode.getNodeName())) {
                    this.parseTable(context, childNode);
                }
            }
        }

    }

    protected void parseSqlMapGenerator(Context context, Node node) {
        SqlMapGeneratorConfiguration sqlMapGeneratorConfiguration = new SqlMapGeneratorConfiguration();
        context.setSqlMapGeneratorConfiguration(sqlMapGeneratorConfiguration);
        Properties attributes = this.parseAttributes(node);
        String targetPackage = attributes.getProperty("targetPackage");
        String targetProject = attributes.getProperty("targetProject");
        sqlMapGeneratorConfiguration.setTargetPackage(targetPackage);
        sqlMapGeneratorConfiguration.setTargetProject(targetProject);
        NodeList nodeList = node.getChildNodes();

        for(int i = 0; i < nodeList.getLength(); ++i) {
            Node childNode = nodeList.item(i);
            if (childNode.getNodeType() == 1 && "property".equals(childNode.getNodeName())) {
                this.parseProperty(sqlMapGeneratorConfiguration, childNode);
            }
        }

    }

    protected void parseTable(Context context, Node node) {
        TableConfiguration tc = new TableConfiguration(context);
        context.addTableConfiguration(tc);
        Properties attributes = this.parseAttributes(node);
        String catalog = attributes.getProperty("catalog");
        if (StringUtility.stringHasValue(catalog)) {
            tc.setCatalog(catalog);
        }

        String schema = attributes.getProperty("schema");
        if (StringUtility.stringHasValue(schema)) {
            tc.setSchema(schema);
        }

        String tableName = attributes.getProperty("tableName");
        if (StringUtility.stringHasValue(tableName)) {
            tc.setTableName(tableName);
        }

        String domainObjectName = attributes.getProperty("domainObjectName");
        if (StringUtility.stringHasValue(domainObjectName)) {
            tc.setDomainObjectName(domainObjectName);
        }

        String alias = attributes.getProperty("alias");
        if (StringUtility.stringHasValue(alias)) {
            tc.setAlias(alias);
        }

        String enableInsert = attributes.getProperty("enableInsert");
        if (StringUtility.stringHasValue(enableInsert)) {
            tc.setInsertStatementEnabled(StringUtility.isTrue(enableInsert));
        }

        String enableSelectByPrimaryKey = attributes.getProperty("enableSelectByPrimaryKey");
        if (StringUtility.stringHasValue(enableSelectByPrimaryKey)) {
            tc.setSelectByPrimaryKeyStatementEnabled(StringUtility.isTrue(enableSelectByPrimaryKey));
        }

        String enableSelectByExample = attributes.getProperty("enableSelectByExample");
        if (StringUtility.stringHasValue(enableSelectByExample)) {
            tc.setSelectByExampleStatementEnabled(StringUtility.isTrue(enableSelectByExample));
        }

        String enableUpdateByPrimaryKey = attributes.getProperty("enableUpdateByPrimaryKey");
        if (StringUtility.stringHasValue(enableUpdateByPrimaryKey)) {
            tc.setUpdateByPrimaryKeyStatementEnabled(StringUtility.isTrue(enableUpdateByPrimaryKey));
        }

        String enableDeleteByPrimaryKey = attributes.getProperty("enableDeleteByPrimaryKey");
        if (StringUtility.stringHasValue(enableDeleteByPrimaryKey)) {
            tc.setDeleteByPrimaryKeyStatementEnabled(StringUtility.isTrue(enableDeleteByPrimaryKey));
        }

        String enableDeleteByExample = attributes.getProperty("enableDeleteByExample");
        if (StringUtility.stringHasValue(enableDeleteByExample)) {
            tc.setDeleteByExampleStatementEnabled(StringUtility.isTrue(enableDeleteByExample));
        }

        String enableCountByExample = attributes.getProperty("enableCountByExample");
        if (StringUtility.stringHasValue(enableCountByExample)) {
            tc.setCountByExampleStatementEnabled(StringUtility.isTrue(enableCountByExample));
        }

        String enableUpdateByExample = attributes.getProperty("enableUpdateByExample");
        if (StringUtility.stringHasValue(enableUpdateByExample)) {
            tc.setUpdateByExampleStatementEnabled(StringUtility.isTrue(enableUpdateByExample));
        }

        String selectByPrimaryKeyQueryId = attributes.getProperty("selectByPrimaryKeyQueryId");
        if (StringUtility.stringHasValue(selectByPrimaryKeyQueryId)) {
            tc.setSelectByPrimaryKeyQueryId(selectByPrimaryKeyQueryId);
        }

        String selectByExampleQueryId = attributes.getProperty("selectByExampleQueryId");
        if (StringUtility.stringHasValue(selectByExampleQueryId)) {
            tc.setSelectByExampleQueryId(selectByExampleQueryId);
        }

        String modelType = attributes.getProperty("modelType");
        if (StringUtility.stringHasValue(modelType)) {
            tc.setConfiguredModelType(modelType);
        }

        String escapeWildcards = attributes.getProperty("escapeWildcards");
        if (StringUtility.stringHasValue(escapeWildcards)) {
            tc.setWildcardEscapingEnabled(StringUtility.isTrue(escapeWildcards));
        }

        String delimitIdentifiers = attributes.getProperty("delimitIdentifiers");
        if (StringUtility.stringHasValue(delimitIdentifiers)) {
            tc.setDelimitIdentifiers(StringUtility.isTrue(delimitIdentifiers));
        }

        String delimitAllColumns = attributes.getProperty("delimitAllColumns");
        if (StringUtility.stringHasValue(delimitAllColumns)) {
            tc.setAllColumnDelimitingEnabled(StringUtility.isTrue(delimitAllColumns));
        }

        String mapperName = attributes.getProperty("mapperName");
        if (StringUtility.stringHasValue(mapperName)) {
            tc.setMapperName(mapperName);
        }

        String sqlProviderName = attributes.getProperty("sqlProviderName");
        if (StringUtility.stringHasValue(sqlProviderName)) {
            tc.setSqlProviderName(sqlProviderName);
        }

        NodeList nodeList = node.getChildNodes();

        for(int i = 0; i < nodeList.getLength(); ++i) {
            Node childNode = nodeList.item(i);
            if (childNode.getNodeType() == 1) {
                if ("property".equals(childNode.getNodeName())) {
                    this.parseProperty(tc, childNode);
                } else if ("columnOverride".equals(childNode.getNodeName())) {
                    this.parseColumnOverride(tc, childNode);
                } else if ("ignoreColumn".equals(childNode.getNodeName())) {
                    this.parseIgnoreColumn(tc, childNode);
                } else if ("ignoreColumnsByRegex".equals(childNode.getNodeName())) {
                    this.parseIgnoreColumnByRegex(tc, childNode);
                } else if ("generatedKey".equals(childNode.getNodeName())) {
                    this.parseGeneratedKey(tc, childNode);
                } else if ("domainObjectRenamingRule".equals(childNode.getNodeName())) {
                    this.parseDomainObjectRenamingRule(tc, childNode);
                } else if ("columnRenamingRule".equals(childNode.getNodeName())) {
                    this.parseColumnRenamingRule(tc, childNode);
                }
            }
        }

    }

    private void parseColumnOverride(TableConfiguration tc, Node node) {
        Properties attributes = this.parseAttributes(node);
        String column = attributes.getProperty("column");
        ColumnOverride co = new ColumnOverride(column);
        String property = attributes.getProperty("property");
        if (StringUtility.stringHasValue(property)) {
            co.setJavaProperty(property);
        }

        String javaType = attributes.getProperty("javaType");
        if (StringUtility.stringHasValue(javaType)) {
            co.setJavaType(javaType);
        }

        String jdbcType = attributes.getProperty("jdbcType");
        if (StringUtility.stringHasValue(jdbcType)) {
            co.setJdbcType(jdbcType);
        }

        String typeHandler = attributes.getProperty("typeHandler");
        if (StringUtility.stringHasValue(typeHandler)) {
            co.setTypeHandler(typeHandler);
        }

        String delimitedColumnName = attributes.getProperty("delimitedColumnName");
        if (StringUtility.stringHasValue(delimitedColumnName)) {
            co.setColumnNameDelimited(StringUtility.isTrue(delimitedColumnName));
        }

        String isGeneratedAlways = attributes.getProperty("isGeneratedAlways");
        if (StringUtility.stringHasValue(isGeneratedAlways)) {
            co.setGeneratedAlways(Boolean.parseBoolean(isGeneratedAlways));
        }

        NodeList nodeList = node.getChildNodes();

        for(int i = 0; i < nodeList.getLength(); ++i) {
            Node childNode = nodeList.item(i);
            if (childNode.getNodeType() == 1 && "property".equals(childNode.getNodeName())) {
                this.parseProperty(co, childNode);
            }
        }

        tc.addColumnOverride(co);
    }

    private void parseGeneratedKey(TableConfiguration tc, Node node) {
        Properties attributes = this.parseAttributes(node);
        String column = attributes.getProperty("column");
        boolean identity = StringUtility.isTrue(attributes.getProperty("identity"));
        String sqlStatement = attributes.getProperty("sqlStatement");
        String type = attributes.getProperty("type");
        GeneratedKey gk = new GeneratedKey(column, sqlStatement, identity, type);
        tc.setGeneratedKey(gk);
    }

    private void parseIgnoreColumn(TableConfiguration tc, Node node) {
        Properties attributes = this.parseAttributes(node);
        String column = attributes.getProperty("column");
        String delimitedColumnName = attributes.getProperty("delimitedColumnName");
        IgnoredColumn ic = new IgnoredColumn(column);
        if (StringUtility.stringHasValue(delimitedColumnName)) {
            ic.setColumnNameDelimited(StringUtility.isTrue(delimitedColumnName));
        }

        tc.addIgnoredColumn(ic);
    }

    private void parseIgnoreColumnByRegex(TableConfiguration tc, Node node) {
        Properties attributes = this.parseAttributes(node);
        String pattern = attributes.getProperty("pattern");
        IgnoredColumnPattern icPattern = new IgnoredColumnPattern(pattern);
        NodeList nodeList = node.getChildNodes();

        for(int i = 0; i < nodeList.getLength(); ++i) {
            Node childNode = nodeList.item(i);
            if (childNode.getNodeType() == 1 && "except".equals(childNode.getNodeName())) {
                this.parseException(icPattern, childNode);
            }
        }

        tc.addIgnoredColumnPattern(icPattern);
    }

    private void parseException(IgnoredColumnPattern icPattern, Node node) {
        Properties attributes = this.parseAttributes(node);
        String column = attributes.getProperty("column");
        String delimitedColumnName = attributes.getProperty("delimitedColumnName");
        IgnoredColumnException exception = new IgnoredColumnException(column);
        if (StringUtility.stringHasValue(delimitedColumnName)) {
            exception.setColumnNameDelimited(StringUtility.isTrue(delimitedColumnName));
        }

        icPattern.addException(exception);
    }

    private void parseDomainObjectRenamingRule(TableConfiguration tc, Node node) {
        Properties attributes = this.parseAttributes(node);
        String searchString = attributes.getProperty("searchString");
        String replaceString = attributes.getProperty("replaceString");
        DomainObjectRenamingRule dorr = new DomainObjectRenamingRule();
        dorr.setSearchString(searchString);
        if (StringUtility.stringHasValue(replaceString)) {
            dorr.setReplaceString(replaceString);
        }

        tc.setDomainObjectRenamingRule(dorr);
    }

    private void parseColumnRenamingRule(TableConfiguration tc, Node node) {
        Properties attributes = this.parseAttributes(node);
        String searchString = attributes.getProperty("searchString");
        String replaceString = attributes.getProperty("replaceString");
        ColumnRenamingRule crr = new ColumnRenamingRule();
        crr.setSearchString(searchString);
        if (StringUtility.stringHasValue(replaceString)) {
            crr.setReplaceString(replaceString);
        }

        tc.setColumnRenamingRule(crr);
    }

    protected void parseJavaTypeResolver(Context context, Node node) {
        JavaTypeResolverConfiguration javaTypeResolverConfiguration = new JavaTypeResolverConfiguration();
        context.setJavaTypeResolverConfiguration(javaTypeResolverConfiguration);
        Properties attributes = this.parseAttributes(node);
        String type = attributes.getProperty("type");
        if (StringUtility.stringHasValue(type)) {
            javaTypeResolverConfiguration.setConfigurationType(type);
        }

        NodeList nodeList = node.getChildNodes();

        for(int i = 0; i < nodeList.getLength(); ++i) {
            Node childNode = nodeList.item(i);
            if (childNode.getNodeType() == 1 && "property".equals(childNode.getNodeName())) {
                this.parseProperty(javaTypeResolverConfiguration, childNode);
            }
        }

    }

    private void parsePlugin(Context context, Node node) {
        PluginConfiguration pluginConfiguration = new PluginConfiguration();
        context.addPluginConfiguration(pluginConfiguration);
        Properties attributes = this.parseAttributes(node);
        String type = attributes.getProperty("type");
        pluginConfiguration.setConfigurationType(type);
        NodeList nodeList = node.getChildNodes();

        for(int i = 0; i < nodeList.getLength(); ++i) {
            Node childNode = nodeList.item(i);
            if (childNode.getNodeType() == 1 && "property".equals(childNode.getNodeName())) {
                this.parseProperty(pluginConfiguration, childNode);
            }
        }

    }

    protected void parseJavaModelGenerator(Context context, Node node) {
        JavaModelGeneratorConfiguration javaModelGeneratorConfiguration = new JavaModelGeneratorConfiguration();
        context.setJavaModelGeneratorConfiguration(javaModelGeneratorConfiguration);
        Properties attributes = this.parseAttributes(node);
        String targetPackage = attributes.getProperty("targetPackage");
        String targetProject = attributes.getProperty("targetProject");
        javaModelGeneratorConfiguration.setTargetPackage(targetPackage);
        javaModelGeneratorConfiguration.setTargetProject(targetProject);
        NodeList nodeList = node.getChildNodes();

        for(int i = 0; i < nodeList.getLength(); ++i) {
            Node childNode = nodeList.item(i);
            if (childNode.getNodeType() == 1 && "property".equals(childNode.getNodeName())) {
                this.parseProperty(javaModelGeneratorConfiguration, childNode);
            }
        }

    }

    private void parseJavaClientGenerator(Context context, Node node) {
        JavaClientGeneratorConfiguration javaClientGeneratorConfiguration = new JavaClientGeneratorConfiguration();
        context.setJavaClientGeneratorConfiguration(javaClientGeneratorConfiguration);
        Properties attributes = this.parseAttributes(node);
        String type = attributes.getProperty("type");
        String targetPackage = attributes.getProperty("targetPackage");
        String targetProject = attributes.getProperty("targetProject");
        javaClientGeneratorConfiguration.setConfigurationType(type);
        javaClientGeneratorConfiguration.setTargetPackage(targetPackage);
        javaClientGeneratorConfiguration.setTargetProject(targetProject);
        NodeList nodeList = node.getChildNodes();

        for(int i = 0; i < nodeList.getLength(); ++i) {
            Node childNode = nodeList.item(i);
            if (childNode.getNodeType() == 1 && "property".equals(childNode.getNodeName())) {
                this.parseProperty(javaClientGeneratorConfiguration, childNode);
            }
        }

    }

    protected void parseJdbcConnection(Context context, Node node) {
        JDBCConnectionConfiguration jdbcConnectionConfiguration = new JDBCConnectionConfiguration();
        context.setJdbcConnectionConfiguration(jdbcConnectionConfiguration);
        Properties attributes = this.parseAttributes(node);
        String driverClass = attributes.getProperty("driverClass");
        String connectionURL = attributes.getProperty("connectionURL");
        jdbcConnectionConfiguration.setDriverClass(driverClass);
        jdbcConnectionConfiguration.setConnectionURL(connectionURL);
        String userId = attributes.getProperty("userId");
        if (StringUtility.stringHasValue(userId)) {
            jdbcConnectionConfiguration.setUserId(userId);
        }

        String password = attributes.getProperty("password");
        if (StringUtility.stringHasValue(password)) {
            jdbcConnectionConfiguration.setPassword(password);
        }

        NodeList nodeList = node.getChildNodes();

        for(int i = 0; i < nodeList.getLength(); ++i) {
            Node childNode = nodeList.item(i);
            if (childNode.getNodeType() == 1 && "property".equals(childNode.getNodeName())) {
                this.parseProperty(jdbcConnectionConfiguration, childNode);
            }
        }

    }

    protected void parseClassPathEntry(Configuration configuration, Node node) {
        Properties attributes = this.parseAttributes(node);
        configuration.addClasspathEntry(attributes.getProperty("location"));
    }

    protected void parseProperty(PropertyHolder propertyHolder, Node node) {
        Properties attributes = this.parseAttributes(node);
        String name = attributes.getProperty("name");
        String value = attributes.getProperty("value");
        propertyHolder.addProperty(name, value);
    }

    protected Properties parseAttributes(Node node) {
        Properties attributes = new Properties();
        NamedNodeMap nnm = node.getAttributes();

        for(int i = 0; i < nnm.getLength(); ++i) {
            Node attribute = nnm.item(i);
            String value = this.parsePropertyTokens(attribute.getNodeValue());
            attributes.put(attribute.getNodeName(), value);
        }

        return attributes;
    }

    String parsePropertyTokens(String s) {
        String OPEN = "${";
        String CLOSE = "}";
        int currentIndex = 0;
        List<String> answer = new ArrayList();
        int markerStartIndex = s.indexOf("${");
        if (markerStartIndex < 0) {
            answer.add(s);
            currentIndex = s.length();
        }

        while(markerStartIndex > -1) {
            if (markerStartIndex > currentIndex) {
                answer.add(s.substring(currentIndex, markerStartIndex));
                currentIndex = markerStartIndex;
            }

            int markerEndIndex = s.indexOf("}", currentIndex);

            for(int nestedStartIndex = s.indexOf("${", markerStartIndex + "${".length()); nestedStartIndex > -1 && markerEndIndex > -1 && nestedStartIndex < markerEndIndex; markerEndIndex = s.indexOf("}", markerEndIndex + "}".length())) {
                nestedStartIndex = s.indexOf("${", nestedStartIndex + "${".length());
            }

            if (markerEndIndex < 0) {
                answer.add(s.substring(markerStartIndex));
                currentIndex = s.length();
                break;
            }

            String property = s.substring(markerStartIndex + "${".length(), markerEndIndex);
            String propertyValue = this.resolveProperty(this.parsePropertyTokens(property));
            if (propertyValue == null) {
                answer.add(s.substring(markerStartIndex, markerEndIndex + 1));
            } else {
                answer.add(propertyValue);
            }

            currentIndex = markerEndIndex + "}".length();
            markerStartIndex = s.indexOf("${", currentIndex);
        }

        if (currentIndex < s.length()) {
            answer.add(s.substring(currentIndex));
        }

        return (String)answer.stream().collect(Collectors.joining());
    }

    protected void parseCommentGenerator(Context context, Node node) {
        CommentGeneratorConfiguration commentGeneratorConfiguration = new CommentGeneratorConfiguration();
        context.setCommentGeneratorConfiguration(commentGeneratorConfiguration);
        Properties attributes = this.parseAttributes(node);
        String type = attributes.getProperty("type");
        if (StringUtility.stringHasValue(type)) {
            commentGeneratorConfiguration.setConfigurationType(type);
        }

        NodeList nodeList = node.getChildNodes();

        for(int i = 0; i < nodeList.getLength(); ++i) {
            Node childNode = nodeList.item(i);
            if (childNode.getNodeType() == 1 && "property".equals(childNode.getNodeName())) {
                this.parseProperty(commentGeneratorConfiguration, childNode);
            }
        }

    }

    protected void parseConnectionFactory(Context context, Node node) {
        ConnectionFactoryConfiguration connectionFactoryConfiguration = new ConnectionFactoryConfiguration();
        context.setConnectionFactoryConfiguration(connectionFactoryConfiguration);
        Properties attributes = this.parseAttributes(node);
        String type = attributes.getProperty("type");
        if (StringUtility.stringHasValue(type)) {
            connectionFactoryConfiguration.setConfigurationType(type);
        }

        NodeList nodeList = node.getChildNodes();

        for(int i = 0; i < nodeList.getLength(); ++i) {
            Node childNode = nodeList.item(i);
            if (childNode.getNodeType() == 1 && "property".equals(childNode.getNodeName())) {
                this.parseProperty(connectionFactoryConfiguration, childNode);
            }
        }

    }

    private String resolveProperty(String key) {
        String property = null;
        property = System.getProperty(key);
        if (property == null) {
            property = this.configurationProperties.getProperty(key);
        }

        if (property == null) {
            property = this.extraProperties.getProperty(key);
        }

        return property;
    }
}
