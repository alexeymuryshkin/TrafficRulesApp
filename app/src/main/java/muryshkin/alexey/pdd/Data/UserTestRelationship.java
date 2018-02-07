package muryshkin.alexey.pdd.Data;

import com.backendless.BackendlessUser;

/**
 * Created by 123 on 8/5/2016.
 */
public class UserTestRelationship {

    private int maxCorrect;
    private int score;
    private Test test;
    private BackendlessUser user;
    private String objectId;

    public UserTestRelationship() {}

    public UserTestRelationship(BackendlessUser user, Test test, int maxCorrect, int score) {
        this.user = user;
        this.test = test;
        this.maxCorrect = maxCorrect;
        this.score = score;
    }

    public int getMaxCorrect() {
        return maxCorrect;
    }

    public void setMaxCorrect(int maxCorrect) {
        this.maxCorrect = maxCorrect;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public Test getTest() {
        return test;
    }

    public BackendlessUser getUser() {
        return user;
    }

    public String getObjectId() {
        return objectId;
    }
}
