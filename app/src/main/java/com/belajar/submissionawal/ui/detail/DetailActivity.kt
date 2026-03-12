package com.belajar.submissionawal.ui.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.lifecycle.ViewModelProvider
import com.belajar.submissionawal.R
import com.belajar.submissionawal.data.local.datastore.SettingPreferences
import com.belajar.submissionawal.data.local.datastore.dataStore
import com.belajar.submissionawal.data.response.ListEventsItem
import com.belajar.submissionawal.databinding.ActivityDetailBinding
import com.belajar.submissionawal.ui.ViewModelFactory
import com.bumptech.glide.Glide

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private lateinit var viewModel: DetailViewModel
    private var currentEvent: ListEventsItem? = null
    private var isFavorite: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Detail Event"

        val eventId = intent.getIntExtra(EXTRA_EVENT_ID, -1)
        
        val factory = ViewModelFactory.getInstance(this, SettingPreferences.getInstance(dataStore))
        viewModel = ViewModelProvider(this, factory)[DetailViewModel::class.java]

        if (eventId != -1) {
            viewModel.getDetailEvent(eventId.toString())
            viewModel.getFavoriteEventById(eventId.toString()).observe(this) { favorite ->
                isFavorite = favorite != null
                updateFavoriteIcon()
            }
        }

        viewModel.eventDetail.observe(this) { event ->
            if (event != null) {
                currentEvent = event
                displayEvent(event)
            }
        }

        viewModel.isLoading.observe(this) {
            showLoading(it)
        }

        viewModel.errorMessage.observe(this) {
            if (it != null) {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }

        binding.fabFavorite.setOnClickListener {
            currentEvent?.let { event ->
                if (isFavorite) {
                    viewModel.deleteFavorite(event.id.toString(), event.name, event.mediaCover)
                    Toast.makeText(this, "Removed from favorites", Toast.LENGTH_SHORT).show()
                } else {
                    viewModel.saveFavorite(event)
                    Toast.makeText(this, "Added to favorites", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateFavoriteIcon() {
        if (isFavorite) {
            binding.fabFavorite.setImageResource(R.drawable.ic_favorite)
        } else {
            binding.fabFavorite.setImageResource(R.drawable.ic_favorite_border)
        }
    }

    private fun displayEvent(event: ListEventsItem) {
        binding.apply {
            tvEventName.text = event.name
            tvOwnerName.text = "Penyelenggara: ${event.ownerName}"
            tvBeginTime.text = "Waktu: ${event.beginTime}"
            val sisaKuota = event.quota - event.registrant
            tvQuota.text = "Sisa Kuota: $sisaKuota"
            
            tvDescription.text = if (event.description.isNotEmpty()) {
                HtmlCompat.fromHtml(event.description, HtmlCompat.FROM_HTML_MODE_LEGACY)
            } else {
                "Tidak ada deskripsi."
            }

            val imageUrl = if (event.mediaCover.isNotEmpty()) event.mediaCover else event.imageLogo
            Glide.with(this@DetailActivity)
                .load(imageUrl)
                .into(ivEventImage)

            btnOpenLink.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(event.link))
                startActivity(intent)
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    companion object {
        const val EXTRA_EVENT_ID = "EXTRA_EVENT_ID"
    }
}
