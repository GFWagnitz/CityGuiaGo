package br.ufes.inf.cityguiago.android

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import br.ufes.inf.cityguiago.network.ApiClient
import io.ktor.client.engine.android.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    private lateinit var listView: ListView
    private lateinit var apiClient: ApiClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        listView = findViewById(R.id.attractionListView)
        apiClient = ApiClient(Android.create())

        loadAttractions()
    }

    private fun loadAttractions() {
        launch {
            val attractions = withContext(Dispatchers.IO) { apiClient.getAtracoes() }
            if (attractions.isNotEmpty()) {
                val adapter = ArrayAdapter(
                    this@MainActivity,
                    android.R.layout.simple_list_item_1,
                    attractions.map { it.nome }
                )
                listView.adapter = adapter
            } else {
                // Show an error message or empty state
            }
        }
    }
}