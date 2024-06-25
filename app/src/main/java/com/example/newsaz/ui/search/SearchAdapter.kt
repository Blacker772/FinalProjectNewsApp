package com.example.newsaz.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.newsaz.data.model.newsmodel.NewsListModel
import com.example.newsaz.databinding.NewsItemBinding
import com.example.newsaz.ui.news.pagination.NewsAdapter.OnClickListener
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class SearchAdapter(private val onClickListener: OnClickListener) :
    PagingDataAdapter<NewsListModel, SearchAdapter.SearchViewHolder>(DIFF_UTIL) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = NewsItemBinding.inflate(inflater, parent, false)
        return SearchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
        holder.setIsRecyclable(true)
        val currentData = getItem(position)
        if (currentData != null) {
            holder.binding.apply {
                sivImage.load(currentData.image)
                sivImage.transitionName = currentData.image
                root.setOnClickListener {
                    onClickListener.onClick(currentData, sivImage)
                }
            }
        }
    }

    inner class SearchViewHolder(val binding: NewsItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: NewsListModel) {
            binding.tvTitle.text = data.title
            binding.sivImage.load(data.image) {
                crossfade(true)
                crossfade(200)
            }
            binding.tvPublishedTime.text = convertDate(data.date)
        }
    }

    class OnClickListener(val clickListener: (NewsListModel, ImageView) -> Unit) {
        fun onClick(data: NewsListModel, imageView: ImageView) = clickListener(data, imageView)
    }

    companion object {
        val DIFF_UTIL = object : DiffUtil.ItemCallback<NewsListModel>() {
            override fun areItemsTheSame(
                oldItem: NewsListModel,
                newItem: NewsListModel
            ): Boolean {
                return oldItem.title == newItem.title &&
                        oldItem.image == newItem.image
            }

            override fun areContentsTheSame(
                oldItem: NewsListModel,
                newItem: NewsListModel
            ): Boolean {
                return oldItem.title == newItem.title &&
                        oldItem.link == newItem.link

            }
        }
    }

    private fun convertDate(data: Int): String {
        try {
            val dateAPI =
                DateTimeFormatter.ISO_INSTANT.format(java.time.Instant.ofEpochSecond(data.toLong()))
            val zoneDateTime = ZonedDateTime.parse(dateAPI)
            val localZonedDateTime = zoneDateTime.withZoneSameInstant(ZoneId.of("Asia/Baku"))
            val date = localZonedDateTime.format(
                DateTimeFormatter.ofPattern("dd MMMM, HH:mm").withLocale(Locale("ru_RU"))
            )
            return date

        } catch (e: Exception) {
            val dateAPI =
                DateTimeFormatter.ISO_INSTANT.format(java.time.Instant.ofEpochSecond(data.toLong()))
            val zoneDateTime = ZonedDateTime.parse(dateAPI)
            val localZonedDateTime = zoneDateTime.withZoneSameInstant(ZoneId.of("Asia/Baku"))
            val date = localZonedDateTime.format(DateTimeFormatter.ofPattern("dd MMMM, HH:mm"))
            return date
        }
    }
}