package com.dhl.fin.api.common.mybatisgenerator;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author CuiJianbo
 * @since 2023/6/7
 */
public class DhlParserEntityResolver implements EntityResolver {

    public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
        if ("-//mybatis.org//DTD MyBatis Generator Configuration 1.0//EN".equalsIgnoreCase(publicId)) {
            InputStream is = this.getClass().getClassLoader().getResourceAsStream("mybatis-generator-config.dtd");
            return new InputSource(is);
        } else {
            return null;
        }
    }

}
