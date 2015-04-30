package io.knows.saturn.model;

/**
 * Created by ryun on 15-4-30.
 */
public class Resource {
    public String key;
    final static String ASSETS_BASE = "http://7xi3ld.com2.z0.glb.qiniucdn.com/";
    public Resource(String key) {
        this.key = key;
    }

    public String getUrl(ResourceSize size) {
        return ASSETS_BASE + key + "_" + size.getText();
    }

    public enum ResourceSize {
        STANDARD("standard"),
        THUMBNAIL("thumbnail");
        String text;
        ResourceSize(String type) {
            this.text = type;
        }
        public String getText() {
            return this.text;
        }
    }
}
