package io.knows.saturn.model;

/**
 * Created by ryun on 15-4-20.
 */
public class User extends Model {
    public Integer age;
    public Integer birthday;
    public String nickname;
    public String zodiac;
    public String bio;
    public String school;
    public String[] hometown;
    public Counts counts;
    public String cover;
    public Gender gender;

    public class Counts {
        public int follows;
        public int media;
    }

    public enum Gender {
        MALE("男", 0), FEMALE("女", 1);

        String text;
        int code;

        Gender(String text, Integer code) {
            this.text = text;
            this.code = code;
        }

        public int getCode() {
            return code;
        }

        public String getText() {
            return text;
        }
    }
}
