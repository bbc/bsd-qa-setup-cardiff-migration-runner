package org.runmigrationrunner;

import com.jcraft.jsch.JSchException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class App
{
    public static void main( String[] args ) throws JSchException, IOException, SQLException, ClassNotFoundException {

        String itemId;
        String itemInstanceId;
        String storeInstanceId;
        List<String> storeInstanceIdMod;
        Integer[] mediaCodes;
        DBAccess dbConnection;
        String DBURL;
        String SSHHOSTURL;
        List<String> sshCommands;
        String sshCommandsCombined;
        ServerAccess serverConnection;
        Integer itemHighWatermark;
        List<String> sshResults;

        //------- URL inits

        DBURL = "jdbc:oracle:thin:@(DESCRIPTION = (ADDRESS = (PROTOCOL = TCP)(HOST = zgbwcodb3001.jupiter.bbc.co.uk)(PORT = 1521)) (CONNECT_DATA = (SERVER = DEDICATED) (SERVICE_NAME=JUPDEV2) (INSTANCE_NAME=JUPDEV2)))";
        SSHHOSTURL = "zgbwcjvjoe7ws01.jupiter.bbc.co.uk";

        //------- arguments check

        if (args.length == 0) {
            System.out.println("you have no arguments: please provide one or more media item ids...");
            System.exit(1);
        }else{
            for (String arg: args) {
                if (!arg.matches("[0-9]+")){
                    System.out.println("your item ids should not contain any letters, try again...");
                    System.exit(1);
                }
            }
        }


        //------- DB section

        storeInstanceIdMod = new ArrayList();
        dbConnection = new DBAccess(DBURL);
        dbConnection.connectDB();
        mediaCodes = new Integer[]{20, 114};

        int j = 0;
        for(String arg : args) {
            itemId = arg;
            System.out.println("\n\nYour chosen media itemId " + itemId);
            int i = 0;
            for (int mediaCode : mediaCodes) {
                System.out.println(i + ") for mediaqualitycode = " + mediaCode);
                itemInstanceId = dbConnection.runQuery("select iteminstanceid from mediaiteminstance where itemid = " + itemId + " and mediaqualitycode = " + mediaCode);
                System.out.println("itemInstanceId = " + itemInstanceId);
                storeInstanceId = dbConnection.runQuery("select storeinstanceid from mediaiteminstanceda where iteminstanceid = " + itemInstanceId);
                System.out.println("storeInstanceId: " + storeInstanceId);
                storeInstanceIdMod.add(storeInstanceId.replaceAll("[ /_]", " ").replaceAll("[.xml]",""));
                System.out.println("storeInstanceIdMod[" + j + "]: " + storeInstanceIdMod.get(j));
                dbConnection.runUpdate("update mediaiteminstanceda set storeinstanceid = '" + storeInstanceIdMod.get(j) + "' where iteminstanceid = " + itemInstanceId);
                i++;
            }

            itemHighWatermark = Integer.valueOf(itemId) - 1;
            System.out.println("migration highwatermark will be set to: " + itemHighWatermark);
            dbConnection.runUpdate("update keyvaluepair set value = " + itemHighWatermark + " where key = 'archivemigrationhighwatermark'");
            j++;
        }
        dbConnection.disconnectDB();

        //------ SSH section

        sshCommands = new ArrayList();
        serverConnection = new ServerAccess(SSHHOSTURL);
        serverConnection.serverConnect();
        sshCommandsCombined = "";

        sshCommands.add("cd /var/joe");
        sshCommands.add("ls");
        for(String storeInstanceFile : storeInstanceIdMod){
            System.out.println("adding file ... " + storeInstanceFile);
            sshCommands.add("touch " + "'" + storeInstanceFile + "'");
        }
        sshCommands.add("cd /opt/bbc/jupiter/cardiff-migration-job-creator");

        for (String sshCommand : sshCommands) {
            sshCommandsCombined = sshCommandsCombined + sshCommand + ";";
        }

        System.out.println("executing ssh commands: " + sshCommands);
        sshResults = serverConnection.runCommands(sshCommandsCombined);

        System.out.println("Results:-  \n" + sshResults);
        serverConnection.serverDisconnect();

    }
}

