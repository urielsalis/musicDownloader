package me.urielsalis.musicDownload;

import org.apache.commons.lang3.SystemUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.concurrent.TimeUnit;

/**
 * Created by Uriel Salischiker on 11/20/2016.
 */
public class Main {
    private JTextField downloadLink;
    private JButton downloadButton;
    private JCheckBox remove1;
    private JCheckBox remove2;
    private JTextArea console;
    private JProgressBar progressBar;
    private JTextField outputFolder;
    private JButton browseButton;
    private JPanel panel;
    private File selectedFolder = null;
    private ShellExec exec = new ShellExec();

    public Main() {
        final JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setDialogTitle("Select output folder");
        chooser.setAcceptAllFileFilterUsed(false);
        updateYoutubedl();

        downloadButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String[] titles;
                progressBar.setIndeterminate(true);
                if(downloadLink.getText().contains("http")) {
                    titles = runYoutubedlCommand("-e ", downloadLink.getText()).split(exec.NL);
                } else {
                    titles = downloadLink.getText().split(",");
                }
                progressBar.setIndeterminate(false);
                progressBar.setMaximum(titles.length);
                int value = 0;
                for (String str : titles) {
                    download(str.trim());
                    value++;
                    progressBar.setValue(value);
                }
                progressBar.setValue(0);
            }
        });
        browseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (chooser.showOpenDialog(panel) == JFileChooser.APPROVE_OPTION) {
                    console.append("Output folder set: "+chooser.getSelectedFile().toString());
                    outputFolder.setText(chooser.getSelectedFile().toString());
                    selectedFolder = chooser.getSelectedFile();
                }
            }
        });
    }

    private void download(String title) {
        System.out.println(title);
        String google = "http://www.google.com/search?q=";
        String search = title + " site:zippyshare.com";
        String charset = "UTF-8";
        String userAgent = "MusicDownload 1.0 (+github.com/urielsalis/musicDownload)";

        try {
            Elements links = Jsoup.connect(google + URLEncoder.encode(search, charset)).userAgent(userAgent).get().select(".g>.r>a");

            for (Element link : links) {
                String title2 = link.text();
                String url = link.absUrl("href"); // Google returns URLs in format "http://www.google.com/url?q=<url>&sa=U&ei=<someKey>".
                url = URLDecoder.decode(url.substring(url.indexOf('=') + 1, url.indexOf('&')), "UTF-8");

                if (!url.startsWith("http")) {
                    continue; // Ads/news/etc.
                }

                System.out.println("Title: " + title);
                System.out.println("URL: " + url);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateYoutubedl() {
        runYoutubedlCommand("-U");
    }

    private String runYoutubedlCommand(String... args) {
        String command = SystemUtils.IS_OS_WINDOWS ? "youtube-dl.exe" : "youtube-dl";
        Process p = null;
        try {
            p = Runtime.getRuntime().exec("executable.exec");


            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line;
            while ((line = input.readLine()) != null) {
                System.out.println(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Main");
        frame.setContentPane(new Main().panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
