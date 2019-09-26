import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

public class httpc{
    
    static boolean verboseMode = false;
    static Socket socket = new Socket();
    public static void main (String[] args){
        // Scanner cmdScanner = new Scanner(System.in);
        String inputString = String.join(" ", args);
        if (inputString.contains("get")){
            get(args, inputString);
        }else if(inputString.contains("post")){
            post(args, inputString);
        }else{
            help(inputString);
        }
    }

    /**
     * 
     * @param arg_help
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
                +"-h key:value Associates headers to HTTP Request with the format 'key:value'.";

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
     * 
     */
    public static void get(String[] args, String inpuString){
        if (inpuString.contains(" -v ")){
            verboseMode = true;
        }
        String uRLString = args[1];
        System.out.println(uRLString);
        try {
            socket.connect(new InetSocketAddress(uRLString, 80));
            System.out.println("connected");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        

    }
    /**
     * 
     */
    public static void post(String[] args, String inpuString){

    }
}