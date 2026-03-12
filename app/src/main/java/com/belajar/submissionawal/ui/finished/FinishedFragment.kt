package com.belajar.submissionawal.ui.finished

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
import com.belajar.submissionawal.databinding.FragmentFinishedBinding
import com.belajar.submissionawal.ui.EventViewModel
import com.belajar.submissionawal.ui.ViewModelFactory
import com.belajar.submissionawal.ui.adapter.EventAdapter

class FinishedFragment : Fragment() {

    private var _binding: FragmentFinishedBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFinishedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val layoutManager = LinearLayoutManager(requireContext())
        binding.rvEvents.layoutManager = layoutManager

        val adapter = EventAdapter { event ->
            val intent = android.content.Intent(requireContext(), com.belajar.submissionawal.ui.detail.DetailActivity::class.java)
            intent.putExtra(com.belajar.submissionawal.ui.detail.DetailActivity.EXTRA_EVENT_ID, event.id)
            startActivity(intent)
        }
        binding.rvEvents.adapter = adapter

        val pref = SettingPreferences.getInstance(requireContext().dataStore)
        val factory = ViewModelFactory.getInstance(requireContext(), pref)
        val viewModel = ViewModelProvider(requireActivity(), factory)[EventViewModel::class.java]

        viewModel.finishedEvents.observe(viewLifecycleOwner) { events ->
            adapter.submitList(events)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            if (message != null) {
                Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.getFinishedEvents()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
