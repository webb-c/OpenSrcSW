package scripts;

import org.snu.ids.kkma.index.Keyword;
import org.snu.ids.kkma.index.KeywordExtractor;
import org.snu.ids.kkma.index.KeywordList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 3주차 실습 코드
 *
 * kkma 형태소 분석기를 이용하여 index.xml 파일을 생성하세요.
 *
 * index.xml 파일 형식은 아래와 같습니다.
 * (키워드1):(키워드1에 대한 빈도수)#(키워드2):(키워드2에 대한 빈도수)#(키워드3):(키워드3에 대한 빈도수) ...
 * e.g., 라면:13#밀가루:4#달걀:1 ...
 *
 * input : collection.xml
 * output : index.xml
 */

public class makeKeyword {

    private String input_file;
    private String output_flie = "./index.xml";

    public makeKeyword(String file) {
        this.input_file = file;
    }

    public void convertXml() throws ParserConfigurationException, IOException, SAXException, TransformerException {
        //1. collection.xml읽어오기
        File file = new File(input_file);
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document document = docBuilder.parse(file);

        document.getDocumentElement().normalize();
        NodeList nList = document.getElementsByTagName("doc");

        Document new_document = docBuilder.newDocument();  //새로 저장할 XML을 생성할 때 쓰이는 도큐먼트
        Element docs = new_document.createElement("docs");
        new_document.appendChild(docs);

        for (int temp = 0; temp < nList.getLength(); temp++) {
            Node nNode = nList.item(temp);
            System.out.println("\nCurrent Element :" + nNode.getNodeName());
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;

                //--기존 파일의 body부분 파싱 & 형태분석--
                String kkamString = eElement.getElementsByTagName("body").item(0).getTextContent();
                String keywordString = "";
                KeywordExtractor ke = new KeywordExtractor();                         //추출에 사용되는 객체
                KeywordList k = ke.extractKeyword(kkamString, true);
                for(int i = 0; i < k.size() ; i++){
                    Keyword kwrd = k.get(i);
                    keywordString += kwrd.getString()+":"+kwrd.getCnt()+"#";
                }

                //--새 파일 생성--
                //doc element
                Element doc = new_document.createElement("doc");
                docs.appendChild(doc);
                //doc의 속성값 (id)
                doc.setAttribute("id", Integer.toString(temp));
                //title element
                Element title = new_document.createElement("title");
                title.appendChild(new_document.createTextNode(eElement.getElementsByTagName("title").item(0).getTextContent()));
                doc.appendChild(title);
                //body elementß
                Element body = new_document.createElement("body");
                body.appendChild(new_document.createTextNode(keywordString));  //형태소 분석한 내용 삽입
                doc.appendChild(body);
            }
        }

        //3. 새롭게 만든 XML파일 반환하기
        TransformerFactory transformerFactory = TransformerFactory.newInstance();

        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

        DOMSource source2 = new DOMSource(new_document);
        StreamResult result2 = new StreamResult(new FileOutputStream(new File(output_flie)));

        transformer.transform(source2, result2);

        System.out.println("3주차 실행완료");
    }

}