package scripts;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * 4주차 실습 코드
 *
 * 주어진 xml문서에서 키워드와 빈도수를 읽어와서 post문서를 만드세요
 *
 * input : index.xml
 * output : index.post
 */

public class indexer {

    private String input_file;
    private String output_flie = "./index.post";

    public indexer(String path) {
        this.input_file = path;
    }

    public void makepost() throws IOException, ClassNotFoundException, ParserConfigurationException, SAXException {
        //xml읽어와서 키워드 당 빈도수 분석하기
        //1. collection.xml읽어오기
        File file = new File(input_file);
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document document = docBuilder.parse(file);

        document.getDocumentElement().normalize();
        NodeList nList = document.getElementsByTagName("doc");

        HashMap KeywordMap = new HashMap();  //사용할 HashMap
        ArrayList<Keyword_C> keyword_list = new ArrayList<>();   //키워드 객체 리스트로 사용
        boolean check = false;

        for (int temp = 0; temp < nList.getLength(); temp++) {   //nList.getLength() : 5개
            Node nNode = nList.item(temp);
            System.out.println("\nCurrent Element :" + nNode.getNodeName());
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;
                String all_body = eElement.getElementsByTagName("body").item(0).getTextContent();  //body부분 읽어옴
                String body_split[] = all_body.split("#");  //#으로 구분

                for(int i = 0 ; i < body_split.length; i++){
                    String body__split[] = body_split[i].split(":");   //:로 구분
                    if (temp == 0) {  //첫번째 문서는 다 처음이니까 add만 해주가
                        keyword_list.add(new Keyword_C(body__split[0], temp, Integer.parseInt(body__split[1])));
                    }
                    else {
                        Iterator<Keyword_C> it = keyword_list.iterator();
                        while(it.hasNext()){
                            Keyword_C k = it.next();
                            if(k.keyword.equals(body__split[0])){
                                check = true;  //해당 키워드가 이미 리스트에 존재
                                k.doc_fre_set(temp, Integer.parseInt(body__split[1]));  //새 문서를 기준으로 배열 추가
                                break;
                            }
                        }
                        if(!check) {   //리스트 반복 끝났는데도 발견 못한 경우엔 새로 단어를 추가
                            //System.out.println(body__split[0]+"추가");
                            keyword_list.add(new Keyword_C(body__split[0], temp, Integer.parseInt(body__split[1])));
                        }

                        check = false;
                    }
                }
            }
        }

        //hashmap 파일에 객체 저장
        FileOutputStream fileoutstream = new FileOutputStream(output_flie);

        Iterator<Keyword_C> it = keyword_list.iterator();  //리스트에 저장한거 다시 쓰기
        while(it.hasNext()){
            Keyword_C k = it.next();
            KeywordMap.put(k.keyword, k.outputfre());      //리스트에 저장된 내용 해시맴베 추가
        }

        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileoutstream);
        objectOutputStream.writeObject(KeywordMap);
        objectOutputStream.close();

        //(제대로 만들어졌는지 확인)
        FileInputStream fileinstream = new FileInputStream(output_flie);
        ObjectInputStream objectInputStream = new ObjectInputStream(fileinstream);

        Object object = objectInputStream.readObject();
        objectInputStream.close();

        System.out.println("읽어온 객체의 type -> "+ object.getClass());

        HashMap hashMap = (HashMap)object;
        Iterator<String> hit = hashMap.keySet().iterator();

        while(hit.hasNext()){
            String key = hit.next();
            String value = (String)hashMap.get(key);
            System.out.println(key + " -> " + value);
        }

        System.out.println("4주차 실행완료");
    }

}

class Keyword_C {

    String keyword;                   //해당 키워드 문자열
    int doc_count = 0;                //등장하는 문서 수
    int[] fre = {0, 0, 0, 0, 0};           //문서별 빈도수

    public Keyword_C(String keyword, int id, int num){
        this.keyword = keyword;
        fre[id] = num;
        doc_count = doc_count+1;
    }

    public void doc_fre_set(int id, int num){   //문서 아이디랑 빈도수를 입력으로 받아서 저장
        fre[id] = num;
        doc_count = doc_count+1;
    }

    public String outputfre(){                 //최종 문자열 출력
        String s = "";
        for(int i = 0 ; i < 5 ; i++) {
            double ti = fre[i]*Math.log((double) 5/doc_count);
            String sti = String.format("%.2f", ti);
            s = s+i+" "+sti+" ";
        }
        return s;
    }

}