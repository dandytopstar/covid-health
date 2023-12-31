package au.gov.health.covidsafe.ui.onboarding.fragment.registrationconsent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.accessibility.AccessibilityEvent
import androidx.core.text.HtmlCompat
import au.gov.health.covidsafe.R
import au.gov.health.covidsafe.talkback.setHeading
import au.gov.health.covidsafe.ui.base.PagerChildFragment
import au.gov.health.covidsafe.ui.base.PagerContainer
import au.gov.health.covidsafe.ui.base.UploadButtonLayout
import kotlinx.android.synthetic.main.fragment_registration_consent.*
import kotlinx.android.synthetic.main.fragment_registration_consent.root

class RegistrationConsentFragment : PagerChildFragment() {

    override var step: Int? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?)
            : View? = inflater.inflate(R.layout.fragment_registration_consent, container, false)

    override fun onResume() {
        super.onResume()

        registration_consent_headline.setHeading()
        registration_consent_headline.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED)

        val content = this.getString(R.string.registration_consent_content).replace("Privacy Act 1988", "<i>Privacy Act 1988</i>")
        registration_consent_text.text = HtmlCompat.fromHtml(content, HtmlCompat.FROM_HTML_MODE_LEGACY)

        updateButtonState()
    }

    override fun updateButtonState() {
        (activity as? PagerContainer)?.enableNextButton()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        root.removeAllViews()
    }

    override fun getUploadButtonLayout() = UploadButtonLayout.ContinueLayout(R.string.consent_button) {
        navigateTo(RegistrationConsentFragmentDirections.actionRegistrationConsentFragmentToPersonalDetailsFragment().actionId)
    }
}