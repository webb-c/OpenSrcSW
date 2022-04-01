package scripts;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;

public class kuir {

    public static void main(String[] args) throws IOException, ParserConfigurationException, TransformerException, SAXException, ClassNotFoundException {

        String command = args[0];
        String path = args[1];

        if(command.equals("-c")) {
            makeCollection collection = new makeCollection(path);
            collection.makeXml();
        }
        else if(command.equals("-k")) {
            makeKeyword keyword = new makeKeyword(path);
            keyword.convertXml();
        }
        else if(command.equals("-i")) {
            indexer post = new indexer(path);
            post.makepost();
        }
        else if(command.equals("-s")) {
            String command2 = args[2];
            String query = args[3];
            searcher sh = new searcher(path);
            if(command2.equals("-q")) sh.CalcSim(query);
            else System.out.println("input format error");
        }
    }
}
