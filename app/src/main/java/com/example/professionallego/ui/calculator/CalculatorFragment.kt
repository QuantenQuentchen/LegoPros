package com.example.professionallego.ui.calculator

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import androidx.core.view.accessibility.AccessibilityEventCompat.setAction
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.professionallego.R
import com.example.professionallego.databinding.FragmentCalculatorBinding
import com.example.professionallego.ui.AppSharedViewModel
import com.example.professionallego.ui.history.HistoryData
import com.example.professionallego.ui.legoBox.LegoItemData
import com.google.android.material.snackbar.Snackbar

class CalculatorFragment : Fragment() {

    private var _binding: FragmentCalculatorBinding? = null

    private lateinit var calcInputEditText: EditText
    private lateinit var calcResultRecyView: RecyclerView
    private lateinit var calcButton: Button
    private lateinit var selectSetBtn: Button

    private lateinit var model: AppSharedViewModel;

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var selectedBox: ArrayList<LegoItemData> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(CalculatorViewModel::class.java)

        _binding = FragmentCalculatorBinding.inflate(inflater, container, false)

        model = ViewModelProvider(requireActivity())[AppSharedViewModel::class.java]

        //Bindings
        calcInputEditText = binding.calculatorInputEditText
        calcResultRecyView = binding.calculatorResultRecyclerView
        calcButton = binding.calculatorCalcButton
        selectSetBtn = binding.calculatorSelectSetBtn
        //Settings
        calcInputEditText.inputType = EditorInfo.TYPE_CLASS_NUMBER

        //Listeners
        calcButton.setOnClickListener {
            val worked = validateAndCalc()
        }

        calcInputEditText.setOnEditorActionListener { v, actionId, event ->
            if(actionId == EditorInfo.IME_ACTION_DONE){
                val worked = validateAndCalc()
            }
            true
        }

        selectSetBtn.setOnClickListener {
            val bundle = Bundle()
            bundle.putBoolean("selectMode", true)
            findNavController().navigate(R.id.action_nav_calculator_to_nav_lego_box_box, bundle)
        }

        //Setters

        calcResultRecyView.adapter = CalculatorOutputAdapter(ArrayList())
        calcResultRecyView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)



        val root: View = binding.root

        homeViewModel.text.observe(viewLifecycleOwner) {
        }

        if(model.calculationBoxBoxId == null){
            selectSetBtn.text = ""
        }
        if(model.calculationBoxBoxId != null){
            model.getLegoBoxBoxLiveName(model.calculationBoxBoxId?:0).observe(viewLifecycleOwner) {
                selectSetBtn.text = it
            }
        }



        return root
    }


    private fun setBoxData(){
        var calcId = model.calculationBoxBoxId
        if(calcId == null){
            Snackbar.make(binding.root, getString(R.string.calculation_no_lego_box_set), Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show()
            return
        }

        val BoxList = model.getLegoBoxDataforCalc(calcId)
        if(BoxList == null){
            Snackbar.make(binding.root, getString(R.string.calculation_no_lego_box_set), Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show()
            return
        }
        if(BoxList.size == 0){
            Snackbar.make(binding.root, getString(R.string.calculation_no_lego_box_set), Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show()
            return
        }
        selectedBox = BoxList
    }


    private fun validateAndCalc(): Boolean{
        setBoxData()
        val desiredLength = calcInputEditText.text.toString().toIntOrNull()
        if(desiredLength == null || desiredLength == 0){
            calcInputEditText.clearFocus()
            Snackbar.make(binding.root, getString(R.string.calculation_no_number), Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show()
            return false
        }
        if(selectedBox == null){
            calcInputEditText.clearFocus()
            Snackbar.make(binding.root, getString(R.string.calculation_no_lego_box), Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show()
            return false
        }
        doCalculation(desiredLength)
        calcInputEditText.clearFocus()
        return true
    }

    private fun doCalculation(input: Int){
        val BoxList = selectedBox
        val result = hurensohn(input, BoxList)
        if(result == null){
            Snackbar.make(binding.root, getString(R.string.calculation_no_lego_box), Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show()
            return
        }
        setRecyclerViewData(result)
        addHistory(result, input, model.calculationBoxBoxId?:0)
    }

    private fun calculateCubes(desiredLength: Int, BoxList: ArrayList<LegoItemData>): ArrayList<CalculatorOutputData>?{
        if(BoxList.size == 0){
            return null
        }

        val SortedBoxList = BoxList.sortedByDescending { it.size }
        var result = ArrayList<CalculatorOutputData>()
        var desiredLength = desiredLength;
        var quotient: Int = 0;
        var remainder: Int = 0;
        for (i in SortedBoxList){
            if(i.size > desiredLength){
                continue
            }
            quotient = desiredLength / i.size
            remainder = desiredLength % i.size

            result.add(CalculatorOutputData(i.id!!, i.name!!, i.size, quotient))
            desiredLength = remainder;
            if(remainder == 0){
                return result
            }
        }
        if(remainder != 0){
            return null
        }
        return result
    }

    private fun hurensohn(desiredLength: Int, BoxList: ArrayList<LegoItemData>): ArrayList<CalculatorOutputData>?{

        if(BoxList.size == 0){
            return null
        }

        val SortedBoxList = BoxList.sortedByDescending { it.size }
        var result = ArrayList<CalculatorOutputData>()

        var remainingLength = desiredLength;
        for(i in SortedBoxList){
            if(i.size > desiredLength){
                continue
            }

            if(remainingLength-i.size < 0){
                continue
            }else {
                result.add(CalculatorOutputData(i.id!!, i.name!!, i.size, 1))
            }
            remainingLength -= i.size
        }
        if(remainingLength != 0){
            return null
        }
        return result
    }


    private fun setRecyclerViewData(data: ArrayList<CalculatorOutputData>){
        val adapter = calcResultRecyView.adapter as CalculatorOutputAdapter
        adapter.items = data
        adapter.notifyDataSetChanged()
    }

    private fun addHistory(items: ArrayList<CalculatorOutputData>, Input: Int, BoxId: Int){
        val currentList = model.History.value ?: arrayListOf()
        currentList.add(HistoryData(currentList.size, System.currentTimeMillis(), BoxId, Input, items))
        model.History.value = currentList
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}