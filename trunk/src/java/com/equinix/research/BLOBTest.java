package com.equinix.research;

import oracle.jdbc.driver.OracleDriver;
import oracle.sql.BLOB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.CallableStatement;
import java.io.OutputStream;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Jan 29, 2008
 * Time: 3:53:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class BLOBTest {


/*
    create or replace procedure
    READ_BLOB (blob_value BLOB)
    is
    begin
        DBMS_OUTPUT.put_line('I am here');
        insert into temp_blob values(blob_value);
    end;
    /

    create table TEMP_BLOB (contents BLOB);
    select * from TEMP_BLOB;

*/

    public static void main (String args[]) throws Exception {

        Class.forName("oracle.jdbc.driver.OracleDriver");
        Connection con = DriverManager.getConnection("jdbc:oracle:thin:@devdb02:1529:SISILVER",
                "apps", "ecuthbert");

        BLOB tmpBlob = BLOB.createTemporary(con, false,
                BLOB.DURATION_SESSION);
        OutputStream os = tmpBlob.getBinaryOutputStream();
        os.write("Hello".getBytes());
        os.flush();
        os.close();

        CallableStatement cstmt = con.prepareCall(
                "{call READ_BLOB(?)}");
        cstmt.setBlob(1, tmpBlob);
        cstmt.execute();
     


    }

}
