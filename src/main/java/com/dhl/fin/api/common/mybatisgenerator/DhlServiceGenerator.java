package com.dhl.fin.api.common.mybatisgenerator;

import org.mybatis.generator.api.dom.java.CompilationUnit;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.codegen.AbstractJavaClientGenerator;
import org.mybatis.generator.codegen.AbstractXmlGenerator;
import org.mybatis.generator.codegen.mybatis3.xmlmapper.XMLMapperGenerator;

import java.util.ArrayList;
import java.util.List;

import static org.mybatis.generator.internal.util.messages.Messages.getString;

/**
 * Created by CuiJianbo on 2020.02.15.
 */
public class DhlServiceGenerator extends AbstractJavaClientGenerator {


    public DhlServiceGenerator(String project) {
        this(project, false);
    }

    public DhlServiceGenerator(String project, boolean requiresMatchedXMLGenerator) {
        super(project, requiresMatchedXMLGenerator);
    }

    @Override
    public List<CompilationUnit> getCompilationUnits() {
        String domainName = introspectedTable.getTableConfiguration().getDomainObjectName();

        FullyQualifiedJavaType type = new FullyQualifiedJavaType("com.dhl.fin.api.service." + domainName + "ServiceImpl");
        TopLevelClass interfaze = new TopLevelClass(type);
        interfaze.setVisibility(JavaVisibility.PUBLIC);


        FullyQualifiedJavaType fqjt = new FullyQualifiedJavaType("com.dhl.fin.api.common.service.CommonService<" + domainName + ">");
        interfaze.addImportedType("com.dhl.fin.api.common.service.CommonService");
        interfaze.addImportedType("org.springframework.stereotype.Service");
        interfaze.addImportedType(getDomainType());
        interfaze.addAnnotation("@Service");
        interfaze.addSuperInterface(fqjt);

        List<CompilationUnit> answer = new ArrayList<>();
        answer.add(interfaze);


        return answer;
    }


    @Override
    public AbstractXmlGenerator getMatchedXMLGenerator() {
        return new XMLMapperGenerator();
    }

    public String getDomainType() {
        String domainName = introspectedTable.getTableConfiguration().getDomainObjectName();
        return "com.dhl.fin.api.domain." + domainName;
    }

}
