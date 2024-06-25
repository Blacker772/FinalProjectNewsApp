package com.example.newsaz.ui.search

import android.content.Context
import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDirections
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newsaz.databinding.FragmentSearchBinding
import com.example.newsaz.ui.news.UiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding
    private lateinit var searchAdapter: SearchAdapter
    private val viewModel: SearchViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        initRV()
        animation(requireContext())
        Log.d("lifecycle", "onCreateView: create ")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("lifecycle", "onViewCreated: create ")
        //Откладывает анимацию, пока информация не прогрузится
        postponeEnterTransition()
        //Запускает отложенную анимацию
        binding.rvSearchList.doOnPreDraw {
            startPostponedEnterTransition()
        }

        lifecycleScope.launch {
            viewModel.uiState.collectLatest {
                onChangeState(it)
            }
        }
        // Обработка состояния загрузки адаптера
        lifecycleScope.launch {
            searchAdapter.loadStateFlow.collectLatest { loadState ->
                // Отображаем индикатор загрузки, если данные загружаются
                val isLoading = loadState.refresh is LoadState.Loading
                binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            }
        }

        binding.searchView.setOnQueryTextListener(object : OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                lifecycleScope.launch {
                    newText?.let { search ->
                        viewModel.getSearchNews(search).collectLatest {
                            searchAdapter.submitData(it)
                        }
                    }
                }
                return true
            }
        })

        binding.btBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    //Метод для анимации
    private fun animation(context: Context) {
        val anim = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        sharedElementEnterTransition = anim
    }

    //Метод обработки состояния
    private suspend fun onChangeState(state: UiState) {
        when (state) {
            is UiState.Loading -> {
                binding.progressBar.isVisible = state.isLoading
            }

            is UiState.Error -> {
                Toast.makeText(requireContext(), "${state.message}", Toast.LENGTH_SHORT).show()
            }

            is UiState.Data -> {
                searchAdapter.submitData(state.data)
                binding.progressBar.isVisible = state.isLoading
            }

            UiState.None -> ""
        }
    }

    //Метод для инициализации RV
    private fun initRV() {
        val actionListener = SearchAdapter.OnClickListener { newsListModel, imageView ->
            val action: NavDirections =
                SearchFragmentDirections.actionSearchFragmentToDetailsFragment(newsListModel)
            val extras = FragmentNavigatorExtras(
                imageView to newsListModel.image
            )
            findNavController().navigate(action, extras)
        }
        binding.rvSearchList.apply {
            searchAdapter = SearchAdapter(actionListener)
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false
            )
            adapter = searchAdapter
        }
    }
}