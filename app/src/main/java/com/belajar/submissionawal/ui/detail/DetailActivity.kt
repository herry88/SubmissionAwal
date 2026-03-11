package com.belajar.submissionawal.ui.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import com.belajar.submissionawal.data.response.DetailResponse
import com.belajar.submissionawal.data.response.ListEventsItem
import com.belajar.submissionawal.data.retrofit.ApiConfig
import com.belajar.submissionawal.databinding.ActivityDetailBinding
import com.bumptech.glide.Glide
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Detail Event"

        val eventId = intent.getIntExtra(EXTRA_EVENT_ID, -1)
        if (eventId != -1) {
            getDetailEvent(eventId.toString())
        }
    }

    private fun getDetailEvent(id: String) {
        showLoading(true)
        val client = ApiConfig.getApiService().getDetailEvent(id)
        client.enqueue(object : Callback<DetailResponse> {
            override fun onResponse(call: Call<DetailResponse>, response: Response<DetailResponse>) {
                showLoading(false)
                if (response.isSuccessful) {
                    val event = response.body()?.event
                    if (event != null) {
                        displayEvent(event)
                    } else {
                        Toast.makeText(this@DetailActivity, "Gagal memuat detail event: Data kosong", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@DetailActivity, "Gagal memuat detail event: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<DetailResponse>, t: Throwable) {
                showLoading(false)
                Toast.makeText(this@DetailActivity, "Koneksi gagal: ${t.message}", Toast.LENGTH_SHORT).show()
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
