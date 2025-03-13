package com.pi.cityguiago.module.home

import com.pi.cityguiago.model.Attraction
import com.pi.cityguiago.model.Category
import com.pi.cityguiago.model.Image
import com.pi.cityguiago.network.ApiClient

class HomeService(private val apiClient: ApiClient) {
    suspend fun getAttractions(): Result<List<Attraction>> {
        if (true) {
            return Result.success(mockAttractions)
        } else {
            return try {
                val result: List<Attraction> = apiClient.get("https://cityguiago.com/api/atracoes/")
                Result.success(result)
            } catch (e: Exception) {
                println("Registration failed: ${e.message}")
                Result.failure(e)
            }
        }
    }

    private val mockAttractions = listOf(
        Attraction(
            id = "1",
            nome = "Parque Central",
            descricao = "Um lindo parque para relaxar e caminhar.",
            categoria = Category(id = "1", descricao = "Natureza"),
            horarioFuncionamento = "08:00 - 18:00",
            precoMedio = 0.0,
            enderecoLogradouro = "Avenida Central",
            enderecoNumero = "123",
            enderecoComplemento = "Perto da fonte",
            enderecoBairro = "Centro",
            enderecoCidade = "São Paulo",
            enderecoEstado = "SP",
            enderecoCep = "01000-000",
            enderecoCoordenadas = "-23.5505, -46.6333",
            imagens = listOf(
                Image(id = "1", caminho = "https://example.com/image1.jpg"),
                Image(id = "2", caminho = "https://example.com/image2.jpg")
            )
        ),
        Attraction(
            id = "2",
            nome = "Museu de Arte Moderna",
            descricao = "Um dos museus mais famosos da cidade.",
            categoria = Category(id = "2", descricao = "Cultura"),
            horarioFuncionamento = "10:00 - 17:00",
            precoMedio = 30.0,
            enderecoLogradouro = "Rua das Artes",
            enderecoNumero = "45",
            enderecoComplemento = "Ao lado da galeria",
            enderecoBairro = "Bela Vista",
            enderecoCidade = "São Paulo",
            enderecoEstado = "SP",
            enderecoCep = "01310-000",
            enderecoCoordenadas = "-23.5631, -46.6562",
            imagens = listOf(
                Image(id = "3", caminho = "https://example.com/image3.jpg"),
                Image(id = "4", caminho = "https://example.com/image4.jpg")
            )
        )
    )
}