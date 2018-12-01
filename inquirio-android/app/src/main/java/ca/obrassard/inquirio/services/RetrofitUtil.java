package ca.obrassard.inquirio.services;

import android.util.Base64;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.mock.BehaviorDelegate;
import retrofit2.mock.MockRetrofit;
import retrofit2.mock.NetworkBehavior;

public class RetrofitUtil {
    public static Retrofit retrofit;
    public static InquirioService get(){
        if (retrofit == null){
            retrofit = new Retrofit.Builder()
//                .baseUrl("https://5a5.di.college-em.info:7015/")
                    .baseUrl("https://10.0.2.2:7015/")
                    .addConverterFactory(buildGsonConverter())
                    .client(getClient())
//                .addConverterFactory(ScalarsConverterFactory.create())
                    .build();
        }
        return  retrofit.create(InquirioService.class);
    }

    public static InquirioService getMock(){
        
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://localhost:8080")
                .addConverterFactory(buildGsonConverter())
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        NetworkBehavior networkBehavior = NetworkBehavior.create();
        networkBehavior.setDelay(1000, TimeUnit.MILLISECONDS);
        networkBehavior.setVariancePercent(90);

        MockRetrofit mock = new MockRetrofit.Builder(retrofit).networkBehavior(networkBehavior).build();

        BehaviorDelegate<InquirioService> delegate =
                mock.create(InquirioService.class);

        return new InquirioServiceMock(delegate);
    }

    private static GsonConverterFactory buildGsonConverter() {
        GsonBuilder builder = new GsonBuilder();
        builder.enableComplexMapKeySerialization(); // permet de serialiser des Map avec des cles definies par vous
        builder.registerTypeAdapter(byte[].class, new CodecByteArray());
        Gson myGson = builder.create();
        return GsonConverterFactory.create(myGson);
    }

    public static class CodecByteArray implements JsonSerializer<byte[]>, JsonDeserializer<byte[]> {
        public byte[] deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return Base64.decode(json.getAsString(), Base64.NO_WRAP);
        }

        public JsonElement serialize(byte[] src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(Base64.encodeToString(src, Base64.NO_WRAP));
        }
    }

    private static OkHttpClient getClient(){
        try {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();

            // Sets the cookie Jar to automatically handles incoming and outgoing cookies
            CookieJar cookieJar = new MyCookieJar();
            builder = builder.cookieJar(cookieJar);

            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {}

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {}

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };
            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            // configure the builder to accept all SSL certificates
            builder = builder.sslSocketFactory(sslSocketFactory);
            // configure the builder to accept all hostnames includint localhost
            builder = builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            return builder.build();
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static class MyCookieJar implements CookieJar {

        private List<Cookie> cookies;

        @Override
        public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
            this.cookies =  cookies;
        }

        @Override
        public List<Cookie> loadForRequest(HttpUrl url) {
            List<Cookie> res = new ArrayList<>();
            if (cookies != null){
                for(Cookie c : cookies){
                    if (c.expiresAt() > System.currentTimeMillis()) res.add(c);
                }
            }
            return res;
        }
    }


}
