package Hinna.WordPress;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

@RestController
public class WordPressAuthController {

    @GetMapping("/wordpress-auth")
    public String showWordPressAuthPopup() {
        return "/wordpress-auth"; // This could also serve as the main login page if needed.
    }

    @GetMapping("/auth/check-username")
    public ResponseEntity<Map<String, Boolean>> checkUsername(@RequestParam String username) {
        boolean exists = checkUserInDatabase(username);
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/wordpress-login")
    public ResponseEntity<Map<String, Object>> handleWordPressLogin(
            @RequestParam("username") String username,
            @RequestParam(value = "password", required = false) String password) {

        Map<String, Object> response = new HashMap<>();

        if (checkUserInDatabase(username)) {
            if (password == null || password.isEmpty()) {
                response.put("success", false);
                response.put("message", "Enter Password");
            } else if (authenticateUser(username, password)) {
                response.put("success", true);
                return ResponseEntity.ok(response); // Send success response
            } else {
                response.put("success", false);
                response.put("message", "Invalid password");
            }
        } else {
            response.put("success", false);
            response.put("message", "Username not found. Please try again.");
        }

        return ResponseEntity.ok(response); // Send failure response
    }

    // Check if the username exists in XML
    private boolean checkUserInDatabase(String username) {
        try {
            File inputFile = new File("src/main/resources/wordpress-users.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName("user");

            for (int i = 0; i < nodeList.getLength(); i++) {
                String xmlUsername = nodeList.item(i).getTextContent().trim();
                if (xmlUsername.equalsIgnoreCase(username.trim())) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Simplified authenticateUser method
    private boolean authenticateUser(String username, String password) {
        try {
            File inputFile = new File("src/main/resources/wordpress-users.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();

            // Get all <user> elements
            NodeList users = doc.getElementsByTagName("user");

            for (int i = 0; i < users.getLength(); i++) {
                Element userElement = (Element) users.item(i); // Ensure it's an Element

                // Extract the <username> and <password> elements for each <user>
                String xmlUsername = userElement.getElementsByTagName("username").item(0).getTextContent().trim();
                String xmlPassword = userElement.getElementsByTagName("password").item(0).getTextContent().trim();

                // Compare with provided username and password
                if (xmlUsername.equalsIgnoreCase(username.trim()) && xmlPassword.equals(password.trim())) {
                    return true; // Username and password match
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false; // Authentication failed
    }
}
