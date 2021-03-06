package org.freakz.hokan_ng_springboot.bot.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Petri Airio on 29.10.2015.
 */
public class JarWindowsBatExecutor {

    private static final Logger log = LoggerFactory.getLogger(JarWindowsBatExecutor.class);

    private static final String SHELL = "cmd.exe";
    private final String scriptName;
    private final String charset;

    public JarWindowsBatExecutor(String scriptName, String charset) {
        this.scriptName = scriptName;
        this.charset = charset;
    }

    public String[] executeJarWindowsBat(String... args) {
        log.info("Executing {} with args: {}", scriptName, StringStuff.arrayToString(args, ","));
        InputStream inputStream = this.getClass().getResourceAsStream(scriptName);
        if (inputStream == null) {
            log.error("Couldn't get InputStream for {}", this.scriptName);
            return null;
        }
        File tmpFile = null;
        try {
            tmpFile = File.createTempFile(scriptName, ".bat");

            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            BufferedWriter bw = new BufferedWriter(new FileWriter(tmpFile));
            String line;
            while (true) {
                line = br.readLine();
                if (line == null) {
                    break;
                }
                bw.write(line + "\n");
            }
            br.close();
            bw.flush();
            bw.close();

            String tmpScriptName = tmpFile.getAbsolutePath();
            List<String> cmdList = new ArrayList<>();
            cmdList.add(SHELL);
            cmdList.add("/c");
            cmdList.add(tmpScriptName);
            cmdList.addAll(Arrays.asList(args));
            String[] stringArray = cmdList.toArray(new String[cmdList.size()]);
            Process p = Runtime.getRuntime().exec(stringArray);

            br = new BufferedReader(new InputStreamReader(p.getInputStream(), this.charset));

            List<String> output = new ArrayList<>();
            String l;
            do {
                l = br.readLine();
                if (l != null) {
                    output.add(l);
                }

            } while (l != null);
            p.destroy();
            log.info("Process {} ended", p);
            String[] out = output.toArray(new String[output.size()]);
            return out;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (tmpFile != null) {
                log.info("Deleting file: {} -> {}", tmpFile, tmpFile.delete());
            }
        }
        return new String[0];
    }

}
