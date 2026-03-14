package com.belajar.submissionawal.ui.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.Observer
import com.belajar.submissionawal.R
import com.belajar.submissionawal.data.local.datastore.SettingPreferences
import com.belajar.submissionawal.data.local.datastore.dataStore
import com.belajar.submissionawal.data.response.ListEventsItem
import com.belajar.submissionawal.databinding.ActivityDetailBinding
import com.belajar.submissionawal.ui.ViewModelFactory
import com.belajar.submissionawal.data.local.entity.FavoriteEvent
import com.bumptech.glide.Glide

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private lateinit var viewModel: DetailViewModel
    private var currentEvent: ListEventsItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            setSupportActionBar(toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = "Detail Event"

            val eventId = intent.getIntExtra(EXTRA_EVENT_ID, -1)
            
            val factory = ViewModelFactory.getInstance(this@DetailActivity, SettingPreferences.getInstance(dataStore))
            viewModel = ViewModelProvider(this@DetailActivity, factory).get(DetailViewModel::class.java)

            if (eventId != -1) {
                viewModel.getDetailEvent(eventId.toString())
                
                viewModel.getFavoriteEventById(eventId).observe(this@DetailActivity, Observer { favoriteEvent ->
                    if (favoriteEvent != null) {
                        fabFavorite.setImageResource(R.drawable.ic_favorite)
                        fabFavorite.setOnClickListener {
                            viewModel.deleteFavorite(favoriteEvent)
                        }
                    } else {
                        fabFavorite.setImageResource(R.drawable.ic_favorite_border)
                        fabFavorite.setOnClickListener {
                            currentEvent?.let { event ->
                                val newFavorite = FavoriteEvent(
                                    id = event.id,
                                    name = event.name ?: "-",
                                    mediaCover = if (!event.mediaCover.isNullOrEmpty()) event.mediaCover else event.imageLogo
                                )
                                viewModel.insertFavorite(newFavorite)
                            }
                        }
                    }
                })
            }

            viewModel.eventDetail.observe(this@DetailActivity, Observer { event ->
                if (event != null) {
                    currentEvent = event
                    displayEvent(event)
                    fabFavorite.visibility = View.VISIBLE
                }
            })
        }

        viewModel.isLoading.observe(this, Observer { isLoading ->
            showLoading(isLoading)
        })

        viewModel.errorMessage.observe(this, Observer { message ->
            if (message != null) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        })

    }


    private fun displayEvent(event: ListEventsItem) {
        binding.apply {
            tvEventName.text = event.name ?: "-"
            tvOwnerName.text = "Penyelenggara: ${event.ownerName ?: "-"}"
            tvBeginTime.text = "Waktu: ${formatToIndonesian(event.beginTime)}"

            tvEventName.text = event.name
            tvOwnerName.text = "Penyelenggara: ${event.ownerName}"
            tvBeginTime.text = "Waktu: ${event.beginTime}"

            val sisaKuota = event.quota - event.registrants
            tvQuota.text = "Sisa Kuota: $sisaKuota"
            
            val desc = event.description
            tvDescription.text = if (!desc.isNullOrEmpty()) {
                HtmlCompat.fromHtml(desc, HtmlCompat.FROM_HTML_MODE_LEGACY)
            } else {
                "Tidak ada deskripsi."
            }

            val imageUrl = if (!event.mediaCover.isNullOrEmpty()) event.mediaCover else event.imageLogo
            Glide.with(this@DetailActivity)
                .load(imageUrl)
                .into(ivEventImage)

            btnOpenLink.setOnClickListener {
                val link = event.link
                if (!link.isNullOrEmpty()) {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
                    startActivity(intent)
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun formatToIndonesian(dateString: String?): String {
        if (dateString.isNullOrEmpty()) return "-"
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale("id", "ID"))
            val outputFormat = SimpleDateFormat("dd MMMM yyyy, HH.mm 'WIB'", Locale("id", "ID"))
            outputFormat.timeZone = TimeZone.getTimeZone("Asia/Jakarta")
            val date = inputFormat.parse(dateString)
            if (date != null) outputFormat.format(date) else dateString
        } catch (e: Exception) {
            dateString
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    companion object {
        const val EXTRA_EVENT_ID = "EXTRA_EVENT_ID"
    }
}
