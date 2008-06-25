<%@ page import="java.sql.DriverManager"%>
<%@ page import="java.sql.Connection"%>
<%@ page import="java.sql.Statement"%>
<%@ page import="java.io.InputStream"%>
<%@ page import="java.sql.ResultSet"%>
<%@ page import="java.io.FileOutputStream"%>
<%@ page import="java.io.IOException"%>
<%@ page import="java.util.ArrayList"%>
<%!

    public static class ThreadRunner implements Runnable {

        HttpServletRequest request;
        HttpServletResponse response;
        String id;

        private boolean isRunning;
        private boolean isError;

        public void loadFileFromDB() throws Exception {

            response.getWriter().write(id + ":" + "Starting........<br>");
            Class.forName("oracle.jdbc.driver.OracleDriver");
            Connection conn =
                    DriverManager.getConnection("jdbc:oracle:thin:@DEVDB02.scl:1529:SISILVER", "apps", "ecuthbert");

            Statement st = conn.createStatement();

            String fct = "";
            String inv_fname = "";
            byte[] buff	  = null;
            String file_id = request.getParameter("file_id");
            if (file_id == null || file_id.trim().length() == 0) {
                file_id= "1511338";
            }
            FileOutputStream fos = new FileOutputStream("/tmp/fnd_lob_" + System.currentTimeMillis());

            response.getWriter().write(id + ":" + "Running Select........<br>");
            String SqlQuery="SELECT file_id, file_name, file_content_type, file_data, file_format FROM fnd_lobs WHERE 1=1 AND FILE_ID =" + file_id;

            ResultSet rs=st.executeQuery(SqlQuery);
            response.getWriter().write(id + ":" + "Select Returned, getting data...<br>");
            while(rs.next()) {
                InputStream is=rs.getBinaryStream("file_data");
                fct = rs.getString("file_content_type");
                inv_fname = rs.getString("file_name");
                int len=0;
                buff = new byte[32*1024];
                while ((len=is.read( buff ))!=-1 ) {
                    fos.write(buff,0,len);
                    fos.flush();
                }
            }
            conn.close();
            fos.close();
            response.getWriter().write("<b>" + id + ":" + "Finished writing file</b><br>" );

        }

        public void run() {

            try {
                isRunning = true;
                loadFileFromDB();
                isRunning = false;
            } catch (Exception e) {
                isRunning = false;
                isError = true;
                try {
                    response.getWriter().print("<font color=red>Error: " + e + "</font><br>");
                    e.printStackTrace(response.getWriter());
                } catch (IOException ie) {
                    ie.printStackTrace();
                }
            }

            isRunning = false;

        }


        public boolean isRunning() {
            return isRunning;
        }

        public void setRunning(boolean running) {
            isRunning = running;
        }

        public boolean isError() {
            return isError;
        }

        public void setError(boolean error) {
            isError = error;
        }

    }

%>

<%

    response.setContentType("text/html");

    String totalReqs = request.getParameter("total");
    int total = 0;
    if (totalReqs == null) {
        total = 25;
    } else {
        try {
            total = Integer.parseInt(totalReqs);
        } catch (NumberFormatException nme) {
            total = 25;;
        }
    }

    ArrayList list = new ArrayList();
    for (int i=0; i<total; i++) {
        ThreadRunner runner = new ThreadRunner();
        runner.id = "Thread " + i;
        runner.request = request;
        runner.response = response;
        Thread th = new Thread(runner);
        list.add(runner);
        th.start();
    }

    for (int i=0; i<list.size(); i++) {
        ThreadRunner th = (ThreadRunner)list.get(i);
        if (th.isRunning()) {
            response.getWriter().println(th.id + " is still running, sleeping for 5 seconds<br>");
            Thread.sleep(5000);
        }
    }
    response.getWriter().println("All Threads completed.... Exitiing");


%>