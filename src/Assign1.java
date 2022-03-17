import org.jsoup.Jsoup;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

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
import java.io.FilenameFilter;
import java.io.IOException;

public class Assign1 {
    public static void main(String[] args) throws IOException, ParserConfigurationException, TransformerException {

        //1. html폴더 안에 있는 파일 읽어오기 (https://codechacha.com/ko/java-list-files/)
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

        //2. jsoup으로 가져온 html파일 파싱하기 (https://jobc.tistory.com/78)
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
        StreamResult result = new StreamResult(new FileOutputStream(new File("result/change.xml")));

        transformer.transform(source, result);
    }
}
