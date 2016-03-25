import java.sql.*;
import java.util.*;
import org.json.JSONObject;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;
import javax.xml.transform.dom.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.net.URI;
import java.net.URISyntaxException;

import static spark.Spark.*;
import spark.template.freemarker.FreeMarkerEngine;
import spark.ModelAndView;
import static spark.Spark.get;

import static javax.measure.unit.SI.KILOGRAM;
import javax.measure.quantity.Mass;
import org.jscience.physics.model.RelativisticModel;
import org.jscience.physics.amount.Amount;

import com.heroku.sdk.jdbc.DatabaseUrl;
import com.google.gson.Gson;
import java.util.List;

public class Main {

  public static void main(String[] args) {
      
    Gson gson = new Gson();
      
    port(Integer.valueOf(System.getenv("PORT")));
    staticFileLocation("/public");

    get("/hello", (req, res) -> {
      RelativisticModel.select();
      String energy = System.getenv().get("ENERGY");

      Amount<Mass> m = Amount.valueOf(energy).to(KILOGRAM);
      return "E=mc^2: " + energy + " = " + m.toString();
    });

    
    get("/", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("message", "Hello World!");

            return new ModelAndView(attributes, "index.ftl");
        }, new FreeMarkerEngine());

    
    
    
    get("/db", (req, res) -> {
      Connection connection = null;
      Map<String, Object> attributes = new HashMap<>();
      try {
        connection = DatabaseUrl.extract().getConnection();

        Statement stmt = connection.createStatement();
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS ticks (tick timestamp)");
        stmt.executeUpdate("INSERT INTO ticks VALUES (now())");
        ResultSet rs = stmt.executeQuery("SELECT tick FROM ticks");

        ArrayList<String> output = new ArrayList<String>();
        while (rs.next()) {
          output.add( "Read from DB: " + rs.getTimestamp("tick"));
        }

        attributes.put("results", output);
        return new ModelAndView(attributes, "db.ftl");
      } catch (Exception e) {
        attributes.put("message", "There was an error: " + e);
        return new ModelAndView(attributes, "error.ftl");
      } finally {
        if (connection != null) try{connection.close();} catch(SQLException e){}
      }
    }, new FreeMarkerEngine());
    
    
    
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
        // get column names to attach to each value
        ResultSetMetaData rsMeta = rs.getMetaData();
        int columnCnt = rsMeta.getColumnCount();
        List<String> columnNames = new ArrayList<String>();
        for(int i=1;i<=columnCnt;i++) {
            columnNames.add(rsMeta.getColumnName(i).toUpperCase());
        }
        
        //get data or values and attach column name
        while(rs.next()) { // convert each object to an human readable JSON object
            JSONObject obj = new JSONObject();
            for(int i=1;i<=columnCnt;i++) {
                String key = columnNames.get(i - 1);
                String value = rs.getString(i);
                obj.put(key, value);
            }
            resList.add(obj); //Add to ArrayList
        }
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
        
        //Get column count of rs table
        ResultSetMetaData rsmd = rs.getMetaData();
        int colCount = rsmd.getColumnCount();
        
        //create new document
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.newDocument();
        Element results = doc.createElement("Results");
        doc.appendChild(results);
        
        //create each row as xml with column name tags
        while (rs.next()) {
            Element row = doc.createElement("Row");
            results.appendChild(row);
                for (int ii = 1; ii <= colCount; ii++) {
                    String columnName = rsmd.getColumnName(ii);
                    Object value = rs.getObject(ii);
                    Element node = doc.createElement(columnName);
                    node.appendChild(doc.createTextNode(value.toString()));
                    row.appendChild(node);
                }//end for
        }//end while
      
        //output xml document
        return (getDocumentAsXml(doc));
          
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
        
        //Get data from inventory sql table
        ArrayList<String> output = new ArrayList<String>();
          while (rs.next()) { 
            output.add(rs.getString("owner"));
            output.add(rs.getString("manufacturer"));
            output.add(rs.getString("model"));
            output.add(rs.getString("ip_address"));
            output.add(rs.getString("serial"));
            output.add(rs.getString("location"));
          }
        
        //push data to freemarker page
        attributes.put("results", output);
        return new ModelAndView(attributes, "printers.ftl");
      } catch (Exception e) {
        attributes.put("message", "There was an error: " + e);
        return new ModelAndView(attributes, "error.ftl");
      } finally {
        if (connection != null) try{connection.close();} catch(SQLException e){}
      }
    }, new FreeMarkerEngine());
    
  }//end Main

  public static String getDocumentAsXml(Document doc)
      throws TransformerConfigurationException, TransformerException {
    
    //Add the XML formatting to doc
    DOMSource domSource = new DOMSource(doc);
    TransformerFactory tf = TransformerFactory.newInstance();
    Transformer transformer = tf.newTransformer();
    transformer.setOutputProperty(OutputKeys.METHOD, "xml");
    transformer.setOutputProperty(OutputKeys.ENCODING,"ISO-8859-1");
    transformer.setOutputProperty
       ("{http://xml.apache.org/xslt}indent-amount", "4");
    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    java.io.StringWriter sw = new java.io.StringWriter();
    StreamResult sr = new StreamResult(sw);
    transformer.transform(domSource, sr);
    return sw.toString();
  }//end getDocumentAsXML
}//End class Main
