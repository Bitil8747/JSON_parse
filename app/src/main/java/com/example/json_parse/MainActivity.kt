package com.example.json_parse

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONArray
import java.io.IOException
import java.io.InputStream


val FILE_NAME: String = "Test.json"                                         //наименование json файла
var testName: ArrayList<String> = arrayListOf()                             //массив с наименованиями тестов
var questionText: ArrayList<ArrayList<String>> = arrayListOf()              //двумерный массив с вопросами для каждого теста
var questionId: ArrayList<ArrayList<String>> = arrayListOf()                //двумерный массив с номерами вопросов для каждого теста
var answersText: ArrayList<ArrayList<ArrayList<String>>> = arrayListOf()    //трехмерный массив с ответами для каждого вопроса и для каждого теста
val questions: ArrayList<Questions> = arrayListOf()                         //массив типа Questions с вопросами

//класс вопросов c ответами
public class Questions(val questionMessage: String, val questionNumber: String, val answer1: String, val answer2: String){
    public fun getQuestionText(): String{
        return questionMessage
    }
    public fun getQuestionId(): String{
        return questionNumber
    }
    public fun getFirstAnswer(): String{
        return answer1
    }
    public fun getSecondAnswer(): String{
        return answer2
    }
}

//адаптер для RecycleView с вопросами и ответами
internal class DataAdapter(context: Context?, private val questions: List<Questions>) : RecyclerView.Adapter<DataAdapter.ViewHolder>() {
    private var inflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = inflater.inflate(R.layout.list_items, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val question = questions[position]
        holder.questionsView.setText(question.getQuestionText())
        holder.questionsIdView.setText(question.getQuestionId())
        holder.select1.setText(question.getFirstAnswer())
        holder.select2.setText(question.getSecondAnswer())
    }

    override fun getItemCount(): Int {
        return questions.size
    }

    inner class ViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        val questionsView: TextView
        val questionsIdView: TextView
        val select1: RadioButton
        val select2: RadioButton
        val radGrp: RadioGroup

        init {
            questionsView = view.findViewById<View>(R.id.question) as TextView
            questionsIdView = view.findViewById<View>(R.id.num_of_question) as TextView
            select1 = view.findViewById(R.id.select1) as RadioButton
            select2 = view.findViewById(R.id.select2) as RadioButton
            radGrp = view.findViewById(R.id.RadioGroup) as RadioGroup
        }
    }

    init {
        inflater = LayoutInflater.from(context)
    }
}

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        readJson()

        val recyclerView: RecyclerView = findViewById(R.id.list) as RecyclerView
        val adapter: DataAdapter = DataAdapter(this, questions)
        recyclerView.setAdapter(adapter)
    }


    private fun setInitialData(message: String, id: String, ans1: String, ans2: String) {
        questions.add(Questions(message, id, ans1, ans2))
    }

    fun readJson() {
        var jsonFile: String? = null
        if (jsonFile != null) {
            val jsomArray: JSONArray = JSONArray(jsonFile)
        }
        try {
            val inputStream: InputStream = assets.open(FILE_NAME) //инициализируем входной поток
            jsonFile = inputStream.bufferedReader().use { it.readText() } //читаем файл в переменную
            val jsonArray: JSONArray = JSONArray(jsonFile) //инициализируем внешний Json массив
            var tmpForQuestions = 0 //сохраняет количество вопросов
            // пробегаем по каждому Json объекту в Json массиве
            for (i in 0..jsonArray.length() - 1) {
                // инициализируем каждый Json объект в Json массиве
                val jsonObject = jsonArray.getJSONObject(i)
                // заносим в массив "testName" значения с ключом "name" для каждого Json объекта
                testName.add(jsonObject.getString("name"))
                // пробегаем по каждому Json объекту в Json массиве и инициализируем каждый вложенный Json массив с ключом "questions" во внешнем Json массиве
                val jsonArrayOfQuestions = JSONArray(jsonObject.getString("questions"))
                // пробегаем по каждому Json объекту в Json массиве c ключем "questions"
                for (j in 0..jsonArrayOfQuestions.length() - 1) {
                    tmpForQuestions = jsonArrayOfQuestions.length() - 1
                    // инициализируем каждый Json объект в Json массиве c ключем "questions"
                    val jsonObjectOfQuestions = jsonArrayOfQuestions.getJSONObject(j)
                    // заносим в двумерный массив "questionText" значения с ключом "text" для каждого Json объекта
                    questionText.add(ArrayList<String>())
                    questionText.get(i).add(jsonObjectOfQuestions.getString("text"))
                    // заносим в двумерный массив "questionId" значения с ключом "questionId" для каждого Json объекта
                    questionId.add(ArrayList<String>())
                    questionId.get(i).add((jsonObjectOfQuestions.getInt("questionId") + 1).toString() + ". ")
                    // пробегаем по каждому Json объекту в Json массиве и инициализируем каждый вложенный Json массив с ключом "answers" во внешнем Json массиве
                    val jsonArrayOfAnswers = JSONArray(jsonObjectOfQuestions.getString("answers"))
                    // пробегаем по каждому Json объекту в Json массиве c ключем "answers"
                    for (k in 0..jsonArrayOfAnswers.length() - 1) {
                        // инициализируем каждый Json объект в Json массиве c ключем "answers"
                        val jsonObjectOfAnswers = jsonArrayOfAnswers.getJSONObject(k)
                        // заносим в трехмерный массив "answersText" значения с ключом "text" для каждого Json объекта
                        answersText.add(ArrayList())
                        answersText.get(i).add(ArrayList<String>())
                        answersText.get(i).get(j).add(jsonObjectOfAnswers.getString("text"))
                    }
                }
            }

            val spinner: Spinner = findViewById(R.id.spinner) as Spinner //выпадающий список для выбора теста
            val spinnerAdapter: ArrayAdapter<String> = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, testName)
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = spinnerAdapter
            val testNum: Int = 0 // показывает номер теста
            val itemSelectedListener: OnItemSelectedListener = object : OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    val testNum = position // передаем номер выбранного теста
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
            spinner.onItemSelectedListener = itemSelectedListener

            for (p in 0..tmpForQuestions) {
                setInitialData(questionText[testNum][p], questionId[testNum][p], answersText[testNum][p][0], answersText[testNum][p][1])
            }
        } catch (e: IOException) {

        }
    }
}
