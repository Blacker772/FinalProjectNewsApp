package com.example.newsaz.ui.details

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.newsaz.data.model.newsmodel.NewsListModel
import com.example.newsaz.databinding.NewsItemBinding
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class OtherNewsAdapter: ListAdapter<NewsListModel, OtherNewsAdapter.OtherNewsViewHolder>(DIFF_UTIL) {

    private var onItemClick:((news: NewsListModel) -> Unit)? = null
    fun onItemClickListener(onItemClick: (news: NewsListModel) -> Unit){
        this.onItemClick = onItemClick
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OtherNewsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = NewsItemBinding.inflate(inflater, parent, false)
        return OtherNewsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OtherNewsViewHolder, position: Int) {
       holder.bind(currentList[position], onItemClick)
    }

    inner class OtherNewsViewHolder(private val binding: NewsItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(data: NewsListModel, onItemClick: ((news: NewsListModel) -> Unit)?){
           binding.apply {
               tvTitle.text = data.title
               sivImage.load(data.image){
                   crossfade(true)
                   crossfade(1000)
               }
               tvPublishedTime.text = convertDate(data.date)
               newsItem.setOnClickListener {
                   onItemClick?.invoke(data)
               }
           }
        }
    }
    companion object {
        val DIFF_UTIL = object : DiffUtil.ItemCallback<NewsListModel>() {
            override fun areItemsTheSame(
                oldItem: NewsListModel,
                newItem: NewsListModel
            ): Boolean {
                return oldItem.title == newItem.title
            }

            override fun areContentsTheSame(
                oldItem: NewsListModel,
                newItem: NewsListModel
            ): Boolean {
                return oldItem.id == newItem.id &&
                        oldItem.title == newItem.title &&
                        oldItem.source == newItem.source
            }
        }
    }

    private fun convertDate(data:Int):String {
        try {
            val dateAPI = DateTimeFormatter.ISO_INSTANT.format(java.time.Instant.ofEpochSecond(data.toLong()))
            val zoneDateTime = ZonedDateTime.parse(dateAPI)
            val localZonedDateTime = zoneDateTime.withZoneSameInstant(ZoneId.of("Asia/Baku"))
            val date = localZonedDateTime.format(
                DateTimeFormatter.ofPattern("dd MMMM, HH:mm").withLocale(
                    Locale("ru_RU")
                ))
            return date

        }catch (e:Exception){
            val dateAPI = DateTimeFormatter.ISO_INSTANT.format(java.time.Instant.ofEpochSecond(data.toLong()))
            val zoneDateTime = ZonedDateTime.parse(dateAPI)
            val localZonedDateTime = zoneDateTime.withZoneSameInstant(ZoneId.of("Asia/Baku"))
            val date = localZonedDateTime.format(DateTimeFormatter.ofPattern("dd MMMM, HH:mm"))
            return date
        }
    }
}