package scripts;

// 네이버 검색 API 예제 - blog 검색
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class ApiExamSearchBlog {


    public static void main(String[] args) throws ParseException {
        String clientId = "yQoHuKKS5RqKrSVI4bf6"; //애플리케이션 클라이언트 아이디값"
        String clientSecret = "mzUOq59WOS"; //애플리케이션 클라이언트 시크릿값"

        /*API 호출에 필요한 정보 입력*/
        //1. 검색할 쿼리를 인코딩 해준다.
        String text = null;
        try {
            //encode() 메소드의 인자로는 검색할 Query(=그린팩토리)와 인코딩방식(UTF-8)이 전달된다.
            text = URLEncoder.encode("그린팩토리", "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("검색어 인코딩 실패",e);
        }

        //앞에서 Encoder로 만든 text를 주소 뒤에 붙여서 API를 호출할 수 있게 함 -> 이를 이용해 해당 쿼리를 검색
        String apiURL = "https://openapi.naver.com/v1/search/blog?query=" + text;    // json 결과
        // String apiURL = "https://openapi.naver.com/v1/search/blog.xml?query="+ text; // xml 결과

        Map<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put("X-Naver-Client-Id", clientId);
        requestHeaders.put("X-Naver-Client-Secret", clientSecret);
        String responseBody = get(apiURL,requestHeaders);  //바로 아래에 메소드 정의 되어있음

        //System.out.println(responseBody);

        /*JSON pars 이용*/
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = (JSONObject) jsonParser.parse(responseBody);  //parsing할 데이터를 넘겨준다. (respoenseBody)
        JSONArray infoArray = (JSONArray) jsonObject.get("items");            //get()에 parsing에 사용할 key를 인자로 준다.

        for(int i=0; i<infoArray.size(); i++){
            System.out.println("=item_"+i+"===========================================");
            JSONObject itemObject = (JSONObject) infoArray.get(i);    //데이터에 저장된 배열의 각 요소를 가져옴
            System.out.println("title:\t"+itemObject.get("title"));   //데이터 안의 각 key에 대한 data를 parsing하여 출력
            System.out.println("link:\t"+itemObject.get("link"));
            System.out.println("description:\t"+itemObject.get("description"));
            System.out.println("bloggername:\t"+itemObject.get("bloggername")+"\n");
        }
    }

    /* get 메소드
    - input :검색 API 주소와, Map 형식으로 저장된 client Id, Secret을 인자로 받음
    - operate1 : HttpURLConnection 객체를 만들고, Map에 저장된 Id, Secret을 이용해 검색을 위한 기본 설정한다.
    - operate2 : 검색 결과 또는, 오류에 대한 결과(에러발생시) String을 저장
    - output : 저장한 검색 결과를 String 타입으로 반환한다.
    */
    private static String get(String apiUrl, Map<String, String> requestHeaders){
        HttpURLConnection con = connect(apiUrl);  //바로 아래에 정의 되어있음
        try {
            con.setRequestMethod("GET");  // setRequestMethod : 검색 결과를 주고받는 방식(GET, POST) 지정
            // - GET : URL에 검색어를 넘겨서 그 결과를 받아오는 방법
            // - POST : URL에는 검색어가 표기되지 않고, stdin & stdout 방식으로 검색어를 전 (표준입출력)
            for(Map.Entry<String, String> header :requestHeaders.entrySet()) {
                con.setRequestProperty(header.getKey(), header.getValue());  //id, secret 설정
            }
            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) { // 정상 호출 (200)
                return readBody(con.getInputStream());       // readBody : 바로 아래에 정의되어있음
            } else { // 에러 발생
                return readBody(con.getErrorStream());
            }
        } catch (IOException e) {
            throw new RuntimeException("API 요청과 응답 실패", e);
        } finally {
            con.disconnect();
        }
    }

    /* connet 메소드
    - input : String 형태의 apiUrl (해당 쿼리 검색을 위해만든 Url)
    - operate : String을 이용해 실제 URL 객체를 생성하고, HttpURLConnection형태로 만들어 반환한다. (+예외처리)
    - output : 해당 url에 대한 HttpURLConnection 객체를 반환
    */
    private static HttpURLConnection connect(String apiUrl){
        try {
            URL url = new URL(apiUrl);    //실제 URL 객체 생성
            return (HttpURLConnection)url.openConnection();  //openConnection : 해당 주소에 대해 Client(나의 앱)와 Server를 연결
        } catch (MalformedURLException e) {
            throw new RuntimeException("API URL이 잘못되었습니다. : " + apiUrl, e);
        } catch (IOException e) {
            throw new RuntimeException("연결이 실패했습니다. : " + apiUrl, e);
        }
    }

    /* readBody 메소드
    - input : 검색되어 Stream에 들어와있는 것 (InputStram)
    - operate : 해당 InputStream 내용을 InputStreamReader를 이용해 읽는다. (+예외처리)
    - output : 읽은 결과를 String 타입으로 반환한다.
    */
    private static String readBody(InputStream body){
        InputStreamReader streamReader = new InputStreamReader(body);

        try (BufferedReader lineReader = new BufferedReader(streamReader)) {
            StringBuilder responseBody = new StringBuilder();
            String line;
            while ((line = lineReader.readLine()) != null) {
                responseBody.append(line);
            }
            return responseBody.toString();
        } catch (IOException e) {
            throw new RuntimeException("API 응답을 읽는데 실패했습니다.", e);
        }
    }
}