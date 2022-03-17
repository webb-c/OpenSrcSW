import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, ParserConfigurationException, TransformerException, SAXException {
        Parsing_Keyword pk = new Parsing_Keyword();

        pk.parsing_assign1();   //과제 1 - XML파일 생성
        System.out.println("과제 1 수행완료");

        pk.keyword_assign2();   //과제 2 - Keyword 파악 & parsing해서 XML파일 생성
        System.out.println("과제 2 수행완료");
    }
}

