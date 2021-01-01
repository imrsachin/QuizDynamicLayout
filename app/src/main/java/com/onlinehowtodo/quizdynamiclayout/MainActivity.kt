package com.onlinehowtodo.quizdynamiclayout

import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.widget.*
import java.util.*

const val margin: Int = 16

//extension property to convert dp into pixel
val Int.pixel: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

class MainActivity : AppCompatActivity() {
    private var questions: MutableList<Question> = mutableListOf()
    lateinit var quiz_container:LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    quiz_container = findViewById(R.id.linear_layout)
        setupQuestions()
        setupQuiz()
        setupSubmitButton()
    }

    private fun setupQuestions(){

        questions.add(
            Question(1, QuestionType.Text, "What is the capital of Nagaland?",
            null, listOf("kohima"))
        )

        questions.add(
            Question(2, QuestionType.Radio, "Which is the largest(area) state of India?",
            listOf("Bihar", "Madhya Pradesh", "Uttar Pradesh", "Rajasthan"), listOf("Rajasthan"))
        )

        questions.add(
            Question(3, QuestionType.CheckBox, "Which of these are state capitals?",
            listOf("Guwahati", "Chennai", "Varanasi", "Dispur"), listOf("Chennai", "Dispur"))
        )



    }

    private fun setupQuiz(){
        questions.forEachIndexed{index, element ->
            when(element.type){
                QuestionType.Text ->{
                    setupTextQuestion(index, element)
                }
                QuestionType.Radio ->{
                    setupRadioQuestion(index, element)
                }
                QuestionType.CheckBox ->{
                    setupCheckBoxQuestion(index, element)
                }
            }
        }
    }

    private fun setupTextQuestion(counter: Int, q: Question){
        val textView = getQuestionTextView(counter, q.qText)

        val editText = EditText(this)
        editText.id = q.id
        editText.setSingleLine(true)
        editText.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        quiz_container.addView(textView)
        quiz_container.addView(editText)

    }

    private fun setupRadioQuestion(counter: Int, q: Question){

        val textView = getQuestionTextView(counter, q.qText)

        val radioGroup = RadioGroup(this)
        radioGroup.id = q.id
        radioGroup.orientation = RadioGroup.VERTICAL

        radioGroup.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        q.options?.forEachIndexed{ index, element ->
            val radioButton = RadioButton(this)
            radioButton.text = element
            radioButton.id = (q.id.toString() + index.toString()).toInt()
            radioGroup.addView(radioButton)
        }

        quiz_container.addView(textView)
        quiz_container.addView(radioGroup)
    }


    private fun setupCheckBoxQuestion(counter: Int, q: Question){

        val textView = getQuestionTextView(counter, q.qText)
        quiz_container.addView(textView)

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        q.options?.forEachIndexed{ index, element ->
            val checkBox = CheckBox(this)
            checkBox.text = element
            checkBox.id = (q.id.toString() + index.toString()).toInt()
            checkBox.layoutParams = params
            quiz_container.addView(checkBox)
        }
    }

    private fun getQuestionTextView(counter: Int, question: String): TextView {
        val textView = TextView(this)
        textView.text = getString(R.string.question, counter, question)

        textView.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply { topMargin = margin.pixel }

        return textView
    }


    private fun setupSubmitButton(){
        val params  = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.topMargin = margin.pixel
        params.gravity = Gravity.CENTER_HORIZONTAL

        val button = Button(this)
        button.layoutParams = params
        button.text = getString(R.string.submit)
        button.setOnClickListener{
            evaluateQuiz()
        }

        quiz_container.addView(button)
    }

    private fun evaluateQuiz() {
        var score = 0

        questions.forEach{ q ->
            when(q.type){
                QuestionType.Text ->{
                    val editText = quiz_container.findViewById<EditText>(q.id)

                    editText?.let{
                        val userAnswer = it.text.toString().toLowerCase(Locale.getDefault())
                        if(userAnswer == q.answers[0]){
                            score++
                        }
                    }
                }
                QuestionType.Radio -> {
                    val radioGroup = quiz_container.findViewById<RadioGroup>(q.id)

                    radioGroup?.let{
                        val checkedId = it.checkedRadioButtonId
                        if(checkedId > 0){
                            val radioButton = quiz_container.findViewById<RadioButton>(checkedId)
                            val userAnswer = radioButton.text
                            if(userAnswer == q.answers[0]){
                                score++
                            }
                        }
                    }
                }
                QuestionType.CheckBox -> {
                    var correct = true

                    q.options?.forEachIndexed{index, element ->
                        val checkedId = (q.id.toString() + index.toString()).toInt()
                        val checkBox =  quiz_container.findViewById<CheckBox>(checkedId)
                        if(q.answers.contains(checkBox.text)){
                            if(!checkBox.isChecked){
                                correct = false
                            }
                        } else{
                            if(checkBox.isChecked){
                                correct = false
                            }
                        }
                    }
                    if(correct) score++
                }
            }
        }
        Toast.makeText(this, getString(R.string.score_result, score, questions.size), Toast.LENGTH_SHORT).show()
    }
}
