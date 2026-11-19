package com.example.spendly.presentation.auth.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.activity.OnBackPressedCallback
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import com.example.spendly.MainActivity
import com.example.spendly.data.auth.AuthError
import com.example.spendly.data.auth.AuthRepository
import com.example.spendly.data.auth.AuthResult
import com.example.spendly.databinding.FragmentRegisterBinding
import com.example.spendly.domain.auth.AuthValidator
import com.example.spendly.presentation.auth.login.LoginFragment
import com.example.spendly.presentation.auth.messageRes
import com.google.android.material.textfield.TextInputLayout

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private lateinit var authRepository: AuthRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        authRepository = AuthRepository.getInstance(requireContext())

        // Back hệ thống và nút ← đều quay về màn hình đăng nhập.
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() = goToLogin()
            },
        )
        binding.btnBack.setOnClickListener { goToLogin() }
        binding.tvGoLogin.setOnClickListener { goToLogin() }

        // Xóa lỗi của từng ô ngay khi người dùng sửa lại.
        clearErrorOnEdit(binding.tilFullName, binding.etFullName)
        clearErrorOnEdit(binding.tilEmail, binding.etEmail)
        clearErrorOnEdit(binding.tilPhone, binding.etPhone)
        clearErrorOnEdit(binding.tilPassword, binding.etPassword)
        clearErrorOnEdit(binding.tilConfirmPassword, binding.etConfirmPassword)

        binding.etConfirmPassword.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                attemptRegister()
                true
            } else {
                false
            }
        }
        binding.btnRegister.setOnClickListener { attemptRegister() }
    }

    private fun clearErrorOnEdit(layout: TextInputLayout, field: android.widget.EditText) {
        field.doAfterTextChanged {
            layout.error = null
            _binding?.tvFormError?.visibility = View.GONE
        }
    }

    private fun attemptRegister() {
        val fullName = binding.etFullName.text?.toString().orEmpty()
        val email = binding.etEmail.text?.toString().orEmpty()
        val phone = binding.etPhone.text?.toString().orEmpty()
        val password = binding.etPassword.text?.toString().orEmpty()
        val confirm = binding.etConfirmPassword.text?.toString().orEmpty()

        binding.tvFormError.visibility = View.GONE

        // Kiểm tra từng ô theo thứ tự trên màn hình, đưa con trỏ về ô lỗi đầu tiên.
        val checks = listOf(
            Triple(binding.tilFullName, binding.etFullName, AuthValidator.validateName(fullName)),
            Triple(binding.tilEmail, binding.etEmail, AuthValidator.validateEmail(email)),
            Triple(binding.tilPhone, binding.etPhone, AuthValidator.validateOptionalPhone(phone)),
            Triple(binding.tilPassword, binding.etPassword, AuthValidator.validateNewPassword(password)),
            Triple(
                binding.tilConfirmPassword,
                binding.etConfirmPassword,
                AuthValidator.validateConfirmPassword(password, confirm),
            ),
        )
        checks.forEach { (layout, _, error) ->
            layout.error = error?.let { getString(it.messageRes()) }
        }
        checks.firstOrNull { it.third != null }?.let { (_, field, _) ->
            field.requestFocus()
            return
        }

        when (val result = authRepository.register(fullName, email, phone, password)) {
            is AuthResult.Success -> {
                hideKeyboard()
                (activity as? MainActivity)?.navigateToFragment(
                    LoginFragment.afterRegistration(result.user.email),
                )
            }
            is AuthResult.Failure -> showRegistrationFailure(result.error)
        }
    }

    private fun showRegistrationFailure(error: AuthError) {
        val message = getString(error.messageRes())
        when (error) {
            AuthError.EMAIL_TAKEN -> {
                binding.tilEmail.error = message
                binding.etEmail.requestFocus()
            }
            AuthError.PHONE_TAKEN -> {
                binding.tilPhone.error = message
                binding.etPhone.requestFocus()
            }
            else -> {
                binding.tvFormError.text = message
                binding.tvFormError.visibility = View.VISIBLE
            }
        }
    }

    private fun goToLogin() {
        hideKeyboard()
        (activity as? MainActivity)?.navigateToFragment(LoginFragment())
    }

    private fun hideKeyboard() {
        val window = activity?.window ?: return
        WindowCompat.getInsetsController(window, binding.root).hide(WindowInsetsCompat.Type.ime())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
