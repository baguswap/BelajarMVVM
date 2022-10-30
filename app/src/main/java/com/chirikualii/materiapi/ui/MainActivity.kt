package com.chirikualii.materiapi.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.chirikualii.materiapi.ViewModelFactory
import com.chirikualii.materiapi.data.model.Movie
import com.chirikualii.materiapi.data.remote.ApiClient
import com.chirikualii.materiapi.databinding.ActivityMainBinding
import com.chirikualii.materiapi.ui.adapter.MovieListAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var binding :ActivityMainBinding
    private lateinit var adapter: MovieListAdapter
    private val viewModel:MainViewModel by viewModels (
        factoryProducer = {ViewModelFactory()}
            )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //setup adapter
        adapter = MovieListAdapter()
        binding.rvMovie.adapter = adapter

        binding.progressBar.visibility = View.INVISIBLE
        binding.progressBar.visibility = View.VISIBLE
        viewModel.doGetPopularMovie()

        viewModel.listMovie.observe(this){
            adapter.addItem(it)
            binding.progressBar.visibility = View.INVISIBLE
        }

    }

    private fun loadDataFromApi() {
        val service = ApiClient.service

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response =service.getPopularMovie()

                if(response.isSuccessful){
                    withContext(Dispatchers.Main){
                        val listMovie =response.body()?.results?.map {
                            Movie(
                                title= it.title,
                                genre = it.releaseDate,
                                imagePoster = it.posterPath
                            )
                        }
                        withContext(Dispatchers.Main){
                            if(listMovie!=null){
                                adapter.addItem(listMovie)
                            }
                        }


                    }

                }else{
                    withContext(Dispatchers.Main){
                        Toast.makeText(this@MainActivity, "gagal", Toast.LENGTH_SHORT).show()
                    }
                }
            }catch (e:Exception){

            }

        }
    }
}