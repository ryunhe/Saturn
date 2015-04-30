package io.knows.saturn.model.renren;

import android.support.annotation.Nullable;

/**
 * Created by ryun on 15-4-30.
 */
public class RennUser {
    public Long id;
    public String name;
    public Image[] avatar;
    public Integer star;
    public BasicInformation basicInformation;
    public School[] education;
//    public Work[] work; // @TODO
    public Like[] like;
    public EmotionalState emotionalState;

    @Nullable
    public School getSchool() {
        EducationBackground[] types = new EducationBackground[] {
                EducationBackground.COLLEGE
                , EducationBackground.MASTER
                , EducationBackground.DOCTOR
                , EducationBackground.HIGHSCHOOL
                , EducationBackground.TECHNICAL
                , EducationBackground.JUNIOR
                , EducationBackground.PRIMARY
            };

        School school;
        for (EducationBackground type : types) {
            school = getSchool(type);
            if (null != school) {
                return school;
            }
        }
        return null;
    }

    @Nullable
    public School getSchool(EducationBackground type) {
        for (School school : education) {
            if (type == school.educationBackground) {
                return school;
            }
        }
        return null;
    }

    public class Image {
        public ImageSize size;
        public String url;
    }

    public class BasicInformation {
        public Sex sex;
        public String birthday;
        public HomeTown homeTown;

    }

    public class School {
        public String name;
        public String year;
        public String department;
        public EducationBackground educationBackground;
    }

    public class Like {
        public LikeCategory category;
        public String name;
    }

    public enum EmotionalState {
        INLOVE("恋爱中"),
        OTHER("其他"),
        SINGLE("单身"),
        MARRIED("已婚"),
        UNOBVIOUSLOVE("暗恋"),
        DIVORCE("离异"),
        ENGAGE("订婚"),
        OUTLOVE("失恋");
        String text;
        EmotionalState(String type) {
            this.text = type;
        }
        public String getText() {
            return this.text;
        }

    }

    public enum LikeCategory {
        SPORT("运动"),
        MOVIE("电影"),
        CARTOON("动漫"),
        GAME("游戏"),
        MUSIC("音乐"),
        BOOK("书籍"),
        INTEREST("爱好");
        String text;
        LikeCategory(String type) {
            this.text = type;
        }
        public String getText() {
            return this.text;
        }
    }

    public enum EducationBackground {
        DOCTOR, COLLEGE, GVY, PRIMARY, OTHER, TEACHER, MASTER, HIGHSCHOOL, TECHNICAL, JUNIOR, SECRET;
    }

    public enum ImageSize {
        MAIN("200ptx600pt"),
        TINY("50ptx50pt"),
        LARGE("720ptx720pt"),
        HEAD("100ptx300pt");
        String text;
        ImageSize(String type) {
            this.text = type;
        }
        public String getText() {
            return this.text;
        }
    }

    public enum Sex {
        FEMALE("女"),
        MALE("男");
        String text;
        Sex(String type) {
            this.text = type;
        }
        public String getText() {
            return this.text;
        }
    }

    public class HomeTown {
        public String province;
        public String city;
        public String getText() {
            return (province + " " + city).trim();
        }
    }
}
