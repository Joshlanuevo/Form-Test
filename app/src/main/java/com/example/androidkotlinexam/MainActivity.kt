package com.example.androidkotlinexam

import android.os.Bundle
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        val fullNameEditText = findViewById<EditText>(R.id.fullNameEditText)
        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val mobileEditText = findViewById<EditText>(R.id.mobileEditText)
        val datePickerDOB = findViewById<DatePicker>(R.id.datePickerDOB)
        val spinnerGender = findViewById<Spinner>(R.id.spinnerGender)
        val submitButton = findViewById<Button>(R.id.submitButton)

        datePickerDOB.setOnDateChangedListener { _, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)
            val birthYear = calendar.get(Calendar.YEAR)

            val currentYear = Calendar.getInstance().get(Calendar.YEAR)
            val age = currentYear - birthYear
            val ageTextView = findViewById<TextView>(R.id.ageTextView)
            ageTextView.text = "Age: $age"
        }

        submitButton.setOnClickListener {
            val fullName = fullNameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val mobile = mobileEditText.text.toString().trim()

            if (isValidFullName(fullName) && isValidEmail(email) && isValidPhMobile(mobile) && isValidAge(datePickerDOB)) {
                submitForm(fullName, email, mobile, datePickerDOB, spinnerGender.selectedItem.toString())
            } else {
                val errorMessage = buildErrorMessage(isValidFullName(fullName), isValidEmail(email), isValidPhMobile(mobile), isValidAge(datePickerDOB))
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.responseMessage.observe(this, Observer { message ->
            showDialog(message)
        })
    }

    private fun submitForm(
        fullName: String,
        email: String,
        mobile: String,
        datePicker: DatePicker,
        gender: String
    ) {
        val calendar = Calendar.getInstance()
        calendar.set(datePicker.year, datePicker.month, datePicker.dayOfMonth)
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dob = formatter.format(calendar.time)

        val formData = FormData(fullName, email, mobile, dob, gender)
        viewModel.submitFormData(formData)
    }

    private fun showDialog(message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("API Response")
        builder.setMessage(message)
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    // For validations
    private fun isValidFullName(fullName: String): Boolean {
        val regex = Regex("^[a-zA-Z .,]+$")
        return regex.matches(fullName)
    }

    private fun isValidEmail(email: String): Boolean {
        return !email.isEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isValidPhMobile(mobile: String): Boolean {
        val regex = Regex("^09[0-9]{9}$")
        return regex.matches(mobile)
    }

    private fun isValidAge(datePicker: DatePicker): Boolean {
        val calendar = Calendar.getInstance()
        calendar.set(datePicker.year, datePicker.month, datePicker.dayOfMonth)
        val birthYear = calendar.get(Calendar.YEAR)

        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val age = currentYear - birthYear
        return age >= 18
    }

    private fun buildErrorMessage(
        isValidFullName: Boolean,
        isValidEmail: Boolean,
        isValidPhMobile: Boolean,
        isValidAge: Boolean
    ): String {
        val message = StringBuilder("Please fix the following:\n")
        if (!isValidFullName) {
            message.append("- Full name can only contain letters and spaces.\n")
        }
        if (!isValidEmail) {
            message.append("- Invalid email format.\n")
        }
        if (!isValidPhMobile) {
            message.append("- Invalid mobile number format. Must be PH mobile number (e.g., 09171234567).\n")
        }
        if (!isValidAge) {
            message.append("- Must be 18 years or older.\n")
        }
        return message.toString()
    }
}
