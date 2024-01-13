package com.example.professionallego.ui.historydetails

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.professionallego.R
import com.example.professionallego.databinding.FragmentHistoryDetailsBinding
import com.example.professionallego.data.AppSharedViewModel
import com.example.professionallego.ui.calculator.CalculatorOutputAdapter
import com.example.professionallego.ui.history.HistoryData
import java.text.SimpleDateFormat
import java.util.Locale

class HistoryDetailsFragment : Fragment() {

    private var _binding: FragmentHistoryDetailsBinding? = null

    //private lateinit var viewModel: HistoryDetailsViewModel

    private lateinit var model: AppSharedViewModel

    private lateinit var OutputRecView: RecyclerView
    private lateinit var InputView: TextView
    private lateinit var SetCubeView: ImageView
    private lateinit var SetNameView: TextView
    private lateinit var CreatedAtView: TextView
    private var id: Int = 0

    private val sdf: SimpleDateFormat = SimpleDateFormat("yyy/mm/dd hh:mm", Locale.getDefault())

    private lateinit var HistoryData: HistoryData


    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryDetailsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        id = arguments?.getInt("id") ?: 0
        model = ViewModelProvider(requireActivity())[AppSharedViewModel::class.java]

        HistoryData = model.getHistoryData(id)!!

        val createdAtStr: String = requireContext().getString(R.string.history_item_createdAt)

        //Bindings
        OutputRecView = binding.historyDetailsRecyclerView
        InputView = binding.historyDetailsInput
        SetCubeView = binding.historyDetailsSetCube
        SetNameView = binding.historyDetailsSetName
        CreatedAtView = binding.historyDetailsCreatedAt

        //Other Settings
        InputView.text = HistoryData.input.toString()
        SetNameView.text = HistoryData.legoBoxId.toString()
        CreatedAtView.text = buildString {
        append(createdAtStr)
        append(": ")
        append(sdf.format(HistoryData.timestamp)) //RecViewSettings
    }
        //RecViewSettings
        OutputRecView.layoutManager = LinearLayoutManager(requireContext())
        OutputRecView.adapter = CalculatorOutputAdapter(HistoryData.output)

        return root
    }


}