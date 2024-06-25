package com.example.newsaz.ui.search

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newsaz.Constants
import com.example.newsaz.R
import com.example.newsaz.databinding.FragmentSearchBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding
    private val searchAdapter = SearchAdapter()
    private val viewModel: SearchViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRV()

        lifecycleScope.launch {
            viewModel.getNews(null).collect {
                searchAdapter.submitData(it)
            }
        }

        binding.searchView.queryHint = "Search"
        binding.searchView.setOnQueryTextListener(object : OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                lifecycleScope.launch {
                    newText?.let { search ->
                        viewModel.getSearchNews(search).collect {
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

    private fun initRV(){
        binding.rvSearchList.adapter = searchAdapter
        binding.rvSearchList.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        searchAdapter.onItemClickListener {
            val newsId = bundleOf(
                "id" to it.id,
                "link" to it.link,
                "source" to it.source,
                "categoryId" to it.categoryId,
                "image" to it.image
            )
            findNavController().navigate(R.id.action_searchFragment_to_detailsFragment, newsId)
        }
    }
}