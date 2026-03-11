package com.belajar.submissionawal.ui.upcoming

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.belajar.submissionawal.databinding.FragmentUpcomingBinding
import com.belajar.submissionawal.ui.EventViewModel
import com.belajar.submissionawal.ui.adapter.EventAdapter

class UpcomingFragment : Fragment() {

    private var _binding: FragmentUpcomingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpcomingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val layoutManager = LinearLayoutManager(requireContext())
        binding.rvEvents.layoutManager = layoutManager

        val viewModel = ViewModelProvider(requireActivity())[EventViewModel::class.java]

        viewModel.upcomingEvents.observe(viewLifecycleOwner) { events ->
            val adapter = EventAdapter()
            adapter.submitList(events)
            binding.rvEvents.adapter = adapter
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            if (message.isNotEmpty()) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.getUpcomingEvents()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
