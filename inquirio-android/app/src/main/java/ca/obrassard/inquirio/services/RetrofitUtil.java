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
import java.util.concurrent.TimeUnit;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.mock.BehaviorDelegate;
import retrofit2.mock.MockRetrofit;
import retrofit2.mock.NetworkBehavior;

public class RetrofitUtil {
    public static InquirioService get(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://5a5.di.college-em.info:7015/")
                .addConverterFactory(buildGsonConverter())
//                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        InquirioService service = retrofit.create(InquirioService.class);
        return  service;
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
}
