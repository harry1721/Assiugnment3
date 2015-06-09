// Hi Harry .... Ur going to be awesome today .... :)
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONArray;





/**
 *
 * Name: Hardik Kulkarni(Harry)
 */
public class pro_servelets  extends HttpServlet{
    private  Connection getConn() {
        Connection conn = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            System.err.println("JDBC Drivers missing" + ex.getMessage());
        }

        try {
            String jdbc = "jdbc:mysql://ipro.lambton.on.ca/inventory";
            conn = DriverManager.getConnection(jdbc, "products", "products");
        } catch (SQLException ex) {
            System.err.println("Connection failed" + ex.getMessage());
        }
        return conn;
    }

    
   private String Results(String query, String... arr) {
     String result=new String();
     try (Connection conn = getConn()) {
            PreparedStatement pstmt = conn.prepareStatement(query);
            for (int i = 1; i <= arr.length; i++) {
                pstmt.setString(i, arr[i - 1]);
            }
            ResultSet rs = pstmt.executeQuery();
            JSONArray productArr = new JSONArray();
            while (rs.next()) {
                Map productMap = new LinkedHashMap();
                productMap.put("productID", rs.getInt("productID"));
                productMap.put("name", rs.getString("name"));
                productMap.put("description", rs.getString("description"));
                productMap.put("quantity", rs.getInt("quantity"));
                productArr.add(productMap);
            }
            result = productArr.toString();
        } catch (SQLException ex) {
            Logger.getLogger(pro_servelets.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result.replace("},", "},\n");
       
     }    
   
   
   
    @Override
   protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        Set<String> kset = request.getParameterMap().keySet();
        try (PrintWriter out = response.getWriter()) {
            Connection conn = getConn();
            if (kset.contains("name") && kset.contains("description") && kset.contains("quantity")) {
                PreparedStatement pstmt = conn.prepareStatement("INSERT INTO `products`(`productID`, `name`, `description`, `quantity`) "
                        + "VALUES (null, '" + request.getParameter("name") + "', '"
                        + request.getParameter("description") + "', "
                        + request.getParameter("quantity") + ");"
                );
                try {
                    pstmt.executeUpdate();
                    request.getParameter("productID");
                    doGet(request, response);
                } catch (SQLException ex) {
                    Logger.getLogger(pro_servelets.class.getName()).log(Level.SEVERE, null, ex);
                    out.println("Data inserted Error while retriving data.");
                }
            } else {
                out.println("Error: Not enough data to input");
            }
        } catch (SQLException ex) {
            Logger.getLogger(pro_servelets.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
   
    @Override
   protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        Set<String> kset = request.getParameterMap().keySet();
        try (PrintWriter out = response.getWriter()) {
            Connection conn = getConn();
            if (kset.contains("productID") && kset.contains("name") && kset.contains("description") && kset.contains("quantity")) {
                PreparedStatement pstmt = conn.prepareStatement("UPDATE `products` SET `name`='"
                        + request.getParameter("name") + "',`description`='"
                        + request.getParameter("description")
                        + "',`quantity`=" + request.getParameter("quantity")
                        + " WHERE `productID`=" + request.getParameter("productID"));
                try {
                    pstmt.executeUpdate();
                    doGet(request, response); //shows updated row
                } catch (SQLException ex) {
                    Logger.getLogger(pro_servelets.class.getName()).log(Level.SEVERE, null, ex);
                    out.println("Error putting values.");
                }
            } else {
                out.println("Error: Not enough data to update");
            }
        } catch (SQLException ex) {
            Logger.getLogger(pro_servelets.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
   
    @Override
   protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        try (PrintWriter out = response.getWriter()) {
            if (!request.getParameterNames().hasMoreElements()) {
                out.println(Results("SELECT * FROM products"));
            } else {
                int productID = Integer.parseInt(request.getParameter("productID"));
                out.println(Results("SELECT * FROM products WHERE productID = ?", String.valueOf(productID)));
            }
        } catch (IOException ex) {
            response.setStatus(500);
            Logger.getLogger(pro_servelets.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
   
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Set<String> key = request.getParameterMap().keySet();
        try (PrintWriter out = response.getWriter()) {
            Connection conn = getConn();
            if (key.contains("productID")) {
                PreparedStatement pstmt = conn.prepareStatement("DELETE FROM `products` WHERE `productID`=" + request.getParameter("productID"));
                try {
                    pstmt.executeUpdate();
                } catch (SQLException ex) {
                    Logger.getLogger(pro_servelets.class.getName()).log(Level.SEVERE, null, ex);
                    out.println("Error in deleting the product.");
                }
            } else {
                out.println("Error: in data to delete");
            }
        } catch (SQLException ex) {
            Logger.getLogger(pro_servelets.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

