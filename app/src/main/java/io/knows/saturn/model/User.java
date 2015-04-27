package io.knows.saturn.model;

/**
 * Created by ryun on 15-4-20.
 */
public class User extends Model {
    public int age;
    public String nickname;
    public String zodiac;
    public String bio;
    public String school;
    public String[] hometown;
    public Counts counts;
    public String cover;

    public class Counts {
        public int follows;
        public int media;
    }
}
