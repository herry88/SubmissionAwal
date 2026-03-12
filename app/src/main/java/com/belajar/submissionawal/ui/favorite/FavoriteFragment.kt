package com.belajar.submissionawal.ui.favorite

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.belajar.submissionawal.data.local.datastore.dataStore
import com.belajar.submissionawal.databinding.FragmentFavoriteBinding
import com.belajar.submissionawal.ui.adapter.EventAdapter
import com.belajar.submissionawal.ui.ViewModelFactory
import com.belajar.submissionawal.ui.detail.DetailActivity

class FavoriteFragment : Fragment() {

    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FavoriteViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext(), com.belajar.submissionawal.data.local.datastore.SettingPreferences.getInstance(requireContext().dataStore))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val layoutManager = LinearLayoutManager(requireContext())
        binding.rvFavorite.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(requireContext(), layoutManager.orientation)
        binding.rvFavorite.addItemDecoration(itemDecoration)

        viewModel.getFavoriteEvents().observe(viewLifecycleOwner) { favoriteEvents ->
            if (favoriteEvents.isNullOrEmpty()) {
                binding.tvNoFavorite.visibility = View.VISIBLE
                binding.rvFavorite.visibility = View.GONE
            } else {
                binding.tvNoFavorite.visibility = View.GONE
                binding.rvFavorite.visibility = View.VISIBLE
                
                val adapter = EventAdapter { event ->
                    val intent = Intent(requireContext(), DetailActivity::class.java)
                    intent.putExtra(DetailActivity.EXTRA_EVENT_ID, event.id)
                    startActivity(intent)
                }
                
                val eventsList = favoriteEvents.map {
                    com.belajar.submissionawal.data.response.ListEventsItem(
                        quota = 0,
                        registrants = 0,
                        beginTime = "",
                        endTime = "",
                        link = "",
                        description = "",
                        id = it.id,
                        imageLogo = it.mediaCover ?: "",
                        mediaCover = it.mediaCover ?: "",
                        name = it.name,
                        ownerName = "",
                        summary = "",
                        cityName = "",
                        category = ""
                    )
                }
                binding.rvFavorite.adapter = adapter
                adapter.submitList(eventsList)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
