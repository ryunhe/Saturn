package io.knows.saturn.module;

import android.app.Application;

import com.renn.rennsdk.RennClient;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.knows.saturn.activity.StartActivity;
import io.knows.saturn.fragment.AuthFragment;
import io.knows.saturn.fragment.SignupFragment;


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
                .setScope("read_user_blog read_user_checkin read_user_feed read_user_guestbook " +
                        "read_user_invitation read_user_like_history read_user_message " +
                        "read_user_notification read_user_photo read_user_status " +
                        "read_user_album read_user_comment read_user_share " +
                        "read_user_request read_user_comment read_user_share " +
                        "publish_blog publish_checkin publish_feed publish_share write_guestbook " +
                        "send_invitation send_request send_message send_notification photo_upload " +
                        "status_update create_album publish_comment operate_like");
        rennClient.setTokenType("bearer");
        return rennClient;
    }
}
