package com.haneet.assignment.ui.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

import com.haneet.assignment.R
import com.haneet.assignment.base.BaseClass
import com.haneet.assignment.databinding.ActivityDateLoginBinding
import com.haneet.assignment.ui.MainActivity
import com.haneet.assignment.ui.MainActivityViewModel
import kotlinx.coroutines.InternalCoroutinesApi

class LoginActivity : BaseClass() {


    @InternalCoroutinesApi
    private val viewModel by viewModels<LoginActivityViewModel>()
    lateinit var binding: ActivityDateLoginBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun setBinding() {
        binding =
            DataBindingUtil.setContentView(this, R.layout.activity_date_login)
        binding.counter = 60.00

        binding.handler = ClickEvents()
        setEvents();
    }

    private fun setEvents() {

    }

    @InternalCoroutinesApi
    override fun attachViewModel() {
        viewModel.reset.set(true)
        viewModel.loginFlow.observe(this, { parseLogin(it) })
        viewModel.otpGenerated.observe(this, { binding.isForOTP = it })
    }


    @InternalCoroutinesApi
    private fun parseLogin(it: Boolean?) {
        if (it == true) {
            var intent =
                Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
        }

    }


    inner class ClickEvents() {
        @InternalCoroutinesApi
        fun clickContinue(code: String, number: String, otp: String) {

            if (code.isBlank() || number.isBlank()) {
                binding.errorMessage = "Number is blank";
                binding.showErrorMessage = true;
                return
            }
            if (number.length < 10) {
                binding.errorMessage = "Number should be greater than 10";
                binding.showErrorMessage = true;
                return
            }
            if (binding.isForOTP && (otp.isBlank() || otp.length < 4)) {
                binding.errorMessage = "Please check OTP";
                binding.showErrorMessage = true;
                return;
            }
            binding.showErrorMessage = false;

            if (viewModel.reset.get()) {
                if (!binding.isForOTP) {
                    // binding.counter = 60.00
                    //viewModel.startTimer()
                    viewModel.generateOTP(code + number)
                } else {
                    // binding.counter = 60.00
                    //viewModel.startTimer()
                    viewModel.verifyUser(code + number, otp)
                }
            }

        }

        fun editAgain() {
            binding.isForOTP = false;
        }
    }


}


