import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by United121 on 10/28/2016.
 */
public class JSONHelper {

    public static String FILE_DESTINATION = "file_new.json";

    public void addReview(List<Review> value) {
        addReview(value, FILE_DESTINATION);
    }

    public void addReview(List<Review> value, String nameOfFile) {

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(nameOfFile) , "UTF-8")) {
            writer.write("[");
            //writer.write(gson.toJson(value, Review.class));
            for (int i = 0; i < value.size(); i++) {
                gson.toJson(value.get(i), Review.class, writer);
                if (i != value.size() - 1) {
                    writer.write(",");
                }
            }
            writer.write("]");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public List<Review> getReviews(String filePath) {

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        List<Review> reviews = null;

        try (FileReader reader = new FileReader(filePath)) {
            reviews = Stream.of(gson.fromJson(reader, Review[].class)).collect(Collectors.<Review>toList());
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
        return reviews;
    }


}
