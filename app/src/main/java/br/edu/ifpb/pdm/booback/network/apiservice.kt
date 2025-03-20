package br.edu.ifpb.pdm.booback.network;



import br.edu.ifpb.pdm.booback.models.Recommendation
import retrofit2.Call;
import retrofit2.http.GET;

interface ApiService {
    @GET("/recommendations") // Endereço da sua API
    fun getRecommendation(): Call<List<Recommendation>> // Retorna uma lista de dicas
}
