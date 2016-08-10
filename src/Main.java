import org.springframework.security.crypto.codec.Hex;
import org.springframework.security.crypto.password.HackedStandardPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;
//import org.springframework.security.crypto.password.StandardPasswordEncoder;




public class Main {
    public static void main(String[] args) {
        System.out.println("HELLO THIS IS A PROGRAM FOR VERIFYING HASHES");

        String password = "abc123";
        PasswordEncoder passwordEncoder = new HackedStandardPasswordEncoder("eRp8tqtnWCbQ9t9NgTOC");
        String encodedPassword = passwordEncoder.encode(password);
        String salt = encodedPassword.substring(0,16);
        System.out.println("Salt generated was: "+salt);
        System.out.println("encodedPassword: "+ encodedPassword.substring(16));
        runNode(salt, password, 1, encodedPassword.substring(16));


    }


    public static void runNode(String salt, String password, int iterations, String result) {

        Runtime rt = Runtime.getRuntime();
        String[] commands = {"node","code.js", password, salt, iterations+"", result};
        Process proc = null;
        try {
            proc = rt.exec(commands);


            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(proc.getInputStream()));

            BufferedReader stdError = new BufferedReader(new
                    InputStreamReader(proc.getErrorStream()));

// read the output from the command
            System.out.println("Here is the standard output of the command:\n");
            String s = null;
            while ((s = stdInput.readLine()) != null) {
                System.out.println(s);
            }

// read any errors from the attempted command
            System.out.println("Here is the standard error of the command (if any):\n");
            while ((s = stdError.readLine()) != null) {
                System.out.println(s);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
