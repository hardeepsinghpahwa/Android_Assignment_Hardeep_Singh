package com.example.assignment.mainScreen

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.util.LruCache
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.assignment.R
import com.example.assignment.databinding.ItemArticleBinding
import com.example.assignment.diskCache.DiskCacheManager
import com.example.assignment.model.ArticleDataItem
import com.example.assignment.model.BackupDetails
import com.example.assignment.model.Thumbnail
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL
import javax.inject.Inject


class ArticleAdapter @Inject constructor() : RecyclerView.Adapter<ArticleAdapter.ListViewHolder>() {

    private var articles = ArrayList<ArticleDataItem>()
    private var mMemoryCache: LruCache<String, Bitmap>? =
        object : LruCache<String, Bitmap>((Runtime.getRuntime().maxMemory() / 1024).toInt() / 8) {

            override fun sizeOf(key: String, bitmap: Bitmap): Int {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.byteCount / 1024
            }
        }


    private var diskCacheManager: DiskCacheManager? = null

    inner class ListViewHolder(val binding: ItemArticleBinding) : ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {

        Log.d(
            "LOADING_FROM_CACHE_SIZE",
            ((Runtime.getRuntime().maxMemory() / 1024).toInt() / 8).toString()
        )
        val layout = ItemArticleBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ListViewHolder(layout)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(
        context: Context,
        articles: ArrayList<ArticleDataItem>
    ) {
        articles.add(
            ArticleDataItem(
                BackupDetails("", ""), "", "", "", 0, "", "", Thumbnail(
                    1, "", "", "", "",
                    intArrayOf(0, 1).toList(), 0
                ), ""
            )
        )
        this.articles = articles

        Log.d("ARTICLE_DATE_SIZE", articles.size.toString())
        diskCacheManager = DiskCacheManager(context)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return articles.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {

        with(holder) {
            //binding.highTemp.text = "${this.roundToInt()}°C"
            binding.thumbnail.setImageDrawable(
                ContextCompat.getDrawable(
                    binding.root.context,
                    R.drawable.loading
                )
            )

            val cacheBitmap: Bitmap? =
                getBitmapFromMemCache(articles[position].thumbnail.domain + "/" + articles[position].thumbnail.basePath + "/0/" + articles[position].thumbnail.key)
            val diskBitmap: Bitmap? =
                diskCacheManager?.findBitmap(articles[position].thumbnail.domain + "/" + articles[position].thumbnail.basePath + "/0/" + articles[position].thumbnail.key)
            if (cacheBitmap != null) {
                Log.d("LOADING_FROM", "CACHE")
                binding.thumbnail.setImageBitmap(cacheBitmap)
                binding.thumbnail.visibility=View.VISIBLE
                binding.loader.visibility=View.GONE
            } else if (diskBitmap != null) {
                Log.d("LOADING_FROM", "DISK")
                addBitmapToMemoryCache(articles[position].thumbnail.domain + "/" + articles[position].thumbnail.basePath + "/0/" + articles[position].thumbnail.key, diskBitmap)
                binding.thumbnail.setImageBitmap(diskBitmap)
                binding.thumbnail.visibility=View.VISIBLE
                binding.loader.visibility=View.GONE
            } else {
                CoroutineScope(Dispatchers.IO).launch {

                    val bitmap =
                        downloadBitmap(
                            articles[position].thumbnail.domain + "/" + articles[position].thumbnail.basePath + "/0/" + articles[position].thumbnail.key,
                            articles[position].thumbnail.domain + "/" + articles[position].thumbnail.basePath + "/0/" + articles[position].thumbnail.key
                        )
                    withContext(Dispatchers.Main) {
                        Log.d("LOADING_FROM", "NETWORK")
                        if (bitmap != null) {
                            binding.thumbnail.setImageBitmap(bitmap)
                        } else {
                            Log.d("BITMAP", "NULL")
                            binding.thumbnail.setImageDrawable(
                                ContextCompat.getDrawable(
                                    binding.root.context,
                                    R.drawable.error
                                )
                            )
                        }
                        binding.loader.visibility=View.GONE
                        binding.thumbnail.visibility=View.VISIBLE
                    }
                }
            }
        }
    }

    private fun addBitmapToMemoryCache(key: String?, bitmap: Bitmap?) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache?.put(key, bitmap)
        }
    }

    private fun getBitmapFromMemCache(key: String?): Bitmap? {
        return mMemoryCache?.get(key)
    }

    private fun downloadBitmap(imageUrl: String, key: String): Bitmap? {
        return try {
            val conn = URL(imageUrl).openConnection()
            conn.connect()
            val inputStream = conn.getInputStream()
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()
            addBitmapToMemoryCache(key, bitmap)
            diskCacheManager?.putBitmap(key, bitmap)
            Log.e("LOADING_FROM_BITMAP_SAVED", "SAVED")
            Log.e("LOADING_FROM_BITMAP_SAVED_SIZE", mMemoryCache?.size().toString())
            bitmap
        } catch (e: Exception) {
            Log.e("LOADING_FROM_BITMAP_EXCEPTION", "Exception $e")
            null
        }
    }

}




