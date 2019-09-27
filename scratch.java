public class scratch{
    public static void main(String[] args) {
        String url = "http://httpbin.org/get?course=networking&assignment=1";
        String[] before  = url.split("//");
        for (String s: before){
            System.out.println(s);
        }
        // before[1] contains httpbin.org/get?course=networking&assignment=1
        String[] before2 = before[1].split("/", 2);
        for (String s: before2){
            System.out.println(s);
        }
    }
}