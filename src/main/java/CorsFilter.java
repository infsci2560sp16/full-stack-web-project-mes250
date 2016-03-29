
import java.util.HashMap;
import spark.Filter;
import spark.Request;
import spark.Response;
import spark.Spark;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author mes250
 */
public class CorsFilter {
     private static final HashMap<String, String> corsHeaders = new HashMap<String, String>();
    
    static {
        corsHeaders.put("Access-Control-Allow-Methods", "GET,PUT,POST,DELETE,OPTIONS");
        corsHeaders.put("Access-Control-Allow-Origin", "http://stark-earth-7570.herokuapp.com");
        //corsHeaders.put("Access-Control-Allow-Origin", "http://localhost:5000");
        corsHeaders.put("Access-Control-Allow-Headers", "Content-Type,Authorization,X-Requested-With,Content-Length,Accept,Origin,");
        corsHeaders.put("Access-Control-Allow-Credentials", "true");
    }

    public final static void apply() {
        Filter filter = new Filter() {
            @Override
            public void handle(Request request, Response response) throws Exception {
                response.status(200);
                corsHeaders.forEach((key, value) -> {
                    response.header(key, value);
                    
                });
            }
        };
        Spark.after(filter);
    }
}
