package com.admin;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.db.DBConnection;


public class AdminSignup extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public AdminSignup() {
        super();
    }


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String name = request.getParameter("name");
		String email = request.getParameter("email");
		String mobileno = request.getParameter("mobileno");
		String location = request.getParameter("location");
		String city = request.getParameter("city");
		String taluka = request.getParameter("taluka");
		String password = request.getParameter("password");
		double latitude = Double.parseDouble(request.getParameter("latitude"));
		double longitude = Double.parseDouble(request.getParameter("longitude"));
		
		try {
			Connection con = DBConnection.connect();
			String query = "insert into admin values(?,?,?,?,?,?,?,?,?,?)";
			PreparedStatement stmt = con.prepareStatement(query);
			stmt.setInt(1, 0);
			stmt.setString(2, name);
			stmt.setString(3, email);
			stmt.setString(4, mobileno);
			stmt.setString(5, location);
			stmt.setString(6, city);
			stmt.setString(7, taluka);
			stmt.setString(8, password);
			stmt.setDouble(9, latitude);
			stmt.setDouble(10, longitude);
			int i = stmt.executeUpdate();
			if(i>0)
			{
				response.sendRedirect("index.jsp");
			}
			else{
				response.sendRedirect("signup.jsp");
			}
			
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		
		doGet(request, response);
	}

}
