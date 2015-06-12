package io.knows.saturn.model;

/**
 * Created by ryun on 15-4-30.
 */
public class Resource {
    public ResourceType type;
    public String identity;

    final static String ASSETS_BASE = "http://7xi3ld.com2.z0.glb.qiniucdn.com/";
    public Resource(String identity) {
        this.identity = identity;
    }

    public String getUrl(ResourceSize size) {
        return ASSETS_BASE + identity + "_" + size.getText();
    }

    public enum ResourceSize {
        ORIGINAL("original", 1280),
        STANDARD("standard", 640),
        MEDIUM("medium", 320),
        THUMBNAIL("thumbnail", 80);
        String text;
        Integer size;
        ResourceSize(String type, Integer size) {
            this.text = type;
            this.size = size;
        }
        public String getText() {
            return text;
        }
        public Integer getSize() {
            return size;
        }
    }

    public enum ResourceType {
        IMAGE(1),
        VOICE(2),
        VIDEO(3);
        Integer code;

        ResourceType(Integer code) {
            this.code = code;
        }

        public Integer getCode() {
            return code;
        }
    }
}
