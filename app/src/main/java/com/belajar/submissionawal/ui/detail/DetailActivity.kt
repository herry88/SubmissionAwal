package com.belajar.submissionawal.ui.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
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

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Detail Event"

        val eventId = intent.getIntExtra(EXTRA_EVENT_ID, -1)
        
        val factory = ViewModelFactory.getInstance(this, SettingPreferences.getInstance(dataStore))
        viewModel = ViewModelProvider(this, factory).get(DetailViewModel::class.java)

        if (eventId != -1) {
            viewModel.getDetailEvent(eventId.toString())
            
            viewModel.getFavoriteEventById(eventId).observe(this, Observer { favoriteEvent ->
                if (favoriteEvent != null) {
                    binding.fabFavorite.setImageResource(R.drawable.ic_favorite)
                    binding.fabFavorite.setOnClickListener {
                        viewModel.deleteFavorite(favoriteEvent)
                    }
                } else {
                    binding.fabFavorite.setImageResource(R.drawable.ic_favorite_border)
                    binding.fabFavorite.setOnClickListener {
                        currentEvent?.let { event ->
                            val newFavorite = FavoriteEvent(
                                id = event.id,
                                name = event.name,
                                mediaCover = event.mediaCover.ifEmpty { event.imageLogo }
                            )
                            viewModel.insertFavorite(newFavorite)
                        }
                    }
                }
            })
        }

        viewModel.eventDetail.observe(this, Observer { event ->
            if (event != null) {
                currentEvent = event
                displayEvent(event)
                binding.fabFavorite.visibility = View.VISIBLE
            }
        })

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
            tvEventName.text = event.name
            tvOwnerName.text = "Penyelenggara: ${event.ownerName}"
            tvBeginTime.text = "Waktu: ${event.beginTime}"
            val sisaKuota = event.quota - event.registrants
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
