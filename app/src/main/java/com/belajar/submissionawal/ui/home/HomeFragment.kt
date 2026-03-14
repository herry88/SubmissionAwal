package com.belajar.submissionawal.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.belajar.submissionawal.data.local.datastore.SettingPreferences
import com.belajar.submissionawal.data.local.datastore.dataStore
import com.belajar.submissionawal.databinding.FragmentHomeBinding
import com.belajar.submissionawal.ui.EventViewModel
import com.belajar.submissionawal.ui.ViewModelFactory
import com.belajar.submissionawal.ui.adapter.EventAdapter

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            val upcomingAdapter = EventAdapter { event ->
                val intent = android.content.Intent(requireContext(), com.belajar.submissionawal.ui.detail.DetailActivity::class.java)
                intent.putExtra(com.belajar.submissionawal.ui.detail.DetailActivity.EXTRA_EVENT_ID, event.id)
                startActivity(intent)
            }
            rvUpcoming.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            rvUpcoming.adapter = upcomingAdapter

            val finishedAdapter = EventAdapter { event ->
                val intent = android.content.Intent(requireContext(), com.belajar.submissionawal.ui.detail.DetailActivity::class.java)
                intent.putExtra(com.belajar.submissionawal.ui.detail.DetailActivity.EXTRA_EVENT_ID, event.id)
                startActivity(intent)
            }
            rvFinished.layoutManager = LinearLayoutManager(requireContext())
            rvFinished.adapter = finishedAdapter

            val pref = SettingPreferences.getInstance(requireContext().dataStore)
            val factory = ViewModelFactory.getInstance(requireContext(), pref)
            val viewModel = ViewModelProvider(requireActivity(), factory)[EventViewModel::class.java]

            viewModel.upcomingEvents.observe(viewLifecycleOwner) { events ->
                upcomingAdapter.submitList(events.take(5))
            }

            viewModel.finishedEvents.observe(viewLifecycleOwner) { events ->
                finishedAdapter.submitList(events.take(5))
            }

            viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
                progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            }

            viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
                if (message != null) {
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                }
            }

            viewModel.getHomeUpcomingEvents()
            viewModel.getHomeFinishedEvents()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
