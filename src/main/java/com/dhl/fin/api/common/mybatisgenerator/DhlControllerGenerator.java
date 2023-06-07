package com.dhl.fin.api.common.mybatisgenerator;

import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.codegen.AbstractJavaClientGenerator;
import org.mybatis.generator.codegen.AbstractXmlGenerator;
import org.mybatis.generator.codegen.mybatis3.javamapper.elements.*;
import org.mybatis.generator.codegen.mybatis3.xmlmapper.XMLMapperGenerator;
import org.mybatis.generator.config.JavaClientGeneratorConfiguration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mybatis.generator.internal.util.messages.Messages.getString;

/**
 * Created by CuiJianbo on 2020.02.15.
 */
public class DhlControllerGenerator extends AbstractJavaClientGenerator {


    public DhlControllerGenerator(String project) {
        this(project, false);
    }

    public DhlControllerGenerator(String project, boolean requiresMatchedXMLGenerator) {
        super(project, requiresMatchedXMLGenerator);
    }

    @Override
    public List<CompilationUnit> getCompilationUnits() {
        String domainName = introspectedTable.getTableConfiguration().getDomainObjectName();
        JavaClientGeneratorConfiguration clientGeneratorConfiguration = this.context.getJavaClientGeneratorConfiguration();

        FullyQualifiedJavaType type = new FullyQualifiedJavaType(clientGeneratorConfiguration.getTargetPackage() + ".controller." + domainName + "Control");
        TopLevelClass interfaze = new TopLevelClass(type);
        interfaze.setVisibility(JavaVisibility.PUBLIC);


        FullyQualifiedJavaType superClass = new FullyQualifiedJavaType("com.dhl.fin.api.common.controller.CommonController<" + domainName + ">");
        interfaze.addImportedType("com.dhl.fin.api.common.controller.CommonController");
        interfaze.addImportedType("org.springframework.web.bind.annotation.RequestMapping");
        interfaze.addImportedType("org.springframework.web.bind.annotation.RestController");
        interfaze.addImportedType(getDomainType());
        interfaze.addAnnotation("@RestController");
        interfaze.addAnnotation("@RequestMapping({\"" + domainName.toLowerCase() + "\"})");
        interfaze.addSuperInterface(superClass);

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
