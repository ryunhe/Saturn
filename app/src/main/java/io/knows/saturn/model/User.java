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
    public Resource cover;
    public Gender gender;
    public Double[] location; // [lng,lat]
    public Like[] likes;

    public class Counts {
        public int follows;
        public int media;
    }

    public class Like {
        public LikeCategory category;
        public String name;
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

    public enum LikeCategory {
        BOOK("书籍", 0),
        MUSIC("音乐", 1),
        MOVIE("电影", 2),
        SPORT("运动", 3),
        GAME("游戏", 4),
        PLACE("足迹", 5),
        FOOD("食物", 6),
        ;

        String text;
        int code;

        LikeCategory(String text, Integer code) {
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
