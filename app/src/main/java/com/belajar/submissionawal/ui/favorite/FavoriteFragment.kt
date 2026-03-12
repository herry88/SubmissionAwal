package com.belajar.submissionawal.ui.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.belajar.submissionawal.data.local.datastore.SettingPreferences
import com.belajar.submissionawal.data.local.datastore.dataStore
import com.belajar.submissionawal.data.response.ListEventsItem
import com.belajar.submissionawal.databinding.FragmentFavoriteBinding
import com.belajar.submissionawal.ui.ViewModelFactory
import com.belajar.submissionawal.ui.adapter.EventAdapter

class FavoriteFragment : Fragment() {

    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
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

        val pref = SettingPreferences.getInstance(requireContext().dataStore)
        val factory = ViewModelFactory.getInstance(requireContext(), pref)
        val viewModel = ViewModelProvider(this, factory)[FavoriteViewModel::class.java]

        val adapter = EventAdapter()
        binding.rvFavorite.adapter = adapter

        viewModel.getFavoriteEvents().observe(viewLifecycleOwner) { favorites ->
            binding.progressBar.visibility = View.GONE
            val items = favorites.map {
                ListEventsItem(
                    id = it.id.toInt(),
                    name = it.name,
                    mediaCover = it.mediaCover ?: "",
                    imageLogo = it.mediaCover ?: "",
                    // Providing dummy values for other mandatory fields
                    quota = 0,
                    registrants = 0,
                    beginTime = "",
                    endTime = "",
                    link = "",
                    description = "",
                    ownerName = "",
                    summary = "",
                    cityName = "",
                    category = ""
                )
            }
            adapter.submitList(items)
            binding.tvEmpty.visibility = if (items.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
