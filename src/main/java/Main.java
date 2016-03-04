import java.sql.*;
import java.util.*;
import org.json.JSONObject;

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

    get("/api/invlist2", (req, res) -> {
      Connection connection = null;
      Map<String, Object> attributes = new HashMap<>();
      try {
        connection = DatabaseUrl.extract().getConnection();

        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM inventory");

        ArrayList<String> output = new ArrayList<String>();
          while (rs.next()) { 
            output.add(rs.getString("owner"));
            output.add(rs.getString("manufacturer"));
            output.add(rs.getString("model"));
            output.add(rs.getString("ip"));
            output.add(rs.getString("serial"));
            output.add(rs.getString("processor"));
            output.add(rs.getString("ram"));
            output.add(rs.getString("location"));
          }

          attributes.put("results", output);
          return attributes;
        } catch (Exception e) {
          attributes.put("message", "There was an error: " + e);
          return attributes;
        } finally {
          if (connection != null) try{connection.close();} catch(SQLException e){}
        }
      }, gson::toJson);
    
    get("/api/invlist", (req, res) -> {
      Connection connection = null;
      Map<String, Object> attributes = new HashMap<>();
      try {
        connection = DatabaseUrl.extract().getConnection();

        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM inventory, type where type=type_id");
        
        List<JSONObject> resList = new ArrayList<JSONObject>();
        
        // get column names
        ResultSetMetaData rsMeta = rs.getMetaData();
        int columnCnt = rsMeta.getColumnCount();
        List<String> columnNames = new ArrayList<String>();
        for(int i=1;i<=columnCnt;i++) {
            columnNames.add(rsMeta.getColumnName(i).toUpperCase());
        }
        
        //get data
        while(rs.next()) { // convert each object to an human readable JSON object
            JSONObject obj = new JSONObject();
            for(int i=1;i<=columnCnt;i++) {
                String key = columnNames.get(i - 1);
                String value = rs.getString(i);
                obj.put(key, value);
            }
            resList.add(obj);
        }
        return resList;
          
        } catch (Exception e) {
          attributes.put("message", "There was an error: " + e);
          return attributes;
        } finally {
          if (connection != null) try{connection.close();} catch(SQLException e){}
        }
      });
  
  }

}
