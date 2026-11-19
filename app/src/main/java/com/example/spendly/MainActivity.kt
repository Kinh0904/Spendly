package com.example.spendly

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.spendly.data.auth.AuthRepository
import com.example.spendly.presentation.auth.login.LoginFragment
import com.example.spendly.presentation.auth.register.RegisterFragment
import com.example.spendly.presentation.home.HomeFragment
import com.example.spendly.utils.ThemeManager

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ThemeManager.initTheme(this)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Chưa đăng nhập thì vào màn hình đăng nhập, đã đăng nhập (và chọn "Lưu đăng nhập") thì vào thẳng Trang chủ.
        val loggedIn = AuthRepository.getInstance(this).currentUser() != null
        if (savedInstanceState == null) {
            navigateToFragment(if (loggedIn) HomeFragment() else LoginFragment())
        } else if (!loggedIn && !isAuthScreenShowing()) {
            // Tiến trình bị hệ thống thu hồi: phiên không lưu sẽ mất, không để lộ màn hình bên trong.
            navigateToFragment(LoginFragment())
        }
    }

    private fun isAuthScreenShowing(): Boolean {
        val current = supportFragmentManager.findFragmentById(R.id.fragment_container)
        return current is LoginFragment || current is RegisterFragment
    }

    fun navigateToFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                android.R.anim.fade_in,
                android.R.anim.fade_out,
            )
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}