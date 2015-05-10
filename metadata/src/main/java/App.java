import com.echonest.api.v4.EchoNestException;

/**
 * Created by douglas.calderon on 5/9/2015.
 */
public class App
{
    public static void main(String[] args){
        System.out.println( "Hello World!" );
        System.out.println(
                EchonestFetcher.getGenres("Surrender","Cash Cash")
        );
    }
}
