package com.admin;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

import com.db.DBConnection;

public class Conferm extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public Conferm() {
        super();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int uid = User.getId();
        int eid = Integer.parseInt(request.getParameter("eid"));
        float totalamount = Float.parseFloat(request.getParameter("totalamount"));

        String uname =null;
        System.out.println(uname);
        String ucontact = User.getMobileno();
        String location = User.getLocation();
        String rdate = request.getParameter("rdate");
        String userEmail = null;

        try {
            // Fetch user's email from the database using uid
            Connection con = DBConnection.connect();
            String query = "SELECT uemail,uname FROM user WHERE uid=?";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setInt(1, uid);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                userEmail = rs.getString("uemail");
                uname=rs.getString("uname");
            }

            // Fetch equipment and admin details
            query = "SELECT equipment.ename, admin.aname, admin.aid FROM equipment JOIN admin ON equipment.aid = admin.aid WHERE eid=?";
            stmt = con.prepareStatement(query);
            stmt.setInt(1, eid);
            rs = stmt.executeQuery();
            String ename = null;
            String aname = null;
            
            int aid = 0;
            if (rs.next()) {
                ename = rs.getString("ename");
                aname = rs.getString("aname");
                aid = rs.getInt("aid");
            }

            // Insert order details into confermorder table
            query = "INSERT INTO confermorder VALUES(?,?,?,?,?,?,?,?,?,?,?)";
            stmt = con.prepareStatement(query);
            stmt.setInt(1, 0);
            stmt.setInt(2, eid);
            stmt.setInt(3, aid);
            stmt.setInt(4, uid);
            stmt.setString(5, ename);
            stmt.setString(6, aname);
            stmt.setString(7, uname);
            stmt.setString(8, location);
            stmt.setString(9, ucontact);
            stmt.setFloat(10, totalamount);
            stmt.setString(11, rdate);
            stmt.executeUpdate();

            // Update equipment availability
            query = "UPDATE equipment SET eavailable = 'unavailable' WHERE eid = ?";
            stmt = con.prepareStatement(query);
            stmt.setInt(1, eid);
            int i = stmt.executeUpdate();
            if (i > 0) {
                response.sendRedirect("payment.html");

                // Send email to user
                sendEmail(userEmail, ename, aname, totalamount, rdate,uname);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to send email
    private void sendEmail(String userEmail, String ename, String aname, float totalamount, String rdate,String uname) {
        // Sender's email and password
    	 String senderEmail = "jayeshsonavane2025@gmail.com";
         String senderPassword = "toev zgsx rggo rrgy";
        
        // SMTP server configuration
        Properties prop = new Properties();
        prop.put("mail.smtp.host", "smtp.gmail.com"); // Update with SMTP server host
        prop.put("mail.smtp.port", "587"); // Update with SMTP server port
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true");

        // Email session
        Session session = Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });
        

        try {
            // Compose message
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(userEmail));
            message.setSubject("Order Confirmation");

            
			// Email body
            String emailContent = "Dear "+ uname + "\n\nYour order has been confirmed:\n\n"
                    + "Equipment: " + ename + "\nOwner: " + aname + "\nTotal Amount: " + totalamount
                    + "\nRental Date: " + rdate + "\n\nThank you for choosing our service..!";

            message.setText(emailContent);

            // Send email
            Transport.send(message);
            System.out.println("Email sent successfully.");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}





