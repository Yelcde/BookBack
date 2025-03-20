package br.edu.ifpb.pdm.booback.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
object RetrofitInstance {

private const val BASE_URL = "https://recommendations-json-server.vercel.app/"

    val retrofitInstance: Retrofit by lazy {
    Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
}

    val apiService: ApiService by lazy {
    retrofitInstance.create(ApiService::class.java)
}
}
