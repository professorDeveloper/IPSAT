package com.ip_tv.ipsat.presentation.activities

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.ip_tv.ipsat.R
import com.ip_tv.ipsat.databinding.ActivityMainBinding
import com.ip_tv.ipsat.domain.preference.UserPreferenceManager
import com.ip_tv.ipsat.utils.hideWithAnimation
import com.ip_tv.ipsat.utils.hideWithoutAnimation
import com.ip_tv.ipsat.utils.initActivity
import com.ip_tv.ipsat.utils.showWithAnimation
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityMainBinding

    @Inject
    lateinit var userPreferenceManager: UserPreferenceManager

    private val navController: NavController by lazy {
        (supportFragmentManager.findFragmentById(R.id.navHost) as NavHostFragment).navController
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        initActivity(this)
        setContentView(viewBinding.root)
        viewBinding.homeNavigation.setupWithNavController(navController)
        managePages()
    }

  private  fun managePages(){
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.showMoreMoviesScreen -> {
                    viewBinding.homeNavigation.hideWithAnimation(viewBinding.navHost)
                }
                R.id.updateInfoScreen -> {
                    viewBinding.homeNavigation.hideWithAnimation(viewBinding.navHost)
                }
                R.id.searchScreen -> {
                    viewBinding.homeNavigation.hideWithAnimation(viewBinding.navHost)
                }
                R.id.button_profile_page -> {
                    viewBinding.homeNavigation.showWithAnimation(viewBinding.navHost)
                }

                else -> {
                    viewBinding.homeNavigation.showWithAnimation(viewBinding.navHost)
                }
            }
        }

    }



}