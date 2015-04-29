package io.knows.saturn.module;

import android.app.Application;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;
import com.renn.rennsdk.RennClient;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import java.io.File;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.knows.saturn.activity.MainActivity;
import io.knows.saturn.activity.SignupActivity;
import io.knows.saturn.activity.StartActivity;
import io.knows.saturn.fragment.AuthFragment;
import io.knows.saturn.fragment.CardStackFragment;
import io.knows.saturn.fragment.CongratsFragment;
import io.knows.saturn.fragment.MediaListFragment;
import io.knows.saturn.fragment.ProfileFragment;
import io.knows.saturn.fragment.SignupFragment;
import io.knows.saturn.helper.CupboardDbHelper;
import io.knows.saturn.helper.GsonFieldConverterFactory;
import io.knows.saturn.helper.StringsFieldConverterFactory;
import io.knows.saturn.model.Media;
import io.knows.saturn.model.User;
import nl.nl2312.rxcupboard.RxCupboard;
import nl.nl2312.rxcupboard.RxDatabase;
import nl.qbusict.cupboard.Cupboard;
import nl.qbusict.cupboard.CupboardBuilder;
import nl.qbusict.cupboard.CupboardFactory;
import nl.qbusict.cupboard.DatabaseCompartment;
import timber.log.Timber;

import static android.content.Context.MODE_PRIVATE;
import static com.jakewharton.byteunits.DecimalByteUnit.MEGABYTES;
import static java.util.concurrent.TimeUnit.SECONDS;


/**
 * Created by ryun on 15-4-21.
 */
@Module(
        library = true,
        complete = false,
        includes = DataModule.class,
        injects = {
                StartActivity.class,
                AuthFragment.class,
                SignupFragment.class,
        }
)
public class RennModule {
    private static final String APP_ID = "475451";
    private static final String API_KEY = "cd9a40588be54de3a512ab2773181aa4";
    private static final String SECRET_KEY = "20c4b482a92a4d18b72344066234b18f";

    @Provides @Singleton
    RennClient provideRennClient(Application application) {
        RennClient rennClient = RennClient.getInstance(application);
        rennClient.init(APP_ID, API_KEY, SECRET_KEY);
        rennClient
                .setScope("read_user_blog read_user_photo read_user_status read_user_album "
                        + "read_user_comment read_user_share publish_blog publish_share "
                        + "send_notification photo_upload status_update create_album "
                        + "publish_comment publish_feed");
        rennClient.setTokenType("bearer");
        return rennClient;
    }
}
