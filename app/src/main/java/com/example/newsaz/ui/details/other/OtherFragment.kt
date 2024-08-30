package com.example.newsaz.ui.details.other

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.transition.TransitionInflater
import coil.load
import com.example.newsaz.databinding.FragmentOtherBinding
import com.example.newsaz.ui.details.DetailViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class OtherFragment : Fragment() {

    private lateinit var binding: FragmentOtherBinding
    private val viewModel: DetailViewModel by viewModels()
    private val args: OtherFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentOtherBinding.inflate(inflater, container, false)
        animation(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Получение данных из аргументов
        val id = args.news?.id
        val link = args.news?.link
        val news = args.news?.image
        binding.sivImage.transitionName = news


        //Кнопка "Назад"
        binding.btBack.setOnClickListener {
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

        //кнопка "Поделиться"
        binding.btShare.setOnClickListener {
            val intentShare = Intent(Intent.ACTION_SEND)
            intentShare.type = "text/plain"
            intentShare.putExtra(Intent.EXTRA_TEXT, link)
            startActivity(Intent.createChooser(intentShare, "News..."))
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
    private fun animation(context: Context) {
        val anim = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        sharedElementEnterTransition = anim
        postponeEnterTransition(200, TimeUnit.MILLISECONDS)
    }
}