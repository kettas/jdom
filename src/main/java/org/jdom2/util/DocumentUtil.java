package org.jdom2.util;

import java.io.File;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;

public class DocumentUtil {
    public static Document getDocFromInputStream(InputStream is) throws Exception {
        return getDocBuilder().parse(is);
    }

    public static Document getDocFromFile(File f) throws Exception {
        return getDocBuilder().parse(f);
    }

    public static DocumentBuilder getDocBuilder() throws ParserConfigurationException {
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        docBuilderFactory.setNamespaceAware(true);
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
        return docBuilder;
    }

    public static Document getDocFromString(String xml) throws Exception {
        return getDocFromString(new String(xml.getBytes(),"UTF-8"));
    }
}
