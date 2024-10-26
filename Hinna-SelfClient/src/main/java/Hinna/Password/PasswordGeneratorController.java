package Hinna.Password;

import org.springframework.web.bind.annotation.*;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.File;
import java.util.Random;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/wordpress-auth")
public class PasswordGeneratorController {

    // Check if the username exists
    @PostMapping("/check-username")
    public Map<String, Boolean> checkUsername(@RequestParam String username) {
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", checkUserInDatabase(username));
        return response;
    }

    // Validate the password for an existing user
    @PostMapping("/validate-password")
    public String validatePassword(@RequestParam String username, @RequestParam String password) {
        return (checkUserPassword(username, password)) ? "success" : "failure";
    }

    @PostMapping("/create-account")
    public String createAccount(@RequestParam String username) {
        String newPassword = generateRandomPassword(10);
        createUserInDatabase(username, newPassword);
        return newPassword; // Return password as plain text
    }

    // Check if the username exists in the XML database
    private boolean checkUserInDatabase(String username) {
        try {
            File inputFile = new File("/path/to/database.xml");  // Adjust path to your XML file
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();

            NodeList nList = doc.getElementsByTagName("user");
            for (int i = 0; i < nList.getLength(); i++) {
                String xmlUsername = nList.item(i).getTextContent().split(":")[0].trim();
                if (xmlUsername.equalsIgnoreCase(username.trim())) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Check if the password matches for the existing user
    private boolean checkUserPassword(String username, String password) {
        try {
            File inputFile = new File("/path/to/database.xml");  // Adjust path to your XML file
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();

            NodeList nList = doc.getElementsByTagName("user");
            for (int i = 0; i < nList.getLength(); i++) {
                String[] userRecord = nList.item(i).getTextContent().trim().split(":");
                String xmlUsername = userRecord[0];
                String xmlPassword = userRecord[1];
                if (xmlUsername.equalsIgnoreCase(username.trim()) && xmlPassword.equals(password.trim())) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Create a new user in the XML "database"
    private void createUserInDatabase(String username, String password) {
        try {
            File inputFile = new File("/path/to/database.xml");  // Adjust path to your XML file
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();

            Element rootElement = doc.getDocumentElement();
            Element newUser = doc.createElement("user");
            newUser.appendChild(doc.createTextNode(username + ":" + password));

            rootElement.appendChild(newUser);

            // Save the updated XML file (implement your XML save logic here)

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Generate a random password
    private String generateRandomPassword(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }
        return sb.toString();
    }
}
