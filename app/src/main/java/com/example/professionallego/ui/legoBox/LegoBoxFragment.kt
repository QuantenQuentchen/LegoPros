package com.example.professionallego.ui.legoBox

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo.IME_ACTION_DONE
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.professionallego.R
import com.example.professionallego.databinding.FragmentLegoBoxBinding
import com.example.professionallego.ui.AppSharedViewModel
import com.example.professionallego.ui.LegoBoxBox.LegoBoxBoxData
import com.google.android.material.floatingactionbutton.FloatingActionButton

class LegoBoxFragment : Fragment() {

    private var _binding: FragmentLegoBoxBinding? = null
    private lateinit var model: AppSharedViewModel;
    private lateinit var instanceLiveData: LiveData<LegoBoxBoxData>
    //Views
    private lateinit var legoBoxRecyclerView: RecyclerView
    private lateinit var legoBoxNameEditText: EditText
    private lateinit var legoBoxSubmitBtn: Button
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var id: Int = 0
    companion object {
        fun newInstance(id: Int): LegoBoxFragment {
            val fragment = LegoBoxFragment()
            fragment.id = id
            return fragment
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        id = arguments?.getInt("id") ?: 0
        val galleryViewModel =
            ViewModelProvider(this).get(LegoBoxViewModel::class.java)

        _binding = FragmentLegoBoxBinding.inflate(inflater, container, false)
        val root: View = binding.root
        //Accquire Global ViewModel
        model = ViewModelProvider(requireActivity())[AppSharedViewModel::class.java]
        //Set currentId
        model.currentLegoBoxBoxId = id

        //Get Views
        legoBoxRecyclerView = binding.recyclerView;
        legoBoxNameEditText = binding.legoBoxNameEditText;
        legoBoxSubmitBtn = binding.legoBoxSubmitButton;

        legoBoxRecyclerView.layoutManager = LinearLayoutManager(requireContext());
        legoBoxRecyclerView.adapter = LegoBoxAdapter();

        legoBoxSubmitBtn.setOnClickListener {
            model.setLegoBoxBoxName(id, legoBoxNameEditText.text.toString())
        }
        legoBoxNameEditText.setOnEditorActionListener { v, actionId, event ->
            if(actionId == IME_ACTION_DONE){
                model.setLegoBoxBoxName(id, legoBoxNameEditText.text.toString())
                findNavController().popBackStack()
            }
            true
        }

        model.getLegoBoxBoxLiveName(id).observe(viewLifecycleOwner) {
            legoBoxNameEditText.setText(it)
        }

        model.getLegoBoxLiveData(id)?.observe(viewLifecycleOwner) {
            (legoBoxRecyclerView.adapter as LegoBoxAdapter).updateItems(it)
        }


        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}