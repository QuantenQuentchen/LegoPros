package com.example.professionallego.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.professionallego.R
import com.example.professionallego.databinding.FragmentHistoryBinding
import com.example.professionallego.data.AppSharedViewModel

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private lateinit var model: AppSharedViewModel
    private lateinit var recView: RecyclerView
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        val root: View = binding.root
        //get model
        model = ViewModelProvider(requireActivity())[AppSharedViewModel::class.java]

        //bindings
        recView = binding.historyRecView

        recView.layoutManager = LinearLayoutManager(requireContext())
        val history:ArrayList<HistoryData> = model.History.value ?: arrayListOf()
        recView.adapter = HistoryAdapter(history, requireContext(), ::onHistoryClick)

        model.History.observe(viewLifecycleOwner) {
            (recView.adapter as HistoryAdapter).updateItems(it)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun onHistoryClick(item: HistoryData){
        val bundle: Bundle = bundleOf("id" to item.id)
        findNavController().navigate(R.id.action_nav_history_to_nav_history_details, bundle)
    }
}