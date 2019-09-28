import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

public class httpc{
    
    private static boolean isVerbose = false;
    private static boolean hasHeaderData = false;
    private static boolean hasInLineData = false;
    private static boolean readFromFile = false;
    // private static StringBuilder 
    static Socket socket = new Socket();
    // static PrintWriter socketPrintWriter = new PrintWriter();
    // static BufferedReader socketBufferedReader = new BufferedReader();
    public static void main (String[] args){
        // cmdParser(args);
        String inputString = String.join(" ", args);
        if ( args.length == 0){
            System.out.println("\nEnter httpc help to get more information.\n");
        }else if (args[0].equalsIgnoreCase("get")){
            // get(args, inputString);
        }else if(args[0].equalsIgnoreCase("post")){
            post(args, inputString);
        }else{
            help(inputString);
        }
    }

    /**
     * Helper method that parses the arguments
     */
    private static void cmdParser(String[] args){
        for (int i =0; i<args.length; i++){
            if (args[i].equalsIgnoreCase("-v")){
                isVerbose = true;
            }else if (args[i].equalsIgnoreCase("-h")){
                hasHeaderData = true;
            }else if (args[i].equalsIgnoreCase("-d")){
                hasInLineData = true;
            }else if (args[i].equalsIgnoreCase("-f")){
                readFromFile = true;
            }else{
        
            }
       }
}

    /**
     * Prints the help menu.
     */
    public static void help(String inpuString){
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

        if (inpuString.contains("help get")){
            System.out.println(help_get);
        }else if (inpuString.contains("help post")){
            System.out.println(help_post);
        }else{
            System.out.println(help);
        }
    }
    
    /**
     * Executes HTTP GET request for a given URL
     */
    public static void get(String[] args, String inpuString){
        String[] protocol_host_args = new String[2];
        String hostName = " ";
        String arguments = " ";
        String uRLString = " ";
        if (inpuString.contains(" -v ")){
            isVerbose = true;
            uRLString = args[2];
        }else{
            uRLString = args[1];
        }
        
        if (uRLString.contains("//")){
            protocol_host_args = uRLString.split("//");
            if (uRLString.contains("/")){
                protocol_host_args = protocol_host_args[1].split("/");
                hostName = protocol_host_args[0];
                arguments = protocol_host_args[1];
            }
        }else if (uRLString.contains("/")){
            protocol_host_args = uRLString.split("/", 2);
            hostName = protocol_host_args[0];
            arguments = protocol_host_args[1];
        }else{
            hostName = uRLString;
        }
        System.out.println("hostname: "+hostName);
        System.out.println("arguments: "+arguments);

        try {
            socket.connect(new InetSocketAddress(hostName, 80));
            PrintWriter sockePrintWriterOutputStream = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader socketBufferedReaderInputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String message = "GET /"+arguments+" HTTP/1.0\r\n\r\n";
            sockePrintWriterOutputStream.println(message);
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
            sockePrintWriterOutputStream.close();
            socketBufferedReaderInputStream.close();
            socket.close();
        } catch (Exception e) {
            System.out.println("ERROR!!!\n"+e.getMessage());
        }
        
    }
    
    /**
     * Executes a HTTP POST request for a given URL with inline data or from file.
     */
    public static void post(String[] args, String inpuString){
        String[] protocol_host_args = new String[2];
        String hostName = " ";
        String arguments = " ";
        String uRLString = " ";
        if (inpuString.contains(" -v ")){
            isVerbose = true;
            uRLString = args[2];
        }else{
            uRLString = args[1];
        }
        
        if (uRLString.contains("//")){
            protocol_host_args = uRLString.split("//");
            if (uRLString.contains("/")){
                protocol_host_args = protocol_host_args[1].split("/");
                hostName = protocol_host_args[0];
                arguments = protocol_host_args[1];
            }
        }else if (uRLString.contains("/")){
            protocol_host_args = uRLString.split("/", 2);
            hostName = protocol_host_args[0];
            arguments = protocol_host_args[1];
        }else{
            hostName = uRLString;
        }
        System.out.println("hostname "+hostName);
        System.out.println("argument: "+arguments);

    }
}