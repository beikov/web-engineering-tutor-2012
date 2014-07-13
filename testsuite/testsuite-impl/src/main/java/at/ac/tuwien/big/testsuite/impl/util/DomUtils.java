/*
 * Copyright 2013 BIG TU Wien.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package at.ac.tuwien.big.testsuite.impl.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import nu.validator.htmlparser.common.XmlViolationPolicy;
import nu.validator.htmlparser.sax.HtmlParser;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author Christian
 */
public class DomUtils {

    private static final Logger LOG = Logger.getLogger(DomUtils.class.getName());
    public static final String LINE_NUMBER_KEY_NAME = "lineNumber";

    public static Document createDocument(File file) {
        try {
            return createDocument(new FileInputStream(file));
        } catch (FileNotFoundException ex) {
            return null;
        }
    }

    public static Document createDocument(InputStream inputStream) {
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        HtmlParser htmlParser = new HtmlParser(XmlViolationPolicy.ALLOW);

        try (InputStream is = inputStream) {

            DocumentBuilder db = factory.newDocumentBuilder();

            final Document doc = db.newDocument();
            final Stack<Element> elementStack = new Stack<>();
            final StringBuilder textBuffer = new StringBuilder();
            final DefaultHandler handler = new DefaultHandler() {
                private Locator locator;

                @Override
                public void setDocumentLocator(final Locator locator) {
                    this.locator = locator; // Save the locator, so that it can be used later for line tracking when traversing nodes.
                }

                @Override
                public void startElement(final String uri, final String localName, final String qName, final Attributes attributes)
                        throws SAXException {
                    addTextIfNeeded();
                    final Element el = doc.createElement(qName);
                    for (int i = 0; i < attributes.getLength(); i++) {
                        try {
                            el.setAttribute(attributes.getQName(i), attributes.getValue(i));
                        } catch (RuntimeException ex) {
                            LOG.log(Level.SEVERE, "Ignoring invalid attribute '" + attributes.getQName(i) + "' at line " + locator.getLineNumber(), ex);
                        }
                    }

                    el.setUserData(LINE_NUMBER_KEY_NAME, String.valueOf(this.locator.getLineNumber()), null);
                    elementStack.push(el);
                }

                @Override
                public void endElement(final String uri, final String localName, final String qName) {
                    addTextIfNeeded();
                    final Element closedEl = elementStack.pop();
                    if (elementStack.isEmpty()) { // Is this the root element?
                        doc.appendChild(closedEl);
                    } else {
                        final Element parentEl = elementStack.peek();
                        parentEl.appendChild(closedEl);
                    }
                }

                @Override
                public void characters(final char ch[], final int start, final int length) throws SAXException {
                    textBuffer.append(ch, start, length);
                }

                // Outputs text accumulated under the current node
                private void addTextIfNeeded() {
                    if (textBuffer.length() > 0) {
                        final Element el = elementStack.peek();
                        final Node textNode = doc.createTextNode(textBuffer.toString());
                        el.appendChild(textNode);
                        textBuffer.delete(0, textBuffer.length());
                    }
                }
            };

            htmlParser.setContentHandler(handler);
            htmlParser.parse(new InputSource(is));
            return doc;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static Element byId(Element element, String id) {
        XPath xPath = XPathFactory.newInstance().newXPath();

        try {
            return (Element) xPath.evaluate("//*[@id = '" + id + "']", element, XPathConstants.NODE);
        } catch (XPathExpressionException ex) {
            return null;
        }
    }

    public static String textByXpath(Element element, String xpath) {
        XPath xPath = XPathFactory.newInstance().newXPath();

        try {
            return ((Node) xPath.evaluate(xpath, element, XPathConstants.NODE)).getTextContent();
        } catch (XPathExpressionException ex) {
            return null;
        }
    }

    public static List<Element> listByXpath(Element element, String xpath) {
        XPath xPath = XPathFactory.newInstance().newXPath();

        try {
            return asList((NodeList) xPath.evaluate(xpath, element, XPathConstants.NODESET));
        } catch (XPathExpressionException ex) {
            return null;
        }
    }

    public static List<Attr> attributesByXpath(Element element, String xpath) {
        XPath xPath = XPathFactory.newInstance().newXPath();

        try {
            return asAttributeList((NodeList) xPath.evaluate(xpath, element, XPathConstants.NODESET));
        } catch (XPathExpressionException ex) {
            return null;
        }
    }

    public static List<Element> asList(NodeList nodeList) {
        List<Element> elements = new ArrayList<>(nodeList.getLength());

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node childElement = nodeList.item(i);

            if (childElement instanceof Element) {
                elements.add((Element) childElement);
            }
        }

        return elements;
    }

    public static List<Attr> asAttributeList(NodeList nodeList) {
        List<Attr> elements = new ArrayList<>(nodeList.getLength());

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node childElement = nodeList.item(i);

            if (childElement instanceof Attr) {
                elements.add((Attr) childElement);
            }
        }

        return elements;
    }

    public static Element firstByClass(NodeList childList, String className) {
        for (int i = 0; i < childList.getLength(); i++) {
            Element childElement = (Element) childList.item(i);

            if (className.equals(childElement.getAttribute("class"))) {
                return childElement;
            }
        }

        return null;
    }

    public static void write(Document doc, File target) {
        try (FileWriter fos = new FileWriter(target)) {
            Source source = new DOMSource(doc);
            Result result = new StreamResult(fos);

            Transformer xformer = TransformerFactory.newInstance().newTransformer();
            xformer.transform(source, result);
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Could not write xml file", ex);
        }
    }
}
