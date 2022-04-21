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
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

public class MidTerm {

    private String data_path;

    public MidTerm(String path) {
        this.data_path = path;
    }

    public void showSnippet(String query) throws ParserConfigurationException, IOException, SAXException {

        //각 문서의 title, body부분을 저장할 String 배열
        String[] title = new String[5];
        String[] body = new String[5];
        LinkedList<String> bodyList = new LinkedList<>();

        //입력으로 들어온 쿼리 분석
        LinkedList<String> queryList = new LinkedList<>();  //쿼리를 키워드별로 저장할 String 리스트
        KeywordExtractor ke = new KeywordExtractor();                         //추출에 사용되는 객체
        KeywordList k = ke.extractKeyword(query, true);
        for (int i = 0; i < k.size(); i++) {
            Keyword kwrd = k.get(i);
            queryList.add(kwrd.getString());
        }

        //collection.xml읽어오기
        File file = new File(data_path);
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document document = docBuilder.parse(file);

        document.getDocumentElement().normalize();
        NodeList nList = document.getElementsByTagName("doc");

        for (int temp = 0; temp < nList.getLength(); temp++) {
            Node nNode = nList.item(temp);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;
                //body, title부분 배열에 저장
                body[temp] = eElement.getElementsByTagName("body").item(0).getTextContent();
                title[temp] = eElement.getElementsByTagName("title").item(0).getTextContent();;
            }
        }

        LinkedList<Snippet> SnippetList = new LinkedList<>();

        int count;
        String temp;
        KeywordList k2;
        for (int id = 0 ; id < 5 ; id++){
            for (int i = 0 ; i <= body[id].length()-29 ; i++) {
                count = 0;
                temp = body[id].substring(i, i+29);
                //해당 구간의 키워드 분석
                k2 = ke.extractKeyword(temp, true);
                for (int j = 0; j < k2.size(); j++) {
                    Keyword kwrd = k2.get(j);
                    bodyList.add(kwrd.getString());
                }
                for(int b = 0 ; b < bodyList.size() ; b++) {
                    for(int h = 0; h < queryList.size() ; h++) {
                        if(queryList.get(h).equals(bodyList.get(b))) {
                            count++;
                        }
                    }
                }
                if(i == 0){
                    SnippetList.add(new Snippet(temp, id, count));
                }
                else if(SnippetList.get(id).score < count){
                    SnippetList.get(id).set(temp, id, count);
                }
            }
        }

        for(int id = 0 ; id < 5 ; id++){
            if(SnippetList.get(id).score != 0){
                System.out.println("Title: "+title[id]+SnippetList.get(id).print());
            }
        }
    }
}


class Snippet {

    String snippet = "";
    int id = 0;
    int score = 0;


    public Snippet(String snippet, int id, int score){
        this.snippet = snippet;
        this.id = id;
        this.score = score;
    }

    public void set(String snippet, int id, int score){
        this.snippet = snippet;
        this.id = id;
        this.score = score;
    }

    public String print(){
        String s = "Snippet: "+snippet+"Matching score: "+score;
        return s;
    }

    /*
    public String output(){                 //최종 문자열 출력
        String s = "";
        for(int i = 0 ; i < 5 ; i++) {
            double ti = fre[i]*Math.log((double) 5/doc_count);
            String sti = String.format("%.2f", ti);
            s = s+i+" "+sti+" ";
        }
        return s;
    }
    */

}