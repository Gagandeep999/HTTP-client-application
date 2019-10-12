import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URLEncoder;

public class httpc{
    
    private static boolean isVerbose = false;
    private static boolean isGetRequest = false;
    private static boolean isPostRequest = false;
    private static boolean needHelp = false;
    private static boolean hasHeaderData = false;
    private static boolean hasInLineData = false;
    private static boolean readFromFile = false;
    private static String headerData = "";
    private static String inLineData = "";
    private static String filePath = "";
    private static String url = "";
    private static String hostName = "";
    private static String arguments = "";
    private static String messagBuilder = "";
    private static String[] protocol_host_args = new String[2];
    private static Socket socket = new Socket();

    /**
     * Starting point of the application.
     * @param args cmd arguments.
     */
    public static void main (String[] args){
        if ( args.length == 0){
            System.out.println("\nEnter httpc help to get more information.\n");
        }else{
            cmdParser(args);
        }
        if (needHelp) {
            help();
        }else if(isGetRequest){
            get(url);
        }else if (isPostRequest){
            post(url);
        }
    }

    /**
     * This method takes the cmd args and parses them according to the different conditions of the application.
     * @param args an array of the command line arguments.
     */
    public static void cmdParser(String[] args){
        for (int i =0; i<args.length; i++){
            if (args[i].equalsIgnoreCase("-v")){
                isVerbose = true;
            }else if (args[i].equalsIgnoreCase("-h")){
                hasHeaderData = true;
                headerData = headerData.concat(args[i+1]+"\r\n");
                i++;
            }else if (args[i].equalsIgnoreCase("-d")){
                hasInLineData = true;
                inLineData = (args[i+1]);
                i++;
            }else if (args[i].equalsIgnoreCase("-f")){
                readFromFile = true;
                filePath = (args[i+1]);
                i++;
            }else if (args[i].equalsIgnoreCase("get")){
                isGetRequest = true;
            }else if (args[i].equalsIgnoreCase("post")){
                isPostRequest = true;
            }else if (args[i].equalsIgnoreCase("help")){
                needHelp = true;
            }else{
                url = (args[i]);
            }
       }
    }

    /**
     * this methods parses the url into host and arguments
     * @param url is the url to which the get/post request is made. example - 'httpbin.org/post'
     */
    public static void urlParser(String url) {
        if (url.contains("//")){
            protocol_host_args = url.split("//");
            if (url.contains("/")){
                protocol_host_args = protocol_host_args[1].split("/");
                hostName = protocol_host_args[0];
                arguments = protocol_host_args[1];
            }
        }else if (url.contains("/")){
            protocol_host_args = url.split("/", 2);
            hostName = protocol_host_args[0];
            arguments = protocol_host_args[1];
        }else{
            hostName = url;
        }
    }

    /**
     * This method takes the data provided after the -d option and parses it.
     * @param inLineData is the data from the cmd after -d
     * @return a string that contains the same data but formatted as UTF-8 format
     */
    public static String inLineDataParser(String inLineData) {
        //replaces all whitespace and non-visible character from the inline data
        inLineData = inLineData.replaceAll("\\s", "");
        String param = "";
        if (inLineData.charAt(0)=='{'){
            inLineData = inLineData.substring(1, inLineData.length()-1);
        }
        String[] args_arrayStrings = inLineData.split("&|,|\n");
        try{
            for (String s: args_arrayStrings){
                String[] each_args_arrayStrings = s.split("=|:");
                for (String s1: each_args_arrayStrings){
                    if (s1.charAt(0)=='"'){
                        s1 = s1.substring(1, s1.length()-1);
                    }
                    param = param.concat(URLEncoder.encode(s1, "UTF-8"));
                    param = param.concat("=");
                }
                param = param.substring(0, param.length()-1);
                param = param.concat("&");
            }
        }catch (Exception e){
            System.out.println("Exception in inLineDataParser.\n"+e.getMessage());
        }
        return param.substring(0, param.length() - 1);
        }   
    
    /**
     * This method read data from the file and puts it in the inLineData variable.
     * @param filePath
     * @return a string containing the data from the file
     */
    public static String readingFromFile(String filePath) {
        String line_ = "";
        try{
            File file = new File(filePath);
            BufferedReader input_file = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String line;
            while((line = input_file.readLine()) != null) {
                line_ = line_.concat(line);
            }
            input_file.close();
        }catch(Exception e){
            System.out.println("Exception in readingFromFile!!!"+e.getMessage());
        }
        return line_;
    }
    
    /**
     * This method creates the message that is to be sent over by the socket. 
     * @param requestType either GET or POST
     * @param arguments everything after the .org/"..." or .com/"..."
     * @param hasHeader add to the message only if headers are provided.
     * @param hasData only for the post request. If it has data add it to the message.
     * @return a string that is ready to be send over the socket.
     */
    public static String createMessage(String requestType, String arguments, boolean hasHeader, boolean hasData) {
        String message = "";
        final String HTTP = (" HTTP/1.0\r\n");
        if (requestType=="GET /") {
            message = requestType+arguments+HTTP+"\r\n";
            if (hasHeader){
                message = message.concat(headerData);
            }
        } else {
            message = requestType+arguments+HTTP;
            message = message.concat("Content-Length: "+inLineData.length()+"\r\n");
            if (!hasHeader){
                message = message.concat("\r\n");
            }else{
                message = message.concat(headerData+"\r\n");
            }
            if(hasData){
                message = message.concat(inLineData);
            }
        }
        return message;
    }
    
    /**
     * This is a common method that can be called for both get and post requests.
     */
    public static void sendMessage(String messageBuilder) {
        try {
            socket.connect(new InetSocketAddress(hostName, 80));
            BufferedWriter socketBufferedWriterOutputStream = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader socketBufferedReaderInputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            socketBufferedWriterOutputStream.write(messagBuilder);
            socketBufferedWriterOutputStream.flush();
            String response = " ";

            while ((response = socketBufferedReaderInputStream.readLine()) != null) {
                if ((response.length()==0) && !isVerbose){
                    StringBuilder res_recvd = new StringBuilder();
                    while ((response = socketBufferedReaderInputStream.readLine()) != null){
                        res_recvd.append(response).append("\r\n");
                    }
                    System.out.println(res_recvd.toString());
                    isVerbose = false;
                    break;
                }else if (isVerbose){
                    System.out.println(response);
                }
            }
            socketBufferedWriterOutputStream.close();
            socketBufferedReaderInputStream.close();
            socket.close();
        } catch (Exception e) {
            System.out.println("ERROR from the sendMessage method.\n"+e.getMessage());
        }
    }

    /**
     * Prints the help menu.
     */
    public static void help(){
        String help = "\nhttpc help\n" 
                +"\nhttpc is a curl-like application but supports HTTP protocol only.\n"
                +"Usage:\n"
                +"\t httpc command [arguments]\n"
                +"The commands are:\n"
                +"\t get \t executes a HTTP GET request and prints the response.\n"
                +"\t post \t executes a HTTP POST request and prints the response.\n"
                +"\t help \t prints this screen.\n"
                +"\nUse \"httpc help [command]\" for more information about a command.\n";

        String help_get = "\nhttpc help get\n"
                +"\nusage: httpc get [-v] [-h key:value] URL\n"
                +"\nGet executes a HTTP GET request for a given URL.\n"
                +"\n-v Prints the detail of the response such as protocol, status, and headers.\n"
                +"-h key:value Associates headers to HTTP Request with the format 'key:value'.\n";

        String help_post = "\nhttpc help post\n"
                +"\nusage: httpc post [-v] [-h key:value] [-d inline-data] [-f file] URL\n"
                +"\nPost executes a HTTP POST request for a given URL with inline data or from file.\n"
                +"\n-v Prints the detail of the response such as protocol, status, and headers.\n"
                +"-h key:value Associates headers to HTTP Request with the format 'key:value'.\n"
                +"-d string Associates an inline data to the body HTTP POST request.\n"
                +"-f file Associates the content of a file to the body HTTP POST request.\n"
                +"\nEither [-d] or [-f] can be used but not both.\n";

        if (isPostRequest){
            System.out.println(help_post);
            System.exit(0);
        }else if (isGetRequest){
            System.out.println(help_get);
            System.exit(0);
        }else{
            System.out.println(help);
            System.exit(0);
        }
    }
    
    /**
     * Executes HTTP GET request for a given URL
     */
    public static void get(String inpuString){
        urlParser(inpuString);  

        messagBuilder = createMessage("GET /", arguments, hasHeaderData, false);

        sendMessage(messagBuilder);        
    }
    
    /**
     * Executes a HTTP POST request for a given URL with inline data or from file.
     */
    public static void post(String inpuString){
        urlParser(inpuString);
        
        if (hasInLineData && readFromFile){
            System.out.println("Cannot have -d and -f together. Exiting the application.");
            System.exit(1);
        }
        else if (readFromFile){
            hasInLineData = true;
            inLineData = readingFromFile(filePath);
            inLineData = inLineDataParser(inLineData);
        }
        else if (hasInLineData){
            inLineData = inLineDataParser(inLineData);
        }

        messagBuilder = createMessage("post /", arguments, hasHeaderData, hasInLineData);

        sendMessage(messagBuilder);
    }
}