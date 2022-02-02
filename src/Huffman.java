//Alaa Ebrahim Mahmoud
//ID: 20190105
//Rana Ihab Ahmed
//ID: 20190207
//CS-s2


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

class Node {
    int frequency;
    char symbol;
    String code = "";
    Node left;
    Node right;
}

class Compare implements Comparator<Node> {
    @Override
    public int compare(Node o1, Node o2) {
        return o1.frequency - o2.frequency;
    }
}

public class Huffman {
    public static HashMap<Character, Integer> getFrequencies(String fl) throws Exception {
        BufferedReader buffer = new BufferedReader(new FileReader(fl));
        Scanner sc = new Scanner(buffer);
        String txt = buffer.readLine();

        HashMap<Character, Integer> charFrequency = new HashMap<Character, Integer>();

        for (int i = 0; i < txt.length(); i++) {
            int count = 1;
            if (charFrequency.containsKey(txt.charAt(i))) {
                continue;
            }

            for (int j = i + 1; j < txt.length(); j++) {
                if (txt.charAt(j) == txt.charAt(i)) {
                    count++;
                }
            }
            charFrequency.put(txt.charAt(i), count);
        }
        for (Character key : charFrequency.keySet()) {
            System.out.println(key + " = " + charFrequency.get(key));
        }
        return charFrequency;
    }

    public static void writeCode(Node root) throws IOException {
        if (root.left == null && root.right == null) {
            return;
        }
        root.left.code += root.code + "0";
        root.right.code += root.code + "1";
        writeCode(root.left);
        writeCode(root.right);
    }

    public static void makeDictionary(String dic, String in) throws Exception {
        HashMap<Character, Integer> freq = null;

        //create dictionary in path dic
        String currentPath = Paths.get("").toAbsolutePath().toString();
        File file = new File(dic);
        if (!file.isAbsolute()) {
            dic = currentPath + File.separator + dic;
        }
        Path p = Paths.get(dic);
        try {
            if (!file.exists()) {
                Path x = Files.createFile(p);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            freq = getFrequencies(in);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        PriorityQueue<Node> heapMin = new PriorityQueue<Node>(freq.size(), new Compare());

        for (Character key : freq.keySet()) {
            Node node = new Node();
            node.symbol = key;
            node.frequency = freq.get(key);
            node.left = null;
            node.right = null;
            heapMin.add(node);
        }

        Node root = null;

        ArrayList<Node> nodes = new ArrayList<>();
        while (heapMin.size() > 1) {
            Node x = heapMin.poll();
            Node y = heapMin.poll();
            Node node = new Node();
            node.frequency = x.frequency + y.frequency;
            node.symbol = '.';
            node.left = x;
            node.right = y;
            if (node.left.left == null && node.left.right == null) {
                nodes.add(node.left);
            }
            if (node.right.left == null && node.right.right == null) {
                nodes.add(node.right);
            }
            root = node;
            heapMin.add(node);
        }


        BufferedWriter writer = new BufferedWriter(new FileWriter(dic));
        String dict = "";
        writeCode(root);
        for (int i = nodes.size() - 1; i >= 0; i--) {
            if (nodes.get(i).symbol == ' ') {
                dict += nodes.get(i).code + " " + "(space character encoding)";
            } else {
                dict += nodes.get(i).symbol + " " + nodes.get(i).code;
            }

            if (i != 0) {
                dict += ", ";
            }
        }

        writer.write(dict);
        writer.close();

    }

    public static HashMap<Character, String> getCoDict(String dict) throws FileNotFoundException {
        BufferedReader buffer = new BufferedReader(new FileReader(dict));

        String line = null;
        String text = "";
        try {
            line = buffer.readLine();

            while (line != null) {
                text += line;
                line = buffer.readLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        String[] arr = text.split(", ", 0);

        HashMap<Character, String> dictionary = new HashMap<>();
        for (int i = 0; i < arr.length; i++) {
            String[] charCode = arr[i].split(" ", 0);
            if (charCode.length == 1 && charCode[0].equals("")) {
                continue;
            }
            if (charCode.length == 1) {
                dictionary.put(',', charCode[0]);
            } else if (charCode.length > 2) {
                dictionary.put(' ', charCode[0]);
            } else {
                dictionary.put(charCode[0].charAt(0), charCode[1]);
            }
        }

        return dictionary;
    }

    public static void compress(String in, String dict, String out) throws Exception {
        makeDictionary(dict, in);
        HashMap<Character, String> dictionary = getCoDict(dict);

        //create output file in path out
        String currentPath = Paths.get("").toAbsolutePath().toString();
        File file = new File(out);
        if (!file.isAbsolute()) {
            out = currentPath + File.separator + out;
        }
        Path p = Paths.get(out);
        try {
            if (!file.exists()) {
                Path x = Files.createFile(p);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        BufferedReader br = new BufferedReader(new FileReader(in));
        String message = br.readLine();
        BufferedWriter writer = new BufferedWriter(new FileWriter(out));
        String compressed = "";
        for (int i = 0; i < message.length(); i++) {
            compressed += dictionary.get(message.charAt(i));
        }
        writer.write(compressed);
        writer.close();
        double co = (double) (message.length() * 8) / compressed.length();
        System.out.println("Compressed Ratio = " + message.length() * 8 + "/" + compressed.length() + " = " + co);
    }

    public static HashMap<String, Character> getDeDict(String dict) throws FileNotFoundException {
        BufferedReader buffer = new BufferedReader(new FileReader(dict));

        String line = null;
        String text = "";
        try {
            line = buffer.readLine();

            while (line != null) {
                text += line;
                line = buffer.readLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        String[] arr = text.split(", ", 0);

        HashMap<String, Character> dictionary = new HashMap<>();
        for (int i = 0; i < arr.length; i++) {
            String[] charCode = arr[i].split(" ", 0);
            if (charCode.length == 1 && charCode[0].equals("")) {
                continue;
            }
            if (charCode.length == 1) {
                dictionary.put(charCode[0], ',');
            } else if (charCode.length > 2) {
                dictionary.put(charCode[0], ' ');
            } else {
                dictionary.put(charCode[1], charCode[0].charAt(0));
            }
        }

        return dictionary;
    }

    public static void decompress(String in, String dict, String out) throws Exception {
        BufferedReader buffer = new BufferedReader(new FileReader(in));

        String line = null;
        String text = "";
        try {
            line = buffer.readLine();

            while (line != null) {
                text += line;
                line = buffer.readLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        String sentence = "";

        HashMap<String, Character> dictionary = getDeDict(dict);

        for (int i = 0; i < text.length(); i++) {
            String code = "" + text.charAt(0);
            if (dictionary.containsKey(code)) {
                sentence += dictionary.get(code);
                continue;
            }

            for (int j = 1; j < text.length(); j++) {
                code += text.charAt(j);
                if (dictionary.containsKey(code)) {
                    sentence += dictionary.get(code);
                    i = j - 1;
                    text = text.substring(j + 1);
                    break;
                }
            }
        }
        //create output file in path out
        String currentPath = Paths.get("").toAbsolutePath().toString();
        File file = new File(out);
        if (!file.isAbsolute()) {
            out = currentPath + File.separator + out;
        }
        Path p = Paths.get(out);
        try {
            if (!file.exists()) {
                Path x = Files.createFile(p);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        BufferedWriter writer = new BufferedWriter(new FileWriter(out));
        writer.write(sentence);
        writer.close();
    }


    public static void GUI() {

        JFrame frame = new JFrame("Standard Huffman Code");
        frame.setSize(700, 700);
        frame.setLocation(500, 300);
        frame.setPreferredSize(new Dimension(700, 400));
        frame.setBackground(Color.white);

        JPanel panel = new JPanel();
        frame.getContentPane();

        final JTextField text1 = new JTextField();
        text1.setBounds(385, 50, 250, 30);
        panel.add(text1);
        Label l1 = new Label("Original Text Path: ");
        l1.setBounds(110, 50, 160, 30);
        l1.setFont(new Font("Verdana", Font.BOLD, 16));
        panel.add(l1);

        final JTextField text2 = new JTextField();
        text2.setBounds(385, 100, 250, 30);
        panel.add(text2);
        Label l2 = new Label("Dictionary Path: ");
        l2.setBounds(110, 100, 150, 30);
        l2.setFont(new Font("Verdana", Font.BOLD, 16));
        panel.add(l2);

        final JTextField text3 = new JTextField();
        text3.setBounds(385, 150, 250, 30);
        panel.add(text3);
        Label l3 = new Label("Compressed Text Path: ");
        l3.setBounds(110, 150, 180, 30);
        l3.setFont(new Font("Verdana", Font.BOLD, 16));
        panel.add(l3);

        JButton button1 = new JButton("Compress");
        Dimension size1 = button1.getPreferredSize();
        button1.setBounds(210, 250, size1.width, size1.height);
        panel.add(button1);
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String inputPath = text1.getText();
                String DictionaryPath = text2.getText();
                String OutputPath = text3.getText();
                if (inputPath.length() == 0) {
                    JOptionPane.showMessageDialog(null, "The input path is empty!!");
                } else if (DictionaryPath.length() == 0) {
                    JOptionPane.showMessageDialog(null, "The dictionary path is empty!!");
                } else if (OutputPath.length() == 0) {
                    JOptionPane.showMessageDialog(null, "The output path is empty!!");
                }
                try {
                    compress(inputPath, DictionaryPath, OutputPath);
                    JOptionPane.showMessageDialog(null, "Done");

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "The input path is not Found!");
                }
            }
        });

        JButton button2 = new JButton("Decompress");
        Dimension size2 = button2.getPreferredSize();
        button2.setBounds(360, 250, size2.width, size2.height);
        panel.add(button2);
        button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String OutputPath = text1.getText();
                String DictionaryPath = text2.getText();
                String inputPath = text3.getText();
                if (inputPath.length() == 0) {
                    JOptionPane.showMessageDialog(null, "The input path is empty!!");
                } else if (DictionaryPath.length() == 0) {
                    JOptionPane.showMessageDialog(null, "The dictionary path is empty!!");
                } else if (OutputPath.length() == 0) {
                    JOptionPane.showMessageDialog(null, "The output path is empty!!");
                }
                try {
                    decompress(inputPath, DictionaryPath, OutputPath);
                    JOptionPane.showMessageDialog(null, "Done");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "The input path is not Found!");
                }
            }
        });

        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setLayout(null);
        frame.add(panel);
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

    public static void main(String[] args) throws Exception {
        GUI();
    }
}