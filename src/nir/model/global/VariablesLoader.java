package nir.model.global;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class VariablesLoader {

    private static final String filename ="global.txt";
    public static void load(){
        try {
            Gson gson = new Gson();
            JsonReader reader = new JsonReader(new FileReader(filename));
            GlobalVariables data = gson.fromJson(reader, GlobalVariables.class);
            GlobalVariables.load(data);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void save()  {
        Gson gson = new Gson();
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter("global.txt");
            fileWriter.write(gson.toJson(GlobalVariables.getInstance(),GlobalVariables.class));
            fileWriter.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
