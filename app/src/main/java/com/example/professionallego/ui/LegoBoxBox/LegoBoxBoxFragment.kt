package com.example.professionallego.ui.LegoBoxBox

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.TextUtils.replace
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.professionallego.R
import com.example.professionallego.databinding.FragmentLegoBoxBoxBinding
import com.example.professionallego.ui.AppSharedViewModel
import com.example.professionallego.ui.legoBox.LegoBoxFragment

class LegoBoxBoxFragment : Fragment() {
    private var _binding: FragmentLegoBoxBoxBinding? = null

    private lateinit var model: AppSharedViewModel;
    private lateinit var legoBoxBoxRecyclerView: RecyclerView;

    private val binding get() = _binding!!

    private lateinit var viewModel: LegoBoxBoxViewModel

    private var isSelectMode = false;

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val isSelectMode = arguments?.getBoolean("selectMode") ?: false

        val LegoBoxBoxViewModel =
            ViewModelProvider(this).get(LegoBoxBoxViewModel::class.java)
        _binding = FragmentLegoBoxBoxBinding.inflate(inflater, container, false)
        val root: View = binding.root
        model = ViewModelProvider(requireActivity())[AppSharedViewModel::class.java]

        //Bindings
        legoBoxBoxRecyclerView = binding.legoBoxBoxRecyclerView;

        //LayoutManager
        legoBoxBoxRecyclerView.layoutManager = androidx.recyclerview.widget.GridLayoutManager(context, 3)
        val legoBoxBoxArrayList = model.getLegoBoxBoyDataAsArray()
        if(!isSelectMode) {
            legoBoxBoxRecyclerView.adapter =
                LegoBoxBoxAdapter(legoBoxBoxArrayList, ::SelectLegoBox, ::AddLegoBox)
        }else{
            legoBoxBoxRecyclerView.adapter =
                LegoBoxBoxAdapter(legoBoxBoxArrayList, ::selectLegoBoxforCalc, null)
        }
        model.LegoBoxBox.observe(viewLifecycleOwner) {
            val legoBoxBoxArrayList = ArrayList(it.values)
            (legoBoxBoxRecyclerView.adapter as LegoBoxBoxAdapter).updateItems(legoBoxBoxArrayList)
        }

        return root
    }

    private fun SelectLegoBox(Item: LegoBoxBoxData){
        val bundle = bundleOf("id" to Item.id)
        findNavController().navigate(R.id.action_nav_lego_box_box_to_legoBoxFragment, bundle)
    }

    private fun AddLegoBox(){
        val id = model.addLegoBoxBox()
        val bundle = bundleOf("id" to id)
        findNavController().navigate(R.id.action_nav_lego_box_box_to_legoBoxFragment, bundle)
    }


    private fun selectLegoBoxforCalc(Item: LegoBoxBoxData){
        model.calculationBoxBoxId = Item.id
        findNavController().popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}