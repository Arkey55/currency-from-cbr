package romankuznetsov.ru;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.net.URL;

public class App {

    private static final String ID_HUF = "R01135";
    private static final String ID_NOK = "R01535";

    public static void main(String[] args) {
        try {
            URL cbrUrl = new URL("http://www.cbr.ru/scripts/XML_daily.asp");
            float courseHUFtoNOK = 0;
            courseHUFtoNOK = convertCurrency(cbrUrl, ID_HUF, ID_NOK);
            System.out.println(courseHUFtoNOK);
        } catch (IOException | ParserConfigurationException |
                SAXException | XPathExpressionException e) {
            e.printStackTrace();
        }
    }

    public static float convertCurrency(URL url,
                                        String firstCurrencyId,
                                        String secondCurrencyId)
            throws IOException,
            ParserConfigurationException,
            SAXException,
            XPathExpressionException {

        DocumentBuilder documentBuilder = DocumentBuilderFactory
                .newInstance()
                .newDocumentBuilder();
        Document document = documentBuilder.parse(url.openStream());
        XPath xPath = XPathFactory.newInstance().newXPath();

        String expressionCurrency1 =
                "/ValCurs/Valute[@ID=" + "'" + firstCurrencyId + "'" + "]";
        String expressionCurrency2 =
                "/ValCurs/Valute[@ID=" + "'" + secondCurrencyId + "'" + "]";

        Node nodeCurrency1 = (Node) xPath.compile(expressionCurrency1)
                .evaluate(document, XPathConstants.NODE);
        Node nodeCurrency2 = (Node) xPath.compile(expressionCurrency2)
                .evaluate(document, XPathConstants.NODE);

        NodeList nodesCurrency1 = nodeCurrency1.getChildNodes();
        NodeList nodesCurrency2 = nodeCurrency2.getChildNodes();

        float courseCurrency1 = searchCourse(nodesCurrency1);
        float nominalCurrency1 = searchNominal(nodesCurrency1);
        float courseCurrency2 = searchCourse(nodesCurrency2);
        float nominalCurrency2 = searchNominal(nodesCurrency2);

        return (courseCurrency2/nominalCurrency2)/
                (courseCurrency1/nominalCurrency1);
    }

    private static float searchCourse(NodeList nodes){
        for (int i = 0; i < nodes.getLength(); i++) {
            Node n = nodes.item(i);
            if (n.getNodeName().equals("Value")){
                return Float.parseFloat(n.getChildNodes()
                        .item(0)
                        .getTextContent()
                        .replace(",","."));
            }
        }
        return 0f;
    }

    private static float searchNominal(NodeList nodes){
        for (int i = 0; i < nodes.getLength(); i++) {
            Node n = nodes.item(i);
            if (n.getNodeName().equals("Nominal")){
                return Float.parseFloat(n.getChildNodes()
                                .item(0)
                                .getTextContent());
            }
        }
        return 0f;
    }
}
