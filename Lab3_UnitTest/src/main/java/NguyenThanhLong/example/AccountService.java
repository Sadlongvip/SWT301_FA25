package NguyenThanhLong.example;

import java.util.regex.Pattern;

public class AccountService {
    public boolean registerAccount(String username,String password,String email){
        if (username == null || username.trim().isEmpty()) {
            System.out.println("Username is null or empty");
            return false;
        } else if (username.trim().length() < 3) {
            System.out.println("Username is too short");
            return false;
        }
        // password require more 6 char
        if (password == null || password.length() < 6) {
            System.out.println("Password is less than 6 characters");
            return false;
        }
        if (email == null || email.trim().isEmpty()) {
            System.out.println("Email is null or empty");
            return false;
        }
        if(!isValidEmail(email)){
            System.out.println("Email wrong format");
            return false;
        }

        System.out.println("Successfully registered account");
        return true;
    }
    private static boolean isValidEmail(String email){
        Pattern EMAIL_PATTERN = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }
}
