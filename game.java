import com.github.freva.asciitable.AsciiTable;

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Locale;
import java.util.Scanner;

public class game {

    public static void main(String[] args) throws NoSuchAlgorithmException, IOException {
        String se = null;
        for(int i = 0; i < args.length; i++) {
            se += args[i] + " ";
        }
        if(args.length < 1) {
            System.out.println("Objects are missing!");
        } else {
            if (args.length < 3) {
                System.out.println("The number of elements must be >= 3!");
            } else {
                if (args.length >= 3 & args.length % 2 != 0) {
                    try {
                        byte[] hmacSha256 = Igruxa.calcHmacSha256("secret123".getBytes("UTF-8"), se.getBytes("UTF-8"));
                        System.out.println(String.format("HMAC: %032x", new BigInteger(1, hmacSha256)).toUpperCase(Locale.ROOT));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    Menu(args);
                } else  { System.out.println("An odd number of elements!"); }
            }
        }
    }

    public static void Menu(String[] arguments) throws NoSuchAlgorithmException, IOException {
        System.out.println("\nAvailable moves:");
        for(int i = 0; i < arguments.length; i++) {
            System.out.println((i+1) +" - " +arguments[i]);
        }
        System.out.print("0 - exit\n? - help\nEnter your move: ");
        Scanner in = new Scanner(System.in);
        char move = in.nextLine().charAt(0);
        Reaction(move, arguments);
    }

    public static void Reaction(char move, String[] arguments) throws NoSuchAlgorithmException, IOException {
        if(Character.isDigit(move))  {
            int move_ = move - '0';
            if(move_ >= 1 & move_ < arguments.length + 1) {
                Game((move_ - 1), arguments);
            }

            if(move_ == 0) { System.out.println("End of the game!"); System.exit(0); }
        }
        if(move == '?') { AsciiPrint(arguments); }
        Menu(arguments);
    }

    public static void Game(int playerMove, String[] arguments) throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        SecureRandom random = new SecureRandom();
        keyGen.init(random);
        SecretKey secretKey = keyGen.generateKey();
        String s = new BigInteger(1, secretKey.getEncoded()).toString(16);

        int EnemyMove = random.nextInt(arguments.length);;
        String prediction = null;
        int situatuinCase = -1, LenD = arguments.length / 2;
        if(playerMove == EnemyMove) { situatuinCase = 0; }
        else { if(playerMove > EnemyMove) { situatuinCase = 1; }  else  { situatuinCase = 2; } }
        switch (situatuinCase) {
            case 0: { prediction = "Draw!"; break; }
            case 1: {
                if(playerMove + LenD >= arguments.length){
                    int a;
                    if(playerMove + LenD == arguments.length) { a = 0; }
                    else { a = (playerMove + LenD) - arguments.length; }
                    if(a >= EnemyMove) { prediction = "You lose!"; }
                    else { prediction = "You win!"; }
                }  else {
                    if(playerMove < EnemyMove) { prediction = "You lose!"; }
                    else { prediction = "You win!"; }
                }
                break;
            }
            case 2: {
                if(playerMove - LenD <= 0){
                    int a;
                    if(playerMove - LenD > 0) { a = arguments.length - 1; }
                    else { a = (playerMove - LenD) + arguments.length; }
                    if(a <= EnemyMove) { prediction = "You win!"; }
                    else { prediction = "You lose!"; }
                }  else {
                    if(playerMove > EnemyMove) { prediction = "You win!"; }
                    else { prediction = "You lose!"; }
                }
                break;
            }
        }
        System.out.println("Your move: " +arguments[playerMove] +"\nComputer move: " +arguments[EnemyMove] +"\n" +prediction +"\nKEY: "+s.toUpperCase(Locale.ROOT));
    }

    public static void AsciiPrint(String[] arguments){
        String[][] _data_ = new String[arguments.length + 1][arguments.length + 1]; _data_[0][0] = "PC/USER";
        for(int i = 1; i < _data_.length; i++) {
            _data_[i][0] = arguments[i - 1];
            _data_[0][i] = arguments[i - 1];
        }

        for(int i = 1; i < _data_.length; i++) {
            for(int j = 1; j < _data_.length; j++)
            {
                int MyMove = j;
                int EnemyMove = i;
                String prediction = null;
                int situatuinCase = -1, LenD = arguments.length / 2;
                if(MyMove == EnemyMove) { situatuinCase = 0; }
                else { if(MyMove > EnemyMove) { situatuinCase = 1; }  else  { situatuinCase = 2; } }
                switch (situatuinCase) {
                    case 0: { prediction = "Draw"; break; }
                    case 1: {
                        if(MyMove + LenD >= arguments.length){
                            int a;
                            if(MyMove + LenD == arguments.length) { a = 0; }
                            else { a = (MyMove + LenD) - arguments.length; }
                            if(a >= EnemyMove) { prediction = "Lose"; }
                            else { prediction = "Win"; }
                        }  else {
                            if(MyMove < EnemyMove) { prediction = "Lose"; }
                            else { prediction = "Win"; }
                        }
                        break;
                    }
                    case 2: {
                        if(MyMove - LenD <= 0){
                            int a;
                            if(MyMove - LenD > 0) { a = arguments.length - 1; }
                            else { a = (MyMove - LenD) + arguments.length; }
                            if(a <= EnemyMove) { prediction = "Win"; }
                            else { prediction = "Lose"; }
                        }  else {
                            if(MyMove > EnemyMove) { prediction = "Win"; }
                            else { prediction = "Lose"; }
                        }
                        break;
                    }
                } _data_[i][j] = prediction; }
        }
        System.out.println(AsciiTable.getTable(_data_));
    }

    static public byte[] calcHmacSha256(byte[] secretKey, byte[] message) {
        byte[] hmacSha256 = null;
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey, "HmacSHA256");
            mac.init(secretKeySpec);
            hmacSha256 = mac.doFinal(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to calculate hmac-sha256", e);
        }
        return hmacSha256;
    }
}
