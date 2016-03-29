import java.sql.*;
import java.util.*;
import org.json.JSONObject;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import static spark.Spark.*;
import spark.template.freemarker.FreeMarkerEngine;
import spark.ModelAndView;
import static spark.Spark.get;

import com.heroku.sdk.jdbc.DatabaseUrl;
import java.util.List;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {

  public static void main(String[] args) {
          
    port(Integer.valueOf(System.getenv("PORT")));
    staticFileLocation("/public");
    
    //Apply headers across all routers for CORS
    CorsFilter.apply();
    
    //Fix CORS when posting by adding options respone for Preflight check
    options("/*", (request,response)->{
 
        String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
        if (accessControlRequestHeaders != null) {
            response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
        }
 
        String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
        if(accessControlRequestMethod != null){
            response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
        }
 
        return "OK";
    });//end options


    get("/", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("message", "Hello World!");

            return new ModelAndView(attributes, "index.ftl");
        }, new FreeMarkerEngine()
    );//end root
    
    
    get("/api/invlist", (req, res) -> {
        Connection connection = null;
        res.type("application/json"); //Return as JSON
      
        Map<String, Object> attributes = new HashMap<>();
        try {
            //Connect to Database and execute SQL Query
            connection = DatabaseUrl.extract().getConnection();
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM inventory, type where type=type_id");

            List<JSONObject> resList = new ArrayList<JSONObject>();
            
            //get column names to attach to each value
            ResultSetMetaData rsMeta = rs.getMetaData();
            int columnCnt = rsMeta.getColumnCount();
            List<String> columnNames = new ArrayList<String>();
            for(int i=1;i<=columnCnt;i++) {
                columnNames.add(rsMeta.getColumnName(i).toUpperCase());
            }//end for
        
            //get data or values and attach column name
            while(rs.next()) { // convert each object to an human readable JSON object
                JSONObject obj = new JSONObject();
                for(int i=1;i<=columnCnt;i++) {
                    String key = columnNames.get(i - 1);
                    String value = rs.getString(i);
                    obj.put(key, value);
                }//end for
                resList.add(obj); //Add to ArrayList
            }//end while

            //Return JSON
            return resList;
          
        } catch (Exception e) {
            attributes.put("message", "There was an error: " + e);
            return attributes;
        } finally {
            if (connection != null) try{connection.close();} catch(SQLException e){}
        }
      });//End api/invlist
    
    
    get("/api/invlistXML", (req, res) -> {
        Connection connection = null;
        res.type("application/xml"); //Return as XML
      
        Map<String, Object> attributes = new HashMap<>();
        try {
            //Connect to Database and execute SQL Query
            connection = DatabaseUrl.extract().getConnection();
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM inventory, type where type=type_id");

            //Get column count of resultset
            ResultSetMetaData rsmd = rs.getMetaData();
            int colCount = rsmd.getColumnCount();

            //create new document
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();        
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();

            //create new root element for sql results
            Element results = doc.createElement("inventory");
            doc.appendChild(results);

            //create each row as <device> and make column name tags as element
            while (rs.next()) {
                Element row = doc.createElement("device");
                results.appendChild(row);
                    for (int ii = 1; ii <= colCount; ii++) {
                        String columnName = rsmd.getColumnName(ii);
                        Object value = rs.getObject(ii);
                        Element node = doc.createElement(columnName);
                        node.appendChild(doc.createTextNode(value.toString()));
                        row.appendChild(node);
                    }//end for
            }//end while

            //Add name space to root element inventory
            Element documentElement = doc.getDocumentElement();
            documentElement.setAttribute("xmlns", "http://stark-earth-7570.herokuapp.com/schema/inventory");
            documentElement.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
            documentElement.setAttribute("xsi:schemaLocation", "http://stark-earth-7570.herokuapp.com/schema/inventory http://stark-earth-7570.herokuapp.com/schema/inventory/inventory.xsd");

            //Finish formatting as XML and then return XML
            return (CreateXml.getDocumentAsXml(doc));
          
        } catch (Exception e) {
            attributes.put("message", "There was an error: " + e);
            return attributes;
        } finally {
            if (connection != null) try{connection.close();} catch(SQLException e){}
        }
      });//End api/invlistXML
  
    
    get("/printers", (req, res) -> {
        Connection connection = null;
        Map<String, Object> attributes = new HashMap<>();
        try {
            connection = DatabaseUrl.extract().getConnection();

            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM inventory where type=8");

            //Get data from inventory table
            ArrayList<String> output = new ArrayList<String>();
              while (rs.next()) { 
                output.add(rs.getString("owner"));
                output.add(rs.getString("manufacturer"));
                output.add(rs.getString("model"));
                output.add(rs.getString("ip_address"));
                output.add(rs.getString("serial"));
                output.add(rs.getString("location"));
              }//end while

            //push data to freemarker printers page
            attributes.put("results", output);
            return new ModelAndView(attributes, "printers.ftl");
        } catch (Exception e) {
            attributes.put("message", "There was an error: " + e);
            return new ModelAndView(attributes, "error.ftl");
        } finally {
            if (connection != null) try{connection.close();} catch(SQLException e){}
        }
    }, new FreeMarkerEngine()
    ); //end printers
    
    
    get("/schema/inventory/inventory.xsd", (req, res) -> {
        //Used this make Schema avilable for verification
        
        Map<String, Object> attributes = new HashMap<>();
        res.type("application/xml"); //Return as XML
        try {
            //Simply read file to page
            String content = new String(Files.readAllBytes(Paths.get("src/main/resources/public", "inventory.xsd")));
      
        return content; 
          
        } catch (Exception e) {
            attributes.put("message", "There was an error: " + e);
            return attributes;
        } finally {
          
        }
      });//End api/inventory.xsd
    
      
    post("/api/invadd", (req, res) -> {
        Connection connection = null;
      
        //**For Testing**
        //System.out.println(req.body());
      
        try {
            //Get JSON form data and create Java object
            JSONObject obj = new JSONObject(req.body());
            
            //Pull out values from data
            String owner = obj.getString("OWNER");
            String device_name = obj.getString("DEVICE_NAME");
            String manufacturer = obj.getString("MANUFACTURER");
            String model = obj.getString("MODEL");
            int type = obj.getInt("TYPE");
            String serial = obj.getString("SERIAL");
            String ip_address = obj.getString("IP_ADDRESS");
            String processor = obj.getString("PROCESSOR");
            int ram = obj.getInt("RAM");
            String location = obj.getString("LOCATION");

             //**For Testing**
    //       System.out.println(owner);
    //       System.out.println(device_name);
    //       System.out.println(manufacturer);
    //       System.out.println(model);
    //       System.out.println(type);
    //       System.out.println(serial);
    //       System.out.println(ip_address);
    //       System.out.println(processor);
    //       System.out.println(ram);
    //       System.out.println(location);
            
            //Create sql insert statement
            String sql = "INSERT INTO inventory (owner, device_name, manufacturer, model, type, serial, ip_address, processor, ram, location) VALUES ('" 
               + owner + "','" + device_name + "','" + manufacturer + "','" + model + "'," + type + ",'" + serial 
               + "','" + ip_address + "','" + processor + "'," + ram +",'" + location + "')";
       
            //**For Testing**
            //System.out.println(sql);
            
            //Connect to Database
            connection = DatabaseUrl.extract().getConnection();
            Statement stmt = connection.createStatement();
            stmt.executeUpdate(sql); 
            
            //Return OK status and JSON for AJAX success
            res.status(200);
            return req.body();
        } catch (Exception e) {
            res.status(500);
            return e.getMessage();
        } finally {
            res.status(500);
            if (connection != null) try{connection.close();} catch(SQLException e){}
        }
    });//end /api/invadd
  }//end Main
}//End class Main
