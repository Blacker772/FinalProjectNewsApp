package com.example.newsaz.ui.details

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.TransitionInflater
import coil.load
import com.example.newsaz.Constants
import com.example.newsaz.R
import com.example.newsaz.databinding.FragmentDetailsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class DetailsFragment : Fragment() {

    private lateinit var binding: FragmentDetailsBinding
    private val viewModel: DetailViewModel by viewModels()
    private val otherAdapter = OtherNewsAdapter()
    private val args: DetailsFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentDetailsBinding.inflate(inflater, container, false)

        val animation = TransitionInflater.from(requireContext()).inflateTransition(android.R.transition.move)
        sharedElementEnterTransition = animation
        postponeEnterTransition(200, TimeUnit.MILLISECONDS)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRV()

        //Получение данных из аргументов
        val category = args.news.categoryId
        val id = args.news.id
        val link = args.news.link
        val news = args.news.image
        binding.sivImage.transitionName = news

        //Получение списка "другие новости"
        lifecycleScope.launch {
            viewModel.getNews(category)
            viewModel.liveData.observe(viewLifecycleOwner) {
                otherAdapter.submitList(it)
            }
        }

        //Действие при нажатии на кнопку "Назад"
        binding.toolbar.setNavigationIcon(R.drawable.arrow)

        //Кнопка "Назад"
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        //Получение данных новости по id
        lifecycleScope.launch {
            viewModel.getNewsById(id)
            viewModel.state.observe(viewLifecycleOwner) {
                it?.let {
                    binding.tvTitle.text = it.title
                    binding.tvTime.text = it.time
                    binding.sivImage.load(it.image)
                    binding.tvCategory.text = it.category
                    webViewSetting(it.content)
                }
            }
        }

        //Действие при нажатии на кнопку "Поделиться"
        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.share -> {
                    val intentShare = Intent(Intent.ACTION_SEND)
                    intentShare.type = "text/plain"
                    intentShare.putExtra(Intent.EXTRA_TEXT, link)
                    startActivity(Intent.createChooser(intentShare, "News..."))
                    true
                }
                else -> false
            }
        }
    }

    //Настройка webView
    @SuppressLint("SetJavaScriptEnabled")
    private fun webViewSetting(content: String) {
        binding.apply {
            webView.settings.javaScriptEnabled = true
            webView.webViewClient = WebViewClient()
            webView.settings.setSupportZoom(true)
            webView.settings.loadWithOverviewMode = true
            webView.settings.useWideViewPort = true
            webView.settings.builtInZoomControls = false
            binding.webView.loadData(
                "<!DOCTYPE html> <html lang=\"en\"> <head> <style type=\"text/css\"> " +
                        "strong { font-size: 17px; font-weight: normal; font-family: sans-serif; } " +
                        "p { font-size: 17px; font-family: sans-serif; font-style: nunito } iframe,embed { width: 100%;} " +
                        "</style>  <meta charset=\"UTF-8\"> <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"> " +
                        "<title>Axar.az</title> " +
                        "</head> <body> $content </body> </html>",
                "text/html; charset=UTF-8",
                null
            )
        }
    }

    //Инициализация RV
    private fun initRV() {
        binding.rvOtherNews.adapter = otherAdapter
        binding.rvOtherNews.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )

        otherAdapter.onItemClickListener {
            val newsId = bundleOf(
                "id" to it.id,
                "link" to it.link,
                "source" to it.source,
                "categoryId" to it.categoryId,
                "image" to it.image
            )
            findNavController().run {
                popBackStack(R.id.detailsFragment, true)
                navigate(R.id.detailsFragment, newsId)
            }
        }
    }
}