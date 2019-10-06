import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

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

    static Socket socket = new Socket();
    // private static StringBuilder 
    // static PrintWriter socketPrintWriter = new PrintWriter();
    // static BufferedReader socketBufferedReader = new BufferedReader();
    public static void main (String[] args){
        // cmdParser(args);
        // String inputString = String.join(" ", args);
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
     * Helper method that parses the arguments
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
                inLineData = ("\r\n"+args[i+1]);
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
     * @param url
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
        
        if (!hasHeaderData){
            messagBuilder = "GET /"+(arguments)+(" HTTP/1.0\r\n\r\n");
            // messagBuilder = messagBuilder.concat(headerData+"\r\n");
        }  
        else{
            messagBuilder = "GET /"+(arguments)+(" HTTP/1.0\r\n");
            messagBuilder = messagBuilder.concat(headerData+"\r\n");
        }
        // System.out.println(messagBuilder);
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
            System.out.println("ERROR!!!\n"+e.getMessage());
        }
        
    }
    
    /**
     * Executes a HTTP POST request for a given URL with inline data or from file.
     */
    public static void post(String inpuString){
        urlParser(inpuString);
        if (readFromFile){
            //wrtie a method that reads from file and put it in the same 
            //variable inLineData.
        }
        if (hasInLineData && readFromFile){
            System.out.println("Cannot have -d and -f together. Exiting the application.");
            System.exit(1);
        }else if(!hasHeaderData && !hasInLineData && !readFromFile){
            messagBuilder = "POST /"+(arguments)+(" HTTP/1.0\r\n\r\n");
            // messagBuilder = messagBuilder.concat(headerData+"\r\n");
        }  
        else if(hasHeaderData && !hasInLineData){
            messagBuilder = "POST /"+(arguments)+(" HTTP/1.0\r\n");
            messagBuilder = messagBuilder.concat(headerData+"\r\n");
        }else{
            messagBuilder = "POST /"+(arguments)+(" HTTP/1.0\r\n");
            messagBuilder = messagBuilder.concat(headerData);
            messagBuilder = messagBuilder.concat(inLineData+"\r\n");
        }

        System.out.println("message is: \n"+messagBuilder);

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
            System.out.println("ERROR!!!\n"+e.getMessage());
        }

    }
}