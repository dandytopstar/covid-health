package au.gov.health.covidsafe.ui.onboarding

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.LinearLayout
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.fragment.NavHostFragment
import au.gov.health.covidsafe.R
import au.gov.health.covidsafe.preference.Preference
import au.gov.health.covidsafe.ui.base.HasBlockingState
import au.gov.health.covidsafe.ui.base.PagerContainer
import au.gov.health.covidsafe.ui.base.UploadButtonLayout
import com.github.razir.progressbutton.bindProgressButton
import com.github.razir.progressbutton.hideProgress
import com.github.razir.progressbutton.showProgress
import kotlinx.android.synthetic.main.activity_onboarding.*


class OnboardingActivity : FragmentActivity(), HasBlockingState, PagerContainer {

    override var isUiBlocked: Boolean = false
        set(value) {
            loadingProgressBarFrame?.isVisible = value
            field = value
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)
        bindProgressButton(onboarding_next)
        if (isUiBlocked) {
            loadingProgressBarFrame?.isVisible = true
        }

        val myNavHostFragment: NavHostFragment = fragment_nav_host as NavHostFragment
        val inflater = myNavHostFragment.navController.navInflater
        if (Preference.isReRegister(this)) {
            val graph = inflater.inflate(R.navigation.nav_re_register)
            myNavHostFragment.navController.graph = graph
        } else {
            val graph = inflater.inflate(R.navigation.nav_onboarding)
            myNavHostFragment.navController.graph = graph
        }

        non_consent.setOnClickListener {

            val builder = AlertDialog.Builder(this)
            builder.setMessage(R.string.non_consent_popup)
            builder.setNegativeButton(R.string.global_OK, null)

            val dialog = builder.create()
            dialog.show()

            val negativeButton: Button = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            val parent = negativeButton.parent as LinearLayout
            parent.gravity = Gravity.CENTER_HORIZONTAL
            val leftSpacer: View = parent.getChildAt(1)
            leftSpacer.visibility = GONE
        }
    }

    override fun onAttachFragment(fragment: Fragment) {
        super.onAttachFragment(fragment)
        isUiBlocked = false
    }

    override fun onResume() {
        super.onResume()

        toolbar.setNavigationOnClickListener {
            super.onBackPressed()
        }
    }

    override fun onBackPressed() {
        if (Preference.isOnBoarded(this)) {
            finish()
        } else {
            super.onBackPressed()
        }
    }

    override fun updateSteps(step: Int?, totalSteps: Int) {
        when (step) {
            null -> {
                textViewSteps.visibility = GONE
            }
            else -> {
                textViewSteps.visibility = VISIBLE
                textViewSteps.text = String.format(getString(R.string.stepCounter), step, totalSteps)
            }
        }
    }

    override fun setNavigationIcon(navigationIcon: Int?) {
        if (navigationIcon == null) {
            toolbar.navigationIcon = null
            toolbar.visibility = GONE
            toolbarReplacer.visibility = VISIBLE
        } else {
            toolbar.navigationIcon = ContextCompat.getDrawable(this, navigationIcon)
            toolbar.visibility = VISIBLE
            toolbarReplacer.visibility = GONE
        }
    }

    override fun refreshButton(updateButtonLayout: UploadButtonLayout) {
        when (updateButtonLayout) {
            is UploadButtonLayout.ContinueLayout -> {
                onboarding_next.setText(updateButtonLayout.buttonText)
                onboarding_next.setOnClickListener {
                    updateButtonLayout.buttonListener?.invoke()
                }

                onboarding_next_secondary.visibility = GONE
            }

            is UploadButtonLayout.TwoChoiceContinueLayout -> {
                onboarding_next.setText(updateButtonLayout.primaryButtonText)
                onboarding_next.setOnClickListener {
                    updateButtonLayout.primaryButtonListener?.invoke()
                }

                onboarding_next_secondary.setText(updateButtonLayout.secondaryButtonText)
                onboarding_next_secondary.setOnClickListener {
                    updateButtonLayout.secondaryButtonListener?.invoke()
                }

                onboarding_next_secondary.visibility = VISIBLE
            }
        }
    }

    override fun onPause() {
        super.onPause()
        onboarding_next.setOnClickListener(null)
        toolbar.setNavigationOnClickListener(null)
    }

    override fun enableNextButton() {
        onboarding_next.isEnabled = true
    }

    override fun visibleConsentButton() {
        non_consent.visibility = VISIBLE
    }

    override fun invisibleConsentButton() {
        non_consent.visibility = GONE
    }

    override fun disableNextButton() {
        onboarding_next.isEnabled = false
    }

    override fun showLoading() {
        onboarding_next.showProgress {
            progressColorRes = R.color.slack_black_2
        }
    }

    override fun hideLoading(@StringRes stringRes: Int?) {
        if (stringRes == null) {
            onboarding_next.hideProgress()
        } else {
            onboarding_next.hideProgress(newTextRes = stringRes)
        }
    }
}
