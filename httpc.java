
//java socket client example
import java.io.*;
import java.net.*;

public class httpc {
    
    public static void main(String[] args) throws IOException {
       
        Socket socket = new Socket();
	    PrintWriter s_out = null;
        BufferedReader s_in = null;

//should have a big condition here to check if it fits any of the formats
//possibly divide them into how long args is and see what formats that lenght could mean.

        //check first to see if its a "help" command
        if(args[0].equals("help")){
            //check if it follows the correct format for help
            if(args.length==2 && (args[1].equals("get")==true || args[1].equals("post")==true)){
                if(args[1].equals("get")){
                    System.out.println("gagan has the info");
                }
                else if(args[1].equals("post")){
                    System.out.println("gagan has the info");
                }
                else{
                System.out.println("could not resolve host");
                }
            }
            else{
                System.out.println("gagan has the info");
            }
        }
        //check if its a "get" command
        else if(args[0].equals("get")){
            try 
            {

                socket.connect(new InetSocketAddress(args[1] , 80));
                System.out.println("Connected");
                    
                //writer for socket
                s_out = new PrintWriter( socket.getOutputStream(), true);
                //reader for socket
                s_in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            }
            //Host not found
            catch (UnknownHostException e) 
            {
                System.err.println("Don't know about host : " + args[1]);
                System.exit(1);
            }
            
            //Send message to server
            String message = "GET / HTTP/1.1\r\n\r\n";
            s_out.println( message );
                
            System.out.println("Message send");
            
            //Get response from server
            String response;
            while ((response = s_in.readLine()) != null) 
            {
                System.out.println( response );
            }
            s_in.close();
            s_out.close();
            socket.close();
        }
        //check if its a "post" command
        else if(args[0].equals("post")){

        }
    }
}
