import org.jsoup.Jsoup;
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
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;

public class Parsing_Keyword {
    //첫번째 과제 수행 메소드
    public void parsing_assign1() throws IOException, ParserConfigurationException, TransformerException {
        //1. html폴더 안에 있는 파일 읽어오기
        File dir = new File("html");
        FilenameFilter filter = new FilenameFilter() {
            @Override
            public boolean accept(File f, String name) {
                return name.endsWith("html");
            }
        };
        File files[] = dir.listFiles(filter);
        for (int i = 0; i < files.length; i++) {   //파일 제대로 추가되었는지 확인
            System.out.println("file: " + files[i]);
        }

        //파싱에 사용할 타이틀을 저장할 문자열 배열과 바디를 저장할 문자열 배열을 생성
        String[] titleData = new String[files.length];
        String[] bodyData = new String[files.length];

        //2. jsoup으로 가져온 html파일 파싱하기
        for (int i = 0; i < files.length; i++) {
            org.jsoup.nodes.Document html = Jsoup.parse(files[i], "UTF-8");
            titleData[i] = html.title();
            bodyData[i] = html.body().text();
        }

        //3. XML 생성 - Document 객체를 생성한뒤, element(태그)각가그이 객체를 이용해 추출하여 원하는 데이터를 사용
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        Document document = docBuilder.newDocument();

        //docs element
        Element docs = document.createElement("docs");
        document.appendChild(docs);

        for (int i = 0 ; i <files.length ; i++) {
            //doc element
            Element doc = document.createElement("doc");
            docs.appendChild(doc);
            //doc의 속성값 (id)
            doc.setAttribute("id", Integer.toString(i));
            //title element
            Element title = document.createElement("title");
            title.appendChild(document.createTextNode(titleData[i]));
            doc.appendChild(title);
            //body element
            Element body = document.createElement("body");
            body.appendChild(document.createTextNode(bodyData[i]));
            doc.appendChild(body);
        }

        //4. 생성한 파일 출력
        TransformerFactory transformerFactory = TransformerFactory.newInstance();

        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

        DOMSource source = new DOMSource(document);
        StreamResult result = new StreamResult(new FileOutputStream(new File("result/collection.xml")));

        transformer.transform(source, result);
    }

    //두번째 과제 수행 메소드
    public void keyword_assign2() throws ParserConfigurationException, IOException, SAXException, TransformerException {
        //1. collection.xml읽어오기
        File file = new File("result/collection.xml");
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
        StreamResult result2 = new StreamResult(new FileOutputStream(new File("result/index.xml")));

        transformer.transform(source2, result2);

    }
}
