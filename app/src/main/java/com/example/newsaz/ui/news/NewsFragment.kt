package com.example.newsaz.ui.news

import android.content.Context
import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDirections
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newsaz.Constants
import com.example.newsaz.R
import com.example.newsaz.databinding.FragmentNewsBinding
import com.example.newsaz.ui.UiState
import com.example.newsaz.ui.news.pagination.NewsAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NewsFragment : Fragment() {

    private lateinit var binding: FragmentNewsBinding
    private lateinit var newsAdapter: NewsAdapter
    private val viewModel: NewsViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentNewsBinding.inflate(inflater, container, false)
        initRV()
        Log.d("LANGUAGE", "onCreateView: ${Constants.LANGUAGE}")
        //Анимация перехода
        val animation = TransitionInflater.from(requireContext()).inflateTransition(android.R.transition.move)
        sharedElementEnterTransition = animation
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Загрузка новостей при открытии приложения
        lifecycleScope.launch {
            viewModel.uiState.collectLatest {
                onChangeState(it)
            }
        }

        //Откладывает анимацию, пока информация не прогрузится
        postponeEnterTransition()

        //Запускает отложенную анимацию
        binding.rvListNews.doOnPreDraw {
            startPostponedEnterTransition()
        }

        //Создал SharedPreferences для хранения выбранного языка
        val sharedPreferences = requireContext().getSharedPreferences("my_prefs", Context.MODE_PRIVATE)

        //Создаю переменную для хранения выбранного языка
        val selectedLanguage = sharedPreferences.getString("language", Constants.LANGUAGE) ?: "ru"

        //Записываю выбранный язык в константу
        Constants.LANGUAGE = selectedLanguage

        //Доступ к header
        val header = binding.navigationView.getHeaderView(0)

        //Окрытие поиска при нажатии на search
        val searchButton = header.findViewById<Button>(R.id.search_button)
        searchButton.setOnClickListener {
            findNavController().navigate(R.id.action_newsFragment_to_searchFragment)
        }

        //Слушатель нажатий на кнопку Азербайджанского языка
        val azButton = header.findViewById<ImageView>(R.id.btAze)
        azButton.setOnClickListener {
            if (Constants.LANGUAGE == "az") {
                Toast.makeText(requireContext(), "Этот язык уже выбран", Toast.LENGTH_SHORT).show()
            } else {
                Constants.LANGUAGE = "az"
                lifecycleScope.launch {
                    viewModel.getNews(null).collect {
                        newsAdapter.submitData(it)
                    }
                }
                CoroutineScope(Dispatchers.Main).launch {
                    viewModel.getCategory(Constants.LANGUAGE)
                    viewModel.liveData.observe(viewLifecycleOwner) {
                        binding.navigationView.menu.clear()
                        it.forEach { data ->
                            binding.navigationView.menu.add(0, data.params.category, 0, data.title)
                        }
                    }
                }
                sharedPreferences.edit().putString("language", Constants.LANGUAGE).apply()
                binding.drawerLayout.closeDrawers()
            }
        }

        //Слушатель нажатий на кнопку Английского языка
        val enButton = header.findViewById<ImageView>(R.id.btEnglish)
        enButton.setOnClickListener {
            if (Constants.LANGUAGE == "en") {
                Toast.makeText(requireContext(), "Этот язык уже выбран", Toast.LENGTH_SHORT).show()
            } else {
                Constants.LANGUAGE = "en"
                lifecycleScope.launch {
                    viewModel.getNews(null).collect {
                        newsAdapter.submitData(it)
                    }
                }
                CoroutineScope(Dispatchers.Main).launch {
                    viewModel.getCategory(Constants.LANGUAGE)
                    viewModel.liveData.observe(viewLifecycleOwner) {
                        binding.navigationView.menu.clear()
                        it.forEach { data ->
                            binding.navigationView.menu.add(0, data.params.category, 0, data.title)
                        }
                    }
                }
                sharedPreferences.edit().putString("language", Constants.LANGUAGE).apply()
                binding.drawerLayout.closeDrawers()
            }
        }

        //Слушатель нажатий на кнопку Русского языка
        val ruButton = header.findViewById<ImageView>(R.id.btRussian)
        ruButton.setOnClickListener {
            if (Constants.LANGUAGE == "ru") {
                Toast.makeText(requireContext(), "Этот язык уже выбран", Toast.LENGTH_SHORT).show()
            } else {
                Constants.LANGUAGE = "ru"
                lifecycleScope.launch {
                    viewModel.getNews(null).collect {
                        newsAdapter.submitData(it)
                    }
                }
                CoroutineScope(Dispatchers.Main).launch {
                    viewModel.getCategory(Constants.LANGUAGE)
                    viewModel.liveData.observe(viewLifecycleOwner) {
                        binding.navigationView.menu.clear()
                        it.forEach { data ->
                            binding.navigationView.menu.add(0, data.params.category, 0, data.title)
                        }
                    }
                }
                sharedPreferences.edit().putString("language", Constants.LANGUAGE).apply()
                binding.drawerLayout.closeDrawers()
            }
        }

        //Сворачивание меню
        binding.btMenu.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.END)
        }

        //Загрузка новостей при открытии приложения
        lifecycleScope.launch {
            viewModel.uiState.collectLatest {
                onChangeState(it)
            }
        }
        lifecycleScope.launch {
            // Обработка состояния загрузки адаптера
            newsAdapter.loadStateFlow?.collectLatest { loadState ->
                // Отображаем индикатор загрузки, если данные загружаются
                val isLoading = loadState.refresh is LoadState.Loading
                binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            }
        }

        //Загрузка категорий
        lifecycleScope.launch {
            viewModel.getCategory(Constants.LANGUAGE)
            viewModel.liveData.observe(viewLifecycleOwner) {
                Log.d("livedata", "onViewCreated:$it ")
                it.forEach { data ->
                    binding.navigationView.menu.add(0, data.params.category, 0, data.title)
                }
            }
        }

        //Новости при нажатии на категорию
        binding.navigationView.setNavigationItemSelectedListener {
            lifecycleScope.launch {
                viewModel.getNews(it.itemId).collect {
                    newsAdapter.submitData(it)
                }
            }
            binding.drawerLayout.closeDrawers()
            return@setNavigationItemSelectedListener true
        }

        //Обновление новостей при свайпе
        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.swipeRefreshLayout.isRefreshing = true
            lifecycleScope.launch {
                viewModel.getNews(null).collect {
                    newsAdapter.submitData(it)
                }
            }
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    //Метод для инициализации RV
    private fun initRV() {
        val actionListener = NewsAdapter.OnClickListener { newsDataModel, imageView ->
            val direction: NavDirections =
                NewsFragmentDirections.actionNewsFragmentToDetailsFragment(newsDataModel)
            val extras = FragmentNavigatorExtras(
                imageView to newsDataModel.image
            )
            findNavController().navigate(direction, extras)
        }

        binding.rvListNews.apply {
            newsAdapter = NewsAdapter(actionListener)
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false
            )
            adapter = newsAdapter
        }
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
                newsAdapter.submitData(state.data)
                binding.progressBar.isVisible = state.isLoading
            }
        }
    }
}