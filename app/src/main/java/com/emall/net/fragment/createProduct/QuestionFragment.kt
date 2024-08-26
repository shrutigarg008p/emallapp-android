package com.emall.net.fragment.createProduct

import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.emall.net.R
import com.emall.net.activity.dashboard.*
import com.emall.net.adapter.CheckBoxAdapter
import com.emall.net.adapter.RadioButtonAdapter
import com.emall.net.listeners.ItemClick
import com.emall.net.listeners.OnItemCheckListener
import com.emall.net.network.model.AddProduct
import com.emall.net.network.model.createdProduct.Question
import com.emall.net.utils.Constants.BOOLEAN_QUESTION
import com.emall.net.utils.Constants.CHECKBOX_QUESTION
import com.emall.net.utils.Constants.DROPDOWN_QUESTION
import com.emall.net.utils.Constants.PRODUCT
import com.emall.net.utils.Constants.QUESTION_LIST
import com.emall.net.utils.Constants.RADIO_BUTTON_QUESTION
import com.emall.net.utils.Constants.TEXT_BOX_QUESTION
import com.emall.net.utils.Utility.makeInvisible
import com.emall.net.utils.Utility.makeVisible
import com.emall.net.utils.Utility.replaceFragment
import kotlinx.android.synthetic.main.fragment_question.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class QuestionFragment : Fragment(), View.OnClickListener, OnItemCheckListener, ItemClick {

    private var questionList = ArrayList<Question>()
    private var currentQuestion = Question()
    private lateinit var checkboxAdapter: CheckBoxAdapter
    private lateinit var radioButtonAdapter: RadioButtonAdapter

    private var checkboxList = ArrayList<String>()
    private var radioButtonList = ArrayList<String>()
    private var dropDownList = ArrayList<String>()

    private var checkboxIdList = ArrayList<Int>()
    private var radioButtonIdList = ArrayList<Int>()
    private var dropDownIdList = ArrayList<Int>()
    // count
    private var currentPosition: Int = 0
    private var radioButtonAnswer: Int? = 0
    private var dropDownAnswer: String? = ""
    private var textAnswer: String = ""

    private var imagePathList = HashMap<String,String>()
    private var answerList = HashMap<Int, String>()

    private var selectedCheckBoxList = ArrayList<Int>()

    private var itemTitleList = ArrayList<String>()

    private var addProduct: AddProduct? = null

    private var questionAnswerList = HashMap<String,String>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_question, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as SellerActivity).showHideToolbar("")
        (activity as SellerActivity).setToolbarTitle(getString(R.string.sell_your_mobile))
        when {
            arguments != null -> {
                questionList = arguments?.getParcelableArrayList(QUESTION_LIST)!!
                addProduct = arguments?.getParcelable(PRODUCT)
                imagePathList = arguments?.getSerializable("imagePath") as HashMap<String, String>
            }
        }

        updateCount(currentPosition + 1, questionList.size)
        setAdapterData()
        updateUIData()

        next_btn.setOnClickListener(this::onClick)
        true_button.setOnClickListener(this::onClick)
        false_button.setOnClickListener(this::onClick)

        edit_text.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (!s.toString().isNullOrEmpty()) {
                    textAnswer = s.toString()
                }
            }

        })

        drop_down_spinner.setOnItemSelectedListener{ view, position, id, item ->
            dropDownAnswer = dropDownList[position -1]
        }
    }

    private fun updateCount(currentCount: Int, totalCount: Int) {
        question_number.text = "${currentCount.toString().toLowerCase(Locale.getDefault())}/${totalCount.toString().toLowerCase(Locale.getDefault())}"
    }

    private fun updateUIData() {
        if (!questionList.isNullOrEmpty()) {
            if (currentPosition < questionList.size) {
                when (questionList[currentPosition].input_type?.type) {
                    BOOLEAN_QUESTION -> {
                        showBooleanLayout()
                        currentQuestion = questionList[currentPosition]
                    }
                    CHECKBOX_QUESTION -> {
                        checkboxList.clear()
                        checkboxIdList.clear()
                        checkboxAdapter.notifyDataSetChanged()
                        showChoiceLayout()
                        currentQuestion = questionList[currentPosition]
                    }
                    DROPDOWN_QUESTION ->{
                        dropDownList.clear()
                        dropDownIdList.clear()
                        showDropDownLayout()
                        currentQuestion = questionList[currentPosition]
                    }
                    RADIO_BUTTON_QUESTION ->{
                        radioButtonList.clear()
                        radioButtonIdList.clear()
                        radioButtonAdapter.notifyDataSetChanged()
                        showRadioButtonLayout()
                        currentQuestion = questionList[currentPosition]
                    }
                    else -> {
                        showTextLayout()
                        currentQuestion = questionList[currentPosition]
                    }
                }
            } else {
                Log.d("TAG", "updateUIData: " + answerList.entries)
                moveToNextFragment()
            }
        }
    }

    private fun moveToNextFragment() {
        val bundle = Bundle()
        bundle.putParcelable(PRODUCT, addProduct)
        bundle.putSerializable("answersList", answerList)
        bundle.putSerializable("imagePath",imagePathList)
        bundle.putSerializable("questionAnswer",questionAnswerList)
        val fragment = SummaryFragment()
        fragment.arguments = bundle
        fragment.arguments
        (activity as SellerActivity).replaceFragment(fragment,R.id.container)
    }

    private fun setAdapterData() {
        checkboxAdapter = CheckBoxAdapter(checkboxList, this)
        recycler_view.setHasFixedSize(true)
        recycler_view.layoutManager = LinearLayoutManager(activity)
        recycler_view.adapter = checkboxAdapter

        radioButtonAdapter = RadioButtonAdapter(radioButtonList,this)
        radio_btn_recycler_view.setHasFixedSize(true)
        radio_btn_recycler_view.layoutManager = LinearLayoutManager(activity)
        radio_btn_recycler_view.adapter = radioButtonAdapter
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.next_btn -> {
                if (currentPosition <= questionList.size - 1) {
                    if (questionList[currentPosition].input_type?.type.equals(
                            CHECKBOX_QUESTION,
                            true
                        )
                    ) {
                        if (!selectedCheckBoxList.isNullOrEmpty()  && !itemTitleList.isNullOrEmpty()) {
                            questionAnswerList[currentQuestion.title] = itemTitleList.joinToString { "\'$it\'" }
                            answerList[currentQuestion.id] = selectedCheckBoxList.joinToString { "\'$it\'" }
                            selectedCheckBoxList.clear()
                            itemTitleList.clear()
                        } else {
                            answerList[currentQuestion.id] = selectedCheckBoxList.joinToString { "\'$it\'" }
                            questionAnswerList[currentQuestion.title] = itemTitleList.joinToString { "\'$it\'" }
                            selectedCheckBoxList.clear()
                            itemTitleList.clear()
                        }
                    }

                    if (questionList[currentPosition].input_type?.type.equals(TEXT_BOX_QUESTION, true)) {
                        answerList[currentQuestion.id] = textAnswer
                        questionAnswerList[currentQuestion.title] = textAnswer
                    }
                    if (questionList[currentPosition].input_type?.type.equals(RADIO_BUTTON_QUESTION, true)) {
                        answerList[currentQuestion.id] = radioButtonAnswer.toString()
                    }
                    if(questionList[currentPosition].input_type?.type.equals(DROPDOWN_QUESTION,true)) {
                        answerList[currentQuestion.id] = dropDownAnswer!!
                        questionAnswerList[currentQuestion.title] = dropDownAnswer!!
                    }
                    if (currentPosition != questionList.size - 1) {
                        currentPosition++
                        updateCount(currentPosition + 1, questionList.size)
                    } else {
                        moveToNextFragment()
                    }
                    updateUIData()
                }
            }
            R.id.true_button -> {
                true_button.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.selected_color))
                false_button.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.white))
                true_button.setTypeface(true_button.typeface,Typeface.BOLD)
                false_button.setTypeface(false_button.typeface,Typeface.NORMAL)
                answerList[currentQuestion.id] = "1"
                questionAnswerList[currentQuestion.title] = "1"
            }
            R.id.false_button -> {
                false_button.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.selected_color))
                true_button.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.white))
                true_button.setTypeface(true_button.typeface,Typeface.NORMAL)
                false_button.setTypeface(false_button.typeface,Typeface.BOLD)
                answerList[currentQuestion.id] = "0"
                questionAnswerList[currentQuestion.title] = "0"
            }
        }
    }

    private fun showDropDownLayout(){
        true_button.makeInvisible()
        false_button.makeInvisible()
        recycler_view.makeInvisible()
        edit_text.makeInvisible()
        radio_btn_recycler_view.makeInvisible()
        drop_down_spinner.makeVisible()
        dropDownList.add("Select")
        for(choices in questionList[currentPosition].choices!!){
            Log.d("TAG", "showDropDownLayout: " + choices.value)
            dropDownList.add(choices.value)
            dropDownIdList.add(choices.id)
        }
        drop_down_spinner.setItems(dropDownList)
        updateQuestionTitle()
    }

    private fun showRadioButtonLayout(){
        true_button.makeInvisible()
        false_button.makeInvisible()
        recycler_view.makeInvisible()
        edit_text.makeInvisible()
        radio_btn_recycler_view.makeVisible()
        drop_down_spinner.makeInvisible()
        for (choices in questionList[currentPosition].choices!!) {
            Log.d("TAG", "showRadioBtnLayout: " + choices.value)
            radioButtonList.add(choices.value)
            radioButtonIdList.add(choices.id)
        }
        radioButtonAdapter.notifyDataSetChanged()
        updateQuestionTitle()
    }

    private fun showTextLayout() {
        true_button.makeInvisible()
        false_button.makeInvisible()
        recycler_view.makeInvisible()
        edit_text.makeVisible()
        radio_btn_recycler_view.makeInvisible()
        drop_down_spinner.makeInvisible()
        updateQuestionTitle()
    }

    private fun showChoiceLayout() {
        true_button.makeInvisible()
        false_button.makeInvisible()
        recycler_view.makeVisible()
        edit_text.makeInvisible()
        radio_btn_recycler_view.makeInvisible()
        drop_down_spinner.makeInvisible()
        for (choices in questionList[currentPosition].choices!!) {
            Log.d("TAG", "showChoiceLayout: " + choices.value)
            checkboxList.add(choices.value)
            checkboxIdList.add(choices.id)
        }
        checkboxAdapter.notifyDataSetChanged()
        updateQuestionTitle()
    }

    private fun showBooleanLayout() {
        true_button.makeVisible()
        false_button.makeVisible()
        recycler_view.makeInvisible()
        edit_text.makeInvisible()
        radio_btn_recycler_view.makeInvisible()
        drop_down_spinner.makeInvisible()
        updateQuestionTitle()
    }

    private fun updateQuestionTitle() {
        question_title.text = questionList[currentPosition].title
    }

    override fun onItemCheck(position: Int) {
        selectedCheckBoxList.add(checkboxIdList[position])
        itemTitleList.add(checkboxList[position])
    }

    override fun onItemUnCheck(position: Int) {
        if (selectedCheckBoxList.contains(checkboxIdList[position])) {
            selectedCheckBoxList.remove(checkboxIdList[position])
        }
        if(itemTitleList.contains(checkboxList[position])){
            itemTitleList.remove(checkboxList[position])
        }
    }

    override fun itemClick(position: Int) {
        radioButtonAnswer = radioButtonIdList[position]
        questionAnswerList[currentQuestion.title] = radioButtonList[position]

    }

}