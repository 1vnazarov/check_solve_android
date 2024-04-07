package com.example.check_solve.nazarovia

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.check_solve.nazarovia.databinding.ActivityMainBinding
import android.os.SystemClock
import java.math.RoundingMode
import java.text.DecimalFormat
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var correctAnswers = 0
    private var incorrectAnswers = 0
    private var totalAnswers = 0
    private var startTime: Long = 0
    private var endTime: Long = 0
    private var maxTime: Long = 0
    private var minTime: Long = Long.MAX_VALUE
    private var avgTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.correctButton.isEnabled = false
        binding.incorrectButton.isEnabled = false
        binding.startBtn.setOnClickListener {
            generateExample()
            binding.startBtn.isEnabled = false
            binding.correctButton.isEnabled = true
            binding.incorrectButton.isEnabled = true
            startTime = SystemClock.elapsedRealtime()
        }

        binding.correctButton.setOnClickListener {
            checkAnswer(true)
        }

        binding.incorrectButton.setOnClickListener {
            checkAnswer(false)
        }

        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState)
        }
        updateUI()
    }

    private fun getFloatTwoDigits(number: Double): String {
        val format = DecimalFormat("#.##")
        format.roundingMode = RoundingMode.FLOOR
        return format.format(number)
    }

    private fun calculate(): Double {
        val num1 = binding.firstOperand.text.toString().toDouble()
        val num2 = binding.secondOperand.text.toString().toDouble()
        val operator = binding.operator.text.toString()
        var result = 0.0
        when (operator) {
            "*" -> result = num1 * num2
            "/" -> result = getFloatTwoDigits(num1 / num2).toDouble()
            "-" -> result = num1 - num2
            "+" -> result = num1 + num2
        }
        return result
    }

    private fun generateExample() {
        binding.firstOperand.text = (10..99).random().toString()
        binding.secondOperand.text = (10..99).random().toString()
        when ((0..3).random()) {
            0 -> binding.operator.text = "*"
            1 -> binding.operator.text = "/"
            2 -> binding.operator.text = "-"
            3 -> binding.operator.text = "+"
        }
        val result = calculate()
        if ((0..1).random() == 0) {
            binding.answer.text = result.toString()
        } else {
            var answer: String
            do {
                answer = getFloatTwoDigits(Random.nextDouble(-999.0, 999.0))
            } while (answer.toDouble() == result)
            binding.answer.text = answer
        }
    }

    private fun checkAnswer(answer: Boolean) {
        totalAnswers++
        endTime = SystemClock.elapsedRealtime()
        val time = endTime - startTime
        avgTime = (avgTime * totalAnswers + time) / totalAnswers
        maxTime = max(time, maxTime)
        minTime = min(time, minTime)
        if (answer == (calculate() == (binding.answer.text.toString().toDoubleOrNull() ?: 0))) {
            correctAnswers++
        } else {
            incorrectAnswers++
        }

        binding.startBtn.isEnabled = true
        binding.correctButton.isEnabled = false
        binding.incorrectButton.isEnabled = false
        updateUI()
    }

    private fun updateUI() {
        binding.countRightText.text = correctAnswers.toString()
        binding.countErrorText.text = incorrectAnswers.toString()
        binding.countSolveText.text = totalAnswers.toString()
        binding.percentText.text = "%.2f%%".format(correctAnswers.toDouble() / (if (totalAnswers > 0) totalAnswers else 1) * 100)
        binding.maxTimeText.text = (maxTime / 1000).toString()
        binding.minTimeText.text = ((if (minTime == Long.MAX_VALUE) 0 else minTime) / 1000).toString()
        binding.avgTimeText.text = (avgTime / 1000).toString()
    }

    override fun onSaveInstanceState(instanceState: Bundle) {
        super.onSaveInstanceState(instanceState)
        instanceState.putInt("correctAnswers", correctAnswers)
        instanceState.putInt("incorrectAnswers", incorrectAnswers)
        instanceState.putInt("totalAnswers", totalAnswers)
        instanceState.putLong("maxTime", maxTime)
        instanceState.putLong("minTime", minTime)
        instanceState.putLong("avgTime", avgTime)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        correctAnswers = savedInstanceState.getInt("correctAnswers")
        incorrectAnswers = savedInstanceState.getInt("incorrectAnswers")
        totalAnswers = savedInstanceState.getInt("totalAnswers")
        maxTime = savedInstanceState.getLong("maxTime")
        minTime = savedInstanceState.getLong("minTime")
        avgTime = savedInstanceState.getLong("avgTime")
    }
}