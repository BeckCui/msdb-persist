package com.dhl.fin.api.common.mybatisgenerator;

import com.dhl.fin.api.common.util.ObjectUtil;
import org.mybatis.generator.api.dom.java.CompilationUnit;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.codegen.AbstractJavaClientGenerator;
import org.mybatis.generator.codegen.AbstractJavaGenerator;
import org.mybatis.generator.codegen.AbstractXmlGenerator;
import org.mybatis.generator.codegen.mybatis3.xmlmapper.XMLMapperGenerator;
import org.mybatis.generator.config.JavaClientGeneratorConfiguration;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.mybatis.generator.internal.util.messages.Messages.getString;

/**
 * Created by CuiJianbo on 2020.02.15.
 */
public class DhlServiceGenerator extends AbstractJavaGenerator {


    public DhlServiceGenerator(String project) {
        super(project);
    }


    @Override
    public List<CompilationUnit> getCompilationUnits() {
        String domainName = introspectedTable.getTableConfiguration().getDomainObjectName();
        JavaClientGeneratorConfiguration clientGeneratorConfiguration = this.context.getJavaClientGeneratorConfiguration();


        String packageName = clientGeneratorConfiguration.getTargetPackage();
        packageName = packageName.replace(".dao", ".service.");
        packageName = packageName + domainName + "ServiceImpl";

        URL url = this.getClass().getClassLoader().getResource(packageName.replaceAll("\\.", "/") + ".class");
        if (ObjectUtil.notNull(url)) {
            return new ArrayList<>();
        }

        FullyQualifiedJavaType type = new FullyQualifiedJavaType(packageName);
        TopLevelClass interfaze = new TopLevelClass(type);
        interfaze.setVisibility(JavaVisibility.PUBLIC);


        FullyQualifiedJavaType fqjt = new FullyQualifiedJavaType("com.dhl.fin.api.common.service.CommonService<" + domainName + ">");
        interfaze.addImportedType("com.dhl.fin.api.common.service.CommonService");
        interfaze.addImportedType("org.springframework.stereotype.Service");
        interfaze.addImportedType("org.springframework.transaction.annotation.Transactional");
        interfaze.addImportedType(getDomainType());
        interfaze.addAnnotation("@Service");
        interfaze.addAnnotation("@Transactional");
        interfaze.addSuperInterface(fqjt);

        List<CompilationUnit> answer = new ArrayList<>();
        answer.add(interfaze);


        return answer;
    }


    public String getDomainType() {
        String domainName = introspectedTable.getTableConfiguration().getDomainObjectName();
        return "com.dhl.fin.api.domain." + domainName;
    }

}
