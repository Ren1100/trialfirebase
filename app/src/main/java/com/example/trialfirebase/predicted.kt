package com.example.trialfirebase

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup.LayoutParams
import android.widget.Button
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class predicted : AppCompatActivity() {

    private var containerDataList: Array<*>? = null
    private var currentContainerLocation: MutableList<String> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.predicted)

        containerDataList = intent.getSerializableExtra("containerDataList") as? Array<*>
        val tableLayout = findViewById<TableLayout>(R.id.tableLayout)

        val button1 = findViewById<Button>(R.id.button1)
        val button2 = findViewById<Button>(R.id.button2)
        val button3 = findViewById<Button>(R.id.button3)
        val button4 = findViewById<Button>(R.id.button4)
        val button5 = findViewById<Button>(R.id.button5)
        val button6 = findViewById<Button>(R.id.button6)
        val button7 = findViewById<Button>(R.id.button7)
        val button8 = findViewById<Button>(R.id.button8)
        val buttonact = findViewById<Button>(R.id.activate)

        button1.setOnClickListener {
            displayContainerData("1", tableLayout)
        }
        button2.setOnClickListener {
            displayContainerData("2", tableLayout)
        }
        button3.setOnClickListener {
            displayContainerData("3", tableLayout)
        }
        button4.setOnClickListener {
            displayContainerData("4", tableLayout)
        }
        button5.setOnClickListener {
            displayContainerData("5", tableLayout)
        }
        button6.setOnClickListener {
            displayContainerData("6", tableLayout)
        }
        button7.setOnClickListener {
            displayContainerData("7", tableLayout)
        }
        button8.setOnClickListener {
            displayContainerData("8", tableLayout)
        }
        buttonact.setOnClickListener{
            val intent = Intent(this, RobotArm::class.java)
            startActivity(intent)
        }
    }

    private fun displayContainerData(containerLocation: String, tableLayout: TableLayout) {

        if (currentContainerLocation.contains(containerLocation)) {
            return  // Skip if the same location is already displayed
        }

        currentContainerLocation.add(containerLocation)
        containerDataList?.forEach { containerData ->
            if (containerData is Map<*, *>) {
                val location = containerData["location"].toString()
                if(location == containerLocation) {
                    val id = containerData["id"].toString()
                    val dest = containerData["destinations"].toString()
                    val type = containerData["cargo_type"].toString()
                    val weight = containerData["cargo_weight"].toString()


                    val row = TableRow(this)

                    val idTextView = createTextView(id)
                    row.addView(idTextView)

                    val destTextView = createTextView(dest)

                    row.addView(destTextView)

                    val typeTextView = createTextView(type)

                    row.addView(typeTextView)

                    val weightTextView = createTextView(weight)
                    row.addView(weightTextView)

                    val locationTextView = createTextView(location)
                    row.addView(locationTextView)

                    tableLayout.addView(row, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
                }
            }
        }
    }
    private fun createTextView(text: String): TextView {
        val textView = TextView(this)
        textView.text = text
        textView.gravity = Gravity.CENTER
        val layoutParams = TableRow.LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f)
        textView.layoutParams = layoutParams
        return textView
    }
}
