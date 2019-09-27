
//java socket client example
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class httpc {

    static PrintWriter s_out = null;
    static BufferedReader s_in = null;
    static boolean vMode = false;
    static Socket socket;

    public static void main(String[] args) {
        try {
            run(args);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("error --- unable to run httpc");
        }
    }

    public static void run(String[] args){
        // check first if args is null
        if (args.length == 0) {
            System.out.println("try httpc help for more information");
        }
        else if (args.length == 1 && args[0].equals("help")) {
            getGenericHelpMessage();
        } else if (args.length == 2 && args[0].equals("help")) {
            getSpecificHelpMessage(args[1]);
        } else if (args[0].equals("get")) {
            get(args);
        } else if (args[0].equals("post")) {
        //post(args);
        }
        else{
            getGenericHelpMessage();
        }
    }

    // methode where we connect with the website and try and send it "GET /
    //second iteration must have the -v switch case
    private static void get(String[] args) {

        //check to see if its verbose 
        args=checkMode(args);
        
         String url = args[1].toString();
         String host;
         String specifics;
        if(url.contains("//")){
            String[] before  = url.split("//",2);
            String[] before2 = before[1].split("/", 2);
            if(before2.length == 1) {
                host=before2[0];
                specifics="";
            }
            else{
                host=before2[0];
                specifics=before2[1]; 
            }
        }
        else{
            String[] before  = url.split("/",2);
            if(before.length == 1) {
                host=before[0];
                specifics="";
            }
            else{
                host=before[0];
                specifics=before[1];
            }
        }


       //url needs to be split into different tokens. first big chunk is used when we connect the socket to the Web.
       //second part is then addedto the message we send. that forms the request
        try {

           // InetAddress IP = InetAddress.getByName(url);
            // System.out.println(host);
            socket = new Socket(host,80);
            // System.out.println("connected");
            //writer for socket
            s_out = new PrintWriter(socket.getOutputStream(), true);
            //reader for socket
            s_in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            //Send message to server
            String message = "GET /"+specifics+" HTTP/1.0\r\n\r\n";
            s_out.println( message );
                
            // System.out.println("Message send");
            
            //Get response from server
            getResponse(vMode);

            //close everything
            s_in.close();
            s_out.close();
            socket.close();
        }
            //Host not found
        catch (IOException e) 
        {
            System.err.println("Don't know about host : " + url);
            //System.exit(1);
        }
            
    }
    //based on the lenght of the array, we know the max is 6, so we can play on that to see what options are possibly asked for
    private static String[] checkMode(String[] args) {
        List<String> list = new ArrayList<String>(Arrays.asList(args));
        if(list.contains("-v")){
            list.remove("-v");
            vMode=true;
        }
        //can use this to check other modes also
        // if(list.contains("-h")){
        //     list.remove("-h");
        //     hMode=true;
        // }
        
        args = list.toArray(new String[0]);
        return args;
    }

    private static void getResponse(boolean verbose) throws IOException {
        // System.out.println(verbose);
        String response = s_in.readLine();
        if(!verbose){
            while (!response.isEmpty()) {
                 //System.out.println("skiping response: "+ response);
                response = s_in.readLine();
            }
            while ((response = s_in.readLine()) != null) {
                System.out.println( response );
            }
        }
        else{
            while ((response = s_in.readLine()) != null) {
                System.out.println( response );
            }
        }
    }

    // prints the generic help information
    private static void getGenericHelpMessage() {
        System.out.println("\nhttpc help\n" 
        +"\nhttpc is a curl-like application but supports HTTP protocol only.\n"
        +"Usage:\n"
        +"\t httpc command [arguments]\n"
        +"The commands are:\n"
        +"\t get \t executes a HTTP GET request and prints the response.\n"
        +"\t post \t executes a HTTP POST request and prints the response.\n"
        +"\t help \t prints this screen.\n"
        +"\nUse \"httpc help [command]\" for more information about a command.\n");
    }
    //prints the information for the specified action
    private static void getSpecificHelpMessage(String action) {
        if(action.equals("get")){
            System.out.println("\nhttpc help get\n"
            +"\nusage: httpc get [-v] [-h key:value] URL\n"
            +"\nGet executes a HTTP GET request for a given URL.\n"
            +"\n-v Prints the detail of the response such as protocol, status, and headers.\n"
            +"-h key:value Associates headers to HTTP Request with the format 'key:value'.");
        }
        else if(action.equals("post")){
            System.out.println("\nhttpc help post\n"
            +"\nusage: httpc post [-v] [-h key:value] [-d inline-data] [-f file] URL\n"
            +"\nPost executes a HTTP POST request for a given URL with inline data or from file.\n"
            +"\n-v Prints the detail of the response such as protocol, status, and headers.\n"
            +"-h key:value Associates headers to HTTP Request with the format 'key:value'.\n"
            +"-d string Associates an inline data to the body HTTP POST request.\n"
            +"-f file Associates the content of a file to the body HTTP POST request.\n"
            +"\nEither [-d] or [-f] can be used but not both.\n");
        }
        //if does not match with any of our cases
        else{
            getGenericHelpMessage();
        }
    }
}
