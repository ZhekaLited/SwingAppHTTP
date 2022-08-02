package Frame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static javax.swing.JOptionPane.showMessageDialog;

public class Frame  {
    static String methodServer = "GET";
    static String urlString = "";
    static HttpURLConnection con = null;
    static String body = "sn=C02G8416DRJM&cn=&locale=&caller=&num=12345";

    public static void main(String[] args) throws MalformedURLException {
        JFrame frame = new JFrame("POSTMAN");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        JTextField textField = new JTextField("http://localhost:8080/");
        textField.setBounds(190, 150, 500, 30);
        frame.add(textField);
        frame.setSize(900, 600);
        frame.setLayout(null);
        frame.add(textField);

        JTextArea area = new JTextArea("{\n" +
                "    \"carName\":  ,\n" +
                "    \"carColor\": ,\n" +
                "    \"carModel\": \n" +
                "}");
        area.setBounds(40,200, 360,350);
        frame.add(area);
        JScrollPane scrollPaneArea = new JScrollPane();
        scrollPaneArea.setBounds(40,200, 360,350);
        frame.getContentPane().add(scrollPaneArea);
        scrollPaneArea.setViewportView(area);

        JTextArea areaResult = new JTextArea();
        areaResult.setBounds(540,200, 400,350);
        frame.add(areaResult);
        JScrollPane scrollPaneResult = new JScrollPane();
        scrollPaneResult.setBounds(450,200, 400,350);
        frame.getContentPane().add(scrollPaneResult);
        scrollPaneResult.setViewportView(areaResult);

        JButton buttonSend = new JButton("SEND");
        buttonSend.setBounds(700,140,100,40);
        frame.add(buttonSend);

        Dimension objDimension = Toolkit.getDefaultToolkit().getScreenSize();
        int iCoordX = (objDimension.width - frame.getWidth()) / 2;
        int iCoordY = (objDimension.height - frame.getHeight()) / 2;
        frame.setLocation(iCoordX, iCoordY);

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        String[] elements = new String[] {"GET", "POST",
                "PUT","DELETE"};                                //Скролл кнопка
        JComboBox combo = new JComboBox(elements);
        combo.setBounds(80,140,100,40);
        ActionListener actionListenerCombo = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               JComboBox cb = (JComboBox)e.getSource();
               methodServer = (String)cb.getSelectedItem();
            }
        };
        combo.addActionListener(actionListenerCombo);
        frame.add(combo);

        ActionListener actionListenerSend = new ActionListener() {
            public void actionPerformed(ActionEvent e) {              //Кнопка Send
              urlString = textField.getText();
              body = area.getText();
                try {
                    if (urlString.equals("")) {
                        showMessageDialog(null, "Url String is Empty");

                    }

                    URL url = new URL(urlString);
                    con = (HttpURLConnection) url.openConnection();
                    con.setRequestProperty("Content-Type", "application/json" );
                    areaResult.setText("");

                    switch (methodServer) {
                        case "GET":
                            con.setRequestMethod(methodServer);

                            int status = con.getResponseCode();
                            StringBuffer content;
                            if (status == 200) {
                                BufferedReader in = new BufferedReader(
                                        new InputStreamReader(con.getInputStream()));

                                String inputLine;
                                content = new StringBuffer();
                                while (true) {
                                    if (!((inputLine = in.readLine()) != null)) break;
                                    content.append(inputLine);
                                }
                                String str = content.toString().replace("[", "").replace("]", "");
                                String[] itemsSplit = str.split("},");

                                StringBuilder builder = new StringBuilder();
                                builder.append("\r\n"+ "]"+"\n\n\n");
                                for (String it:  itemsSplit)
                                {
                                  builder.append("         " + it + " }, \r\n");

                                }

                                String blockStr = builder.toString();
                                builder.delete(0, builder.length() -1);

                                itemsSplit = blockStr.split(",");
                                for (String it : itemsSplit)
                                {
                                    builder.append("         " +   it + "\r\n");
                                }
                                builder.append("[");

                                areaResult.setText(builder.toString());
                            }
                            break;
                        case "POST":
                            con.setRequestMethod(methodServer);
                            con.setDoOutput(true);
                            try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
                                wr.writeBytes(body);
                                wr.flush();
                            }

                            BufferedReader in = new BufferedReader(
                                    new InputStreamReader(con.getInputStream()));
                            String line;
                            StringBuilder response = new StringBuilder();

                            while ((line = in.readLine()) != null) {
                                response.append(line);
                            }

                            String str = response.toString().replace("[", "").replace("]", "");
                            String[] itemsSplitPost = str.split("},");

                            StringBuilder builder = new StringBuilder();
                            builder.append("\r\n"+ "]"+"\n\n\n");
                            for (String it:  itemsSplitPost)
                            {
                                builder.append("         " + it + " }, \r\n");

                            }

                            String blockStr = builder.toString();
                            builder.delete(0, builder.length() -1);

                            itemsSplitPost = blockStr.split(",");
                            for (String it : itemsSplitPost)
                            {
                                builder.append("         " +   it + "\r\n");
                            }
                            builder.append("[");

                            areaResult.setText(builder.toString());
                            break;
                        case "PUT":
                            con.setRequestMethod(methodServer);
                            con.setDoOutput(true);
                            OutputStreamWriter out = new OutputStreamWriter(
                                    con.getOutputStream());
                            out.write(body);
                            out.close();
                            con.getInputStream();
                            break;

                        case "DELETE":
                            con.setRequestMethod(methodServer);
                            int responseCode = con.getResponseCode();
                            BufferedReader inTwo = new BufferedReader(
                                    new InputStreamReader(con.getInputStream()));
                        String inputLinee;
                        content = new StringBuffer();
                        while (true) {
                            if (!((inputLinee = inTwo.readLine()) != null)) break;
                            content.append(inputLinee);
                            break;
                        }
                        areaResult.setText(content.toString());
                    }
                } catch (MalformedURLException ex) {
                    throw new RuntimeException(ex);
                } catch (IOException ex) {
                    throw new RuntimeException();
                } finally {
                  con.disconnect();
                }
            }
        };
        buttonSend.addActionListener(actionListenerSend);

        frame.setVisible(true);
    }
}


