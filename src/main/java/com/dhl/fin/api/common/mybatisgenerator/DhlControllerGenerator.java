package com.dhl.fin.api.common.mybatisgenerator;

import cn.hutool.core.io.FileUtil;
import com.dhl.fin.api.common.util.ObjectUtil;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.codegen.AbstractJavaClientGenerator;
import org.mybatis.generator.codegen.AbstractJavaGenerator;
import org.mybatis.generator.codegen.AbstractXmlGenerator;
import org.mybatis.generator.codegen.mybatis3.javamapper.elements.*;
import org.mybatis.generator.codegen.mybatis3.xmlmapper.XMLMapperGenerator;
import org.mybatis.generator.config.JavaClientGeneratorConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mybatis.generator.internal.util.messages.Messages.getString;

/**
 * Created by CuiJianbo on 2020.02.15.
 */
public class DhlControllerGenerator extends AbstractJavaGenerator {


    public DhlControllerGenerator(String project) {
        super(project);
    }

    @Override
    public List<CompilationUnit> getCompilationUnits() {
        String domainName = introspectedTable.getTableConfiguration().getDomainObjectName();
        JavaClientGeneratorConfiguration clientGeneratorConfiguration = this.context.getJavaClientGeneratorConfiguration();

        String packageName = clientGeneratorConfiguration.getTargetPackage();
        packageName = packageName.replace(".dao", ".controller.");
        packageName = packageName + domainName + "Control";

        URL url = this.getClass().getClassLoader().getResource(packageName.replaceAll("\\.", "/") + ".class");
        if (ObjectUtil.notNull(url)) {
            return new ArrayList<>();
        }

        FullyQualifiedJavaType type = new FullyQualifiedJavaType(packageName);
        TopLevelClass interfaze = new TopLevelClass(type);
        interfaze.setVisibility(JavaVisibility.PUBLIC);


        FullyQualifiedJavaType superClass = new FullyQualifiedJavaType("com.dhl.fin.api.common.controller.CommonController<" + domainName + ">");
        interfaze.addImportedType("com.dhl.fin.api.common.controller.CommonController");
        interfaze.addImportedType("org.springframework.web.bind.annotation.RequestMapping");
        interfaze.addImportedType("org.springframework.web.bind.annotation.RestController");
        interfaze.addImportedType("org.springframework.transaction.annotation.Transactional");
        interfaze.addImportedType("lombok.extern.slf4j.Slf4j");
        interfaze.addImportedType(getDomainType());
        interfaze.addAnnotation("@Slf4j");
        interfaze.addAnnotation("@Transactional(rollbackFor = Exception.class)");
        interfaze.addAnnotation("@RestController");
        interfaze.addAnnotation("@RequestMapping({\"" + domainName.toLowerCase() + "\"})");
        interfaze.addSuperInterface(superClass);

        List<CompilationUnit> answer = new ArrayList<>();
        answer.add(interfaze);


        return answer;
    }


    public String getDomainType() {
        String domainName = introspectedTable.getTableConfiguration().getDomainObjectName();
        return "com.dhl.fin.api.domain." + domainName;
    }

}
