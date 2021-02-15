import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class Servlet
 */
@WebServlet("/Servlet")
public class Servlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private final List<User> userList = new ArrayList<>();

    private boolean userListContainsAddressMAC(String addressMAC) {
        for (User u : userList) {
            if (u.getAddressMAC().equals(addressMAC)) {
                return true;
            }
        }
        return false;
    }

    private boolean userListContainsUsername(String username) {
        for (User u : userList) {
            if (u.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Servlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.getWriter().append("Userlist: " + userList);
	}

	/**
	 * @see HttpServlet#doPut(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String username = request.getParameter("username");
        String addressMAC = request.getParameter("addressMAC");
        String addressIP = request.getRemoteAddr();
        if (username != null && addressIP != null && addressMAC != null && !userListContainsAddressMAC(addressMAC)) {
            try {
            	sendAll(addressMAC + ":newUser:" + username + ":" + addressIP);
                User user = new User(username, InetAddress.getByName(addressIP), addressMAC);
                if (!userListContainsUsername(username)) {
                	userList.add(user);
                    response.getWriter().append("User added");
                } else {
                    user.closeSocket();
                    response.getWriter().append("Username already used");
                }
            } catch (UnknownHostException e) {
                e.printStackTrace();
                response.setStatus(500);
                response.getWriter().print(e.getMessage());
            }
            return;
        }
        response.getWriter().append("User already exist");
	}

	/**
	 * @see HttpServlet#doDelete(HttpServletRequest request, HttpServletResponse response)
	 */
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String addressMAC = request.getParameter("addressMAC");
        for (User u : userList) {
            if (u.getAddressMAC().equals(addressMAC)) {
                userList.remove(u);
                u.closeSocket();
                sendAll(addressMAC + ":removeUser:");
                return;
            }
        }
        response.getWriter().append("User removed");
    }
    
    private void sendAll(String message) {
    	for (User u : userList) {
    		u.write(message);
    	}
    }
}
