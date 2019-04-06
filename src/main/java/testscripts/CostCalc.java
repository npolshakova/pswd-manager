package testscripts;

import btree.BinaryTree;
import datastruct.LinkedList;
import datastruct.LinkedNode;
import datastruct.StorageNode;
import storage.encryption.IDGenerator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class CostCalc {

    public static void main(String args[]) {

        BinaryTree bt = new BinaryTree();
        StorageNode sn = new StorageNode();
        LinkedList lst = new LinkedList();

        String csvFile = "~/pswd-manager/src/main/resources/test1.csv";
        String line = "";
        String cvsSplitBy = ",";

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {

            while ((line = br.readLine()) != null) {
                String[] row = line.split(cvsSplitBy);
                int id = IDGenerator.generateID(row[0]);
                bt.insert(id, row[1]);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
