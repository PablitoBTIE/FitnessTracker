import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnesstracker.R
import com.google.gson.Gson
import java.io.File
import java.io.FileOutputStream
import java.io.FileInputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

data class Training(val distance: Float, val time: Int, val calories: Int, val intensity: Int, val activityType: String)

class MainActivity : AppCompatActivity() {

    private lateinit var trainingList: MutableList<Training>
    private lateinit var recyclerView: RecyclerView
    private lateinit var trainingAdapter: TrainingAdapter
    private lateinit var distanceInput: EditText
    private lateinit var timeInput: EditText
    private lateinit var caloriesInput: EditText
    private lateinit var intensitySeekBar: SeekBar
    private lateinit var activityRadioGroup: RadioGroup
    private lateinit var addButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        distanceInput = findViewById(R.id.distanceInput)
        timeInput = findViewById(R.id.timeInput)
        caloriesInput = findViewById(R.id.caloriesInput)
        intensitySeekBar = findViewById(R.id.intensitySeekBar)
        activityRadioGroup = findViewById(R.id.activityRadioGroup)
        addButton = findViewById(R.id.addButton)
        recyclerView = findViewById(R.id.trainingRecyclerView)

        trainingList = loadTrainingData()

        trainingAdapter = TrainingAdapter(trainingList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = trainingAdapter

        addButton.setOnClickListener {
            addTraining()
        }
    }

    private fun addTraining() {
        val distance = distanceInput.text.toString().toFloatOrNull()
        val time = timeInput.text.toString().toIntOrNull()
        val calories = caloriesInput.text.toString().toIntOrNull()
        val intensity = intensitySeekBar.progress
        val activityType = when (activityRadioGroup.checkedRadioButtonId) {
            R.id.walkRadio -> "Spacer"
            R.id.runRadio -> "Bieg"
            R.id.strengthRadio -> "Trening siłowy"
            else -> return
        }

        if (distance != null && time != null && calories != null) {
            val training = Training(distance, time, calories, intensity, activityType)
            trainingList.add(training)
            trainingAdapter.notifyDataSetChanged()
            saveTrainingData()
            clearInputs()
        } else {
            Toast.makeText(this, "Proszę uzupełnić wszystkie pola.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveTrainingData() {
        val gson = Gson()
        val json = gson.toJson(trainingList)
        val file = File(filesDir, "trainings.json")
        FileOutputStream(file).use {
            it.write(json.toByteArray())
        }
    }

    private fun loadTrainingData(): MutableList<Training> {
        val file = File(filesDir, "trainings.json")
        return if (file.exists()) {
            val json = file.readText()
            val gson = Gson()
            gson.fromJson(json, Array<Training>::class.java).toMutableList()
        } else {
            mutableListOf()
        }
    }

    private fun clearInputs() {
        distanceInput.text.clear()
        timeInput.text.clear()
        caloriesInput.text.clear()
        intensitySeekBar.progress = 0
        activityRadioGroup.clearCheck()
    }
}
