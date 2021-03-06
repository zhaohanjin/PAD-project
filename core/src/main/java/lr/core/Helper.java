package lr.core;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import lr.core.CmdArgs.BaseArgs;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by luca on 04/03/16.
 */
public class Helper {
    private static byte[] MD5(String s) {
        MessageDigest m = null;
        try {
            m = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        m.reset();
        m.update(s.getBytes());
        return m.digest();
    }

    public static String MD5ToString(String s) {
        byte[] digest = MD5(s);
        BigInteger bigInt = new BigInteger(1, digest);
        String hashtext = bigInt.toString(16);
        while (hashtext.length() < 32) {
            hashtext = "0" + hashtext;
        }
        return hashtext;
    }

    public static int MD5ToInt(String s) {
        byte[] digest = MD5(s);
        BigInteger bigInt = new BigInteger(1, digest);
        return bigInt.intValue();
    }

    public static long MD5ToLong(String s) {
        byte[] digest = MD5(s);
        BigInteger bigInt = new BigInteger(1, digest);
        return bigInt.longValue();
    }

    public static void parseArgs(BaseArgs s, String[] args){
        try {
            JCommander jcom = new JCommander(s, args);
            if (s.isHelp()){
                jcom.usage();
                System.exit(0);
            }
        } catch (ParameterException e) {
            new JCommander(s).usage();
            System.exit(-1);
        }
    }

    public interface IHash {
        long hash(String key);
    }
}
