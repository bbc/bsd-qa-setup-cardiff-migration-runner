package org.runmigrationrunner;

import com.google.common.io.CharStreams;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Scanner;

import static java.util.Arrays.asList;

public class ServerAccess {

    private String SSH_HOST;
    private ChannelExec channel;
    private Session session;
    private String result;
    private InputStream output;
    private Scanner scanner;
    private String USERNAME;
    private String PASSWORD;

    public ServerAccess(String SSH_HOST) {
        this.SSH_HOST = SSH_HOST;
        this.channel = null;
        this.session = null;
        this.result = null;
        this.output = null;
        this.USERNAME = null;
        this.PASSWORD = null;
        this.scanner = new Scanner(System.in);
    }

    public void promptUserCredentials() {
        System.out.println("Please enter your USERNAME for ... " + SSH_HOST);
        USERNAME = scanner.nextLine();
        System.out.println("you entered : " + USERNAME);
        System.out.println("Please enter your PASSWORD for ... "+ SSH_HOST);
        PASSWORD = scanner.nextLine();
    }

    public void serverConnect() throws JSchException {
        session = setupSshSession();
        try {
            session.connect();
        } catch (JSchException e) {
            throw new JSchException("You have entered wrong credentials, try again ...");
        }
        System.out.println("Session " + session + " connected to " + session.getHost() + " ... ");
        channel = (ChannelExec) session.openChannel("exec");
    }

    public List<String> runCommands(String command) throws JSchException {
        try {
            channel.setCommand(command);
            channel.setInputStream(null);
            output = channel.getInputStream();
            channel.connect();
            System.out.println("Channel " + channel + " connected ... ");

            result = CharStreams.toString(new InputStreamReader(output));
            System.out.println("result written ...");
            return asList(result.split("\n"));

        } catch (IOException e) {
            closeConnection(channel, session);
            throw new RuntimeException(e);

        }
    }

    public Session setupSshSession() throws JSchException {
        promptUserCredentials();
        session = new JSch().getSession(USERNAME, SSH_HOST, 22);
        session.setPassword(PASSWORD);
        session.setConfig("PreferredAuthentications", "publickey,keyboard-interactive,password");
        session.setConfig("StrictHostKeyChecking", "no"); // disable check for RSA key
        return session;
    }

    private static void closeConnection(ChannelExec channel, Session session) {
        try {
            channel.disconnect();
            System.out.println("Channel " + channel + " disconnnected ... ");
        } catch (Exception ignored) {
        }
        session.disconnect();
        System.out.println("Session " + session + " disconnnected ... ");
    }

    public void serverDisconnect(){
        closeConnection(channel, session);
    }
}




