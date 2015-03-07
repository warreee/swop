package be.swop.groep11.main;



import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by warreee on 2/03/15.
 */
public class InputReader  {

    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws FileNotFoundException {
        Yaml yaml = new Yaml();

        System.out.println(yaml.dump(yaml.load(new FileInputStream(new File(
                "input.tman")))));

        Map<String, Map<String, String>> values = (Map<String, Map<String, String>>) yaml
                .load(new FileInputStream(new File("input.tman")));

        for (String key : values.keySet()) {
            String o = key;
            ArrayList subList = (ArrayList) values.get(o);
            Map<String, String> test = (Map<String, String>) subList.get(0);


            System.out.println(key);

            for (String subValueKey : test.keySet()) {
                System.out.println(String.format("\t%s = %s",
                        subValueKey, test.get(subValueKey)));
            }
        }
    }

}
