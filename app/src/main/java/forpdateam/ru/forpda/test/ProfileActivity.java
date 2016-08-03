package forpdateam.ru.forpda.test;

import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.widget.TextView;
import android.widget.Toast;

import com.trello.rxlifecycle.ActivityEvent;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import forpdateam.ru.forpda.R;
import forpdateam.ru.forpda.api.Api;
import forpdateam.ru.forpda.api.newslist.models.NewsItem;
import forpdateam.ru.forpda.api.profile.models.ProfileModel;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by radiationx on 03.08.16.
 */
public class ProfileActivity extends RxAppCompatActivity {
    private static final String LINk = "http://4pda.ru/forum/index.php?showuser=2556269#";
    private Subscription subscription;

    private Date date;
    private TextView text;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newslist);
        text = (TextView) findViewById(R.id.textView2);
        date = new Date();
        loadData();
    }

    private void loadData() {
        subscription = Api.Profile().getRx(LINk)
                .timeout(2, TimeUnit.SECONDS)
                .retry(2)
                .onErrorResumeNext(throwable -> {
                    Log.d("kek", "error return next");
                    return null;
                })
                .onErrorReturn(throwable -> {
                    Log.d("kek", throwable.getMessage());
                    throwable.printStackTrace();
                    return new ProfileModel();
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.bindUntilEvent(ActivityEvent.PAUSE))
                .subscribe(this::bindUi);
    }

    private void bindUi(ProfileModel profile) {
        String temp = "";
        String postfix = " \n\n";
        if (profile != null) {
            temp += profile.getAvatar() + postfix;
            temp += profile.getNick() + postfix;
            temp += profile.getStatus() + postfix;
            temp += profile.getGroup() + postfix;
            temp += profile.getRegDate() + postfix;
            temp += profile.getAlerts() + postfix;
            temp += profile.getOnlineDate() + postfix;
            temp += profile.getSign() + postfix;
            temp += profile.getGender() + postfix;
            temp += profile.getBirthDay() + postfix;
            temp += profile.getUserTime() + postfix;
            ArrayList<Pair<String, String>> list = profile.getContacts();
            for (Pair<String, String> pair : list) {
                temp += (pair != null ? pair.first + " : " + pair.second : "null") + postfix;
            }
            list = profile.getDevices();
            for (Pair<String, String> pair : list) {
                temp += (pair != null ? pair.first + " : " + pair.second : "null") + postfix;
            }
            temp += (profile.getKarma() != null ? profile.getKarma().first + " : " +profile.getKarma().second : "null") + postfix;
            temp += (profile.getSitePosts() != null ? profile.getSitePosts().first + " : " +profile.getSitePosts().second : "null") + postfix;
            temp += (profile.getComments() != null ? profile.getComments().first + " : " +profile.getComments().second : "null") + postfix;
            temp += (profile.getReputation() != null ? profile.getReputation().first + " : " +profile.getReputation().second : "null") + postfix;
            temp += (profile.getTopics() != null ? profile.getTopics().first + " : " +profile.getTopics().second : "null") + postfix;
            temp += (profile.getPosts() != null ? profile.getPosts().first + " : " +profile.getPosts().second : "null") + postfix;
        }

        Log.d("kek", "time: " + (new Date().getTime() - date.getTime()));
        text.setText(temp);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        subscription.unsubscribe();
    }
}