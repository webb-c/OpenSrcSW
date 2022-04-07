package scripts;


import org.snu.ids.kkma.index.Keyword;
import org.snu.ids.kkma.index.KeywordExtractor;
import org.snu.ids.kkma.index.KeywordList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import sun.util.resources.cldr.zh.CalendarData_zh_Hans_SG;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

/**
 * 5주차 실습 코드
 *
 * 주어진 입력을 분석해서 내적을 기반으로 유사도 분석후 상위 3개의 문서 title을 출력하라.
 *
 * input : index.post & "질의어"
 * output : 유사한 문서 title
 */

public class searcher {

    private String input_file;
    private String query;
    private String output_String;

    public searcher(String path) {
        this.input_file = path;
    }

    public void CalcSim(String query){

    }

    public float InnerProduct(String query, int id) throws IOException, ClassNotFoundException, ParserConfigurationException, SAXException {
        this.query = query;

        HashMap<String, Integer> hashMap_query = new HashMap<String, Integer>();
        String keywordString = "";
        KeywordExtractor ke = new KeywordExtractor();                         //추출에 사용되는 객체
        KeywordList k = ke.extractKeyword(query, true);

        for (int i = 0; i < k.size(); i++) {
            Keyword kwrd = k.get(i);
            hashMap_query.put(kwrd.getString(), kwrd.getCnt());
        }

        //post 파일 읽기
        FileInputStream fileinstream = new FileInputStream(input_file);
        ObjectInputStream objectInputStream = new ObjectInputStream(fileinstream);

        Object object = objectInputStream.readObject();
        objectInputStream.close();

        HashMap hashMap = (HashMap) object;

        int count = 0;
        float id_doc[] = {0, 0, 0, 0, 0};  //각각의 문서별 유사도를 저장할 배열
        float numdata[] = new float[10];

        Iterator<String> it = hashMap_query.keySet().iterator();
        while (it.hasNext()) {
            //정상 작동
            String key_q = it.next();
            //System.out.println(key_q);

            Iterator<String> hit = hashMap.keySet().iterator();
            while (hit.hasNext()) {
                String key_p = hit.next();
                //System.out.println("현재 쿼리 키워드 : "+key_q +"post 키워드 : "+key_p);
                if (key_q.equals(key_p)) {   //동일한 키워드를 찾았을 때

                    count++;
                    String data = (String) hashMap.get(key_p);    //그 post가 갖는 빈도수 관련 내용 추출
                    int weight = hashMap_query.get(key_q);
                    String[] database = data.split(" ");
                    for (int i = 0; i < database.length; i++) {
                        numdata[i] = Float.parseFloat(database[i]);
                    }
                    for (int i = 0; i < 5; i++) {
                        id_doc[i] = id_doc[i] + numdata[1 + 2 * i] * weight;
                    }
                    break;
                }
            }
        }
        return id_doc[id];
    }
}

class Typeforsort implements Comparable {
    private int id;
    private float sim;

    public Typeforsort(int id, float sim){
        this.id = id;
        this.sim = sim;
    }

    public int getid(){
        return id;
    }

    public float getsim(){
        return sim;
    }

    @Override
    public int compareTo(Object o) {
        Typeforsort t = (Typeforsort)o;
        if(this.sim < t.sim) return 1;
        else if(this.sim == t.sim) {
            if(this.id < t.id) return -1;
            else return 1;
        }
        else return -1;
    }
}