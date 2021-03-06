package org.edx.mobile.user;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.webkit.MimeTypeMap;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.squareup.okhttp.OkHttpClient;

import org.edx.mobile.discussion.RetroHttpExceptionHandler;
import org.edx.mobile.event.ProfilePhotoUpdatedEvent;
import org.edx.mobile.http.RetroHttpException;
import org.edx.mobile.logger.Logger;
import org.edx.mobile.util.Config;
import org.edx.mobile.util.DateUtil;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

import de.greenrobot.event.EventBus;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;
import retrofit.http.Path;
import retrofit.mime.TypedFile;

@Singleton
public class UserAPI {

    private UserService userService;

    private Logger logger = new Logger(UserAPI.class.getName());

    @Inject
    public UserAPI(@NonNull Config config, @NonNull OkHttpClient client) {
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .setDateFormat(DateUtil.ISO_8601_DATE_TIME_FORMAT)
                .create();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setClient(new OkClient(client))
                .setEndpoint(config.getApiHostURL())
                .setConverter(new GsonConverter(gson))
                .setErrorHandler(new RetroHttpExceptionHandler())
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();
        userService = restAdapter.create(UserService.class);
    }

    public Account getAccount(@NonNull String username) throws RetroHttpException {
        return userService.getAccount(username);
    }

    public Account updateAccount(@NonNull String username, @NonNull String field, @NonNull Object value) throws RetroHttpException {
        return userService.updateAccount(username, Collections.singletonMap(field, value));
    }

    public void setProfileImage(@Path("username") String username, @NonNull final File file) throws RetroHttpException, IOException {
        final String mimeType = "image/jpeg";
        logger.debug("Uploading file of type " + mimeType + " from " + file.toString());
        userService.setProfileImage(
                username,
                "attachment;filename=filename." + MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType),
                new TypedFile(mimeType, file));
        EventBus.getDefault().post(new ProfilePhotoUpdatedEvent(username, Uri.fromFile(file)));
    }
}
