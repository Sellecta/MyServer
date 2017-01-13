package Select.MyServer;

/**
 * Created by Select on 05.01.2017.
 */
public class MyStringParser {
     protected String parser;

    public MyStringParser(byte[] buffer,int len){
        parser=new String(buffer,0,len-1);
        parser+=' ';

    }

    public MyStringParser(byte[] buffer){
        parser=new String(buffer);
    }

    public String getParserNick(int from,char divChar ){

        return parser.substring(from,parser.indexOf(divChar,from));
    }
    public String genMessenger(String id, int from, char divChar){

        return id+": "+parser.substring(parser.indexOf(divChar,from)+1);
    }

}
