package com.equinix.research;

import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPMessage;

import javax.mail.*;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import java.util.Enumeration;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Jan 28, 2008
 * Time: 4:49:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class IMAPTest {

    public static void main (String args[]) throws Exception {

        Session session =
                Session.getDefaultInstance(new Properties());

        Store store = session.getStore("imap"); store.connect("HQ-USE2K.us.win.equinix.com",
            "wfsisilver", "equinix12");
//        Folder publicFolder = store.getFolder("Public Folders/");
//        Folder[] folders = publicFolder.list("*Oracle*");
//        for (int i=0; i<folders.length; i++) {
//            System.out.println("The folder is: " + folders[i]);
//        }

        IMAPFolder folder = (IMAPFolder)store.getFolder("INBOX");
        int count = folder.getMessageCount();
        folder.open(Folder.READ_WRITE);
        System.out.println("Total Messages: " + count);

        Message[] messages = folder.getMessages();
        //Message[] messages = folder.getMessagesByUID(new long[] {long200710242117});
        for (int i=0; i<messages.length; i++) {
            System.out.println("=============================================");
            System.out.println("Message Status: " + messages[i].isSet(Flags.Flag.SEEN));

            System.out.println("Message Subject: " + messages[i].getSubject());
            System.out.println("Message ID: " + ((IMAPMessage)messages[i]).getMessageID());

            Enumeration enum1 = messages[i].getAllHeaders();
            while (enum1.hasMoreElements()) {
                Header obj = (Header)enum1.nextElement();
                //System.out.println("Header: " + obj.getName() + ":" + obj.getValue());
            }

            System.out.println("Mime Type is: " + messages[i].getContentType());
            if (messages[i].getContent() instanceof Multipart) {
                Multipart mp = (Multipart)messages[i].getContent();
                Part body = mp.getBodyPart(0);
                System.out.println("Body is: " + body.getContent());

                for (int j=1, n=mp.getCount(); j<n; j++) {
                    Part part = mp.getBodyPart(j);
                    String disposition = part.getDisposition();

                    if ((disposition != null) &&
                            ((disposition.equals(Part.ATTACHMENT) ||
                                    (disposition.equals(Part.INLINE))))) {
                        System.out.println("File is: " + part.getFileName());
                    }

                }
            } else {
                String bodyContents = messages[i].getContent().toString();
                System.out.println("Text Content is: = = = =>\n" + bodyContents);
            }
            //folder.copyMessages(new Message[] {messages[i]}, store.getFolder("DONE"));
            messages[i].setFlag(Flags.Flag.SEEN, true);
            //messages[i].setFlag(Flags.Flag.DELETED, true);

        }
        folder.expunge();
        folder.close(true);
        store.close();

    }


}