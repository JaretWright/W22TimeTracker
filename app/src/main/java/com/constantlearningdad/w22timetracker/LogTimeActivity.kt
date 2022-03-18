package com.constantlearningdad.w22timetracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.constantlearningdad.w22timetracker.databinding.ActivityLogTimeBinding
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore

class LogTimeActivity : AppCompatActivity() {
    private lateinit var binding : ActivityLogTimeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogTimeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //get the course information and update the header
        val projectID = intent.getStringExtra("projectID")
        val db = FirebaseFirestore.getInstance().collection("projects")
        var project = Project()

        db.whereEqualTo("id", projectID)
            .get()
            .addOnSuccessListener { querySnapShot ->
                for (document in querySnapShot) {
                    project = document.toObject(Project::class.java)
                    binding.projectTextView.text = project.projectName
                }
            }

        //variables to store the category, start & stop times
        var startTime: Timestamp? = null
        var finishTime: Timestamp? = null
        var category: String? = null

        //if the start time hasn't been clicked already, set the start time
        binding.startButton.setOnClickListener {
            if (binding.startTextView.text.toString().isNullOrBlank()) {
                startTime = Timestamp.now()
                binding.startTextView.text = startTime!!.toDate().toString()
            }
        }

        //if a category is selected and the start time has been pushed, establish the end time
        //and store in the DB
        binding.finishButton.setOnClickListener {
            if (startTime != null && binding.spinner.selectedItemPosition > 0) {
                category = binding.spinner.selectedItem.toString()
                finishTime = Timestamp.now()
                binding.finishTextView.text = finishTime!!.toDate().toString()

                //create a TimeRecord object and display the duration
                val timeRecord = TimeRecord(category, startTime, finishTime)
                binding.totalTimeTextView.text =
                    String.format("Total Time: %d minutes", timeRecord.getDuration())

                //update the project with the new time record and save it to the database
                project.addTimeRecord(timeRecord)
                project?.let {
                    db.document(project.id!!).set(project)
                        .addOnSuccessListener {
                            Toast.makeText(
                                this,
                                "DB updated",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(
                                this,
                                "DB write failed",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                }
            } else {
                Toast.makeText(this, "Start Time or Category not selected", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }
}
