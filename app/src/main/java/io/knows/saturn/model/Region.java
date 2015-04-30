package io.knows.saturn.model;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import io.knows.saturn.module.DataModule;
import nl.nl2312.rxcupboard.RxDatabase;
import nl.qbusict.cupboard.Cupboard;
import nl.qbusict.cupboard.DatabaseCompartment;
import nl.qbusict.cupboard.annotation.Ignore;
import nl.qbusict.cupboard.annotation.Index;
import timber.log.Timber;

/**
 * Created by ryun on 15-4-29.
 */
public class Region {
    @SerializedName("s")
    public String name;
    @SerializedName("m")
    public String fullName;
    @SerializedName("p")
    public String location;
    @SerializedName("l")
    public List<Region> children;

    public static List<Region> build(Context context) throws IOException {
        InputStream inputStream = context.getAssets().open("region.json");
        Reader reader = new InputStreamReader(inputStream, "UTF-8");
        return new Gson().fromJson(reader, new TypeToken<ArrayList<Region>>(){}.getType());
    }
}
