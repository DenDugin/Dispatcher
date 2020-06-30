package com.dispatcher.dispatcher.component;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


@Component
public class Docker {

    private Logger logger =  LogManager.getLogger();

    public synchronized void startDockerCompose() {

        try {
            Process p = Runtime.getRuntime().exec("docker-compose up --build");
            p.waitFor();

        } catch (IOException | InterruptedException e) {
            logger.error(e.getMessage(),e); }
    }

    public synchronized void startContainer(String image) throws IOException {
        try {

            Process p = Runtime.getRuntime().exec("docker ps");
            p.waitFor();

            String s = null;
            StringBuilder listDocker = new StringBuilder();
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));

            while ((s = stdInput.readLine()) != null) {
                listDocker.append(s);
            }

            if (listDocker.toString().contains(image)) return;

            p = Runtime.getRuntime().exec("docker run -d " + image);
            p.waitFor();

        } catch (InterruptedException e) {
            logger.error(e.getMessage(),e);
        }
    }


}
