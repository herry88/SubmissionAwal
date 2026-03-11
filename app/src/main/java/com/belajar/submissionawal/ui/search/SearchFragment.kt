package com.belajar.submissionawal.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.belajar.submissionawal.databinding.FragmentSearchBinding
import com.belajar.submissionawal.ui.EventViewModel
import com.belajar.submissionawal.ui.adapter.EventAdapter

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = EventAdapter()
        binding.rvResults.layoutManager = LinearLayoutManager(requireContext())
        binding.rvResults.adapter = adapter

        val viewModel = ViewModelProvider(requireActivity())[EventViewModel::class.java]

        binding.searchView.setupWithSearchBar(binding.searchBar)
        binding.searchView.editText.setOnEditorActionListener { textView, _, _ ->
            val query = textView.text.toString()
            if (query.isNotEmpty()) {
                binding.searchBar.setText(query)
                binding.searchView.hide()
                viewModel.searchEvents(query)
                Toast.makeText(requireContext(), "Mencari: $query", Toast.LENGTH_SHORT).show()
            }
            false
        }

        viewModel.searchResults.observe(viewLifecycleOwner) { events ->
            if (events != null) {
                adapter.submitList(events)
                if (events.isEmpty()) {
                    Toast.makeText(requireContext(), "Event tidak ditemukan", Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            if (message.isNotEmpty()) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
