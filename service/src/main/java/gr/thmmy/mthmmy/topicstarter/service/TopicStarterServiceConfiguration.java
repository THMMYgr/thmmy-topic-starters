package gr.thmmy.mthmmy.topicstarter.service;

import io.vavr.control.Try;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

@Configuration
@ComponentScan
public class TopicStarterServiceConfiguration {

    @Bean
    public OkHttpClient getClient() {

        return Try
                .of(() -> new CookieJar() {
                            private final java.util.List<Cookie> cookieStore = new ArrayList<>();

                            @Override
                            public void saveFromResponse(@NonNull HttpUrl url, @NonNull java.util.List<Cookie> cookies) {
                                cookieStore.addAll(cookies);
                            }

                            @Override
                            public java.util.List<Cookie> loadForRequest(@NonNull HttpUrl url) {
                                return cookieStore;
                            }
                        }
                ).map(cookieJar -> new OkHttpClient.Builder()
                        .cookieJar(cookieJar)
                        .connectTimeout(30, TimeUnit.SECONDS)
                        .writeTimeout(30, TimeUnit.SECONDS)
                        .readTimeout(30, TimeUnit.SECONDS)
                        .retryOnConnectionFailure(true)
                        .build())
                .get();
    }
}
