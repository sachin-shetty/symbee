package com.vayoodoot.exception;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Jan 15, 2007
 * Time: 9:19:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class VDException extends Exception {

    private Throwable previousException = null;
    protected String message = null;
    private Throwable nextException;


    public VDException() {

        super();

    }


    public VDException(String messageId) {

        super(messageId);
        message = messageId;

    }

    public VDException(String messageId, Throwable prevException) {

        super(messageId);
        if ((prevException instanceof VDException)
                || (message == null))
            message = messageId;
        this.previousException = prevException;


    }

    public Throwable getPreviousException() {

        return previousException;

    }

    /**
     * This method prints the stack trace to System.err. It prints the
     * entire stackTrace of all the previous exceptions that it has
     * encapsulated.
     */
    public void printStackTrace() {

        super.printStackTrace();
        if (previousException != null) {
            System.err.println("Caused by Prev: ");
            previousException.printStackTrace();
        }
        if (nextException != null) {
            System.err.println("Caused by Next: ");
            nextException.printStackTrace();
        }

    }

    /**
     * This method prints the stack trace to <code>ps</code>. It prints the
     * entire stackTrace of all the previous exceptions that it has
     * encapsulated.
     *
     * @param ps PrintStream into which the stack trace will be written.
     */
    public void printStackTrace(java.io.PrintStream ps) {

        if (ps == null) {
            System.err.println("PrintStream passed to printStackTrace method is null.");
            printStackTrace();
        }
        super.printStackTrace(ps);
        if (previousException != null) {
            ps.println("Caused by:");
            previousException.printStackTrace(ps);
        }

    }

    /**
     * This method returns the message id.
     */
    public String getMessageId() {

        if (message == null)
            return null;
        int index = message.indexOf(":");
        int len = message.length();

        if (index <= 0)
            return null;
        return message.substring(0, index);

    }

    public String getRootMessageId() {
        VDException exp = null;
        Throwable prevExp = this;
        while ((prevExp != null) && (prevExp instanceof VDException)) {
            exp = (VDException) prevExp;
            prevExp = exp.previousException;
        }
        if (exp == null)
            return null;
        String message = exp.message;
        if (message == null)
            return null;
        int index = message.indexOf(":");
        int len = message.length();

        if (index <= 0)
            return null;
        return message.substring(0, index);

    }


    /**
     * This method prints the stack trace to <code>pw</code>. It prints the
     * entire stackTrace of all the previous exceptions that it has
     * encapsulated.
     *
     * @param pw PrintWriter into which the stack trace will be written.
     */
    public void printStackTrace(java.io.PrintWriter pw) {

        if (pw == null) {
            System.err.println("PrintWriter passed to printStackTrace method is null.");
            printStackTrace();
        }
        super.printStackTrace(pw);
        if (previousException != null) {
            pw.println("Caused by:");
            previousException.printStackTrace(pw);
        }

    }


}
