package com.vayoodoot.research;

import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.net.MalformedURLException;


/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Nov 19, 2006
 * Time: 12:00:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class GoogleLogin {

    

    public static void main(String args[]) {

        try {
            URL url;
            URLConnection urlConn;
            DataOutputStream printout;
            DataInputStream input;

            // URL of CGI-Bin script.
            url = new URL("https://www.google.com/accounts/ClientLogin");

            // URL connection channel.
            urlConn = url.openConnection();

            // Let the run-time system (RTS) know that we want input.
            urlConn.setDoInput(true);

            // Let the RTS know that we want to do output.
            urlConn.setDoOutput(true);

            // No caching, we want the real thing.
            urlConn.setUseCaches(false);

            // Specify the content type.
            urlConn.setRequestProperty
                    ("Content-Type", "application/x-www-form-urlencoded");

            // Send POST output.
            printout = new DataOutputStream(urlConn.getOutputStream());

//            String content =
//                    new StringBuffer().append("name=").append(URLEncoder.encode("Buford Early"))
//                            .append("&email=").append(URLEncoder.encode("buford@known-space.com")).toString();

            String params[][] = {
                    {"accountType", "HOSTED_OR_GOOGLE"},
                    {"Email", "sachintheonly@gmail.com"},
                    {"Passwd", "mumbhai"},
                    {"service", "xapi"},
                    {"source", "Gulp-CalGulp-1.05"}
            };

            StringBuffer parameterString = new StringBuffer();
            for (int i = 0; i < params.length; i++) {
                parameterString.append(params[i][0] + "=");
                parameterString.append(URLEncoder.encode(params[i][1]) + "&");
            }


            printout.writeBytes(parameterString.toString());
            printout.flush();
            printout.close();

            // Get response data.
            input = new DataInputStream(urlConn.getInputStream());

            String str;
            while (null != ((str = input.readLine()))) {
                System.out.println(str);
            }
            input.close();

        }
        catch (MalformedURLException me) {
            System.err.println("MalformedURLException: " + me);
        }
        catch (IOException ioe) {
            System.err.println("IOException: " + ioe.getMessage());
        }

    }

}
