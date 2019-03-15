package jimmy.huynh.snake;

import com.google.gson.annotations.SerializedName;

public class Game {
    @SerializedName("objectId")
    private String id;
    @SerializedName("name")
    private String name;
    @SerializedName("score")
    private String score;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }
}
